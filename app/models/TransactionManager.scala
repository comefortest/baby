package models

import Constant._
import util.CommonUtil

object TransactionManager {
  def wrap[T](f: => T) = CommonUtil.synchronized(this) {
    clean
    f
  }
  private def clean {
    for (op <- Pojos.selectNewestOp; opFlag = OpFlag(op.opFlag)) {
      opFlag match {
        case Constant.OpFlag.INITIALIZED => {
          OpType(op.opType) match {
            case OpType.CHANGE_SUPPLIER_BILL => {
              Pojos.selectSupplierChange(op.id) match {
                case Some(change) => {
                  Pojos.selectSupplier(change.objectId) match {
                    case Some(supplier) => {
                      if (supplier.rollid != op.id) {
                        Pojos.updateSupplierBill(change.money, change.credit, supplier.id, op.id)
                        Pojos.billFund(-change.money, -change.money, 0, 0, 0, op.id, op.logTime)
                      }
                      Pojos.rollOp(op.id, OpFlag.COMMITTED.id, System.currentTimeMillis())
                    }
                    case None => {
                      Pojos.rollOp(op.id, OpFlag.INVALIDATE.id, System.currentTimeMillis())
                    }
                  }
                }
                case None => {
                  Pojos.rollOp(op.id, OpFlag.INVALIDATE.id, System.currentTimeMillis())
                }
              }
            }
            case OpType.CHANGE_CUSTOMER_BILL => {
              Pojos.selectCustomerChange(op.id) match {
                case Some(change) => {
                  Pojos.selectCustomer(change.objectId) match {
                    case Some(customer) => {
                      if (customer.rollId != op.id) {
                        Pojos.updateCustomerBill(change.money, change.credit, customer.id, op.id)
                        Pojos.billFund(-change.money, -change.money, 0, 0, 0, op.id, op.logTime)
                      }
                      Pojos.rollOp(op.id, OpFlag.COMMITTED.id, System.currentTimeMillis())
                    }
                    case None => {
                      Pojos.rollOp(op.id, OpFlag.INVALIDATE.id, System.currentTimeMillis())
                    }
                  }
                }
                case None => {
                  Pojos.rollOp(op.id, OpFlag.INVALIDATE.id, System.currentTimeMillis())
                }
              }
            }
            case OpType.CHANGE_FUND_BILL => {
              Pojos.selectFundChange(op.id) match {
                case Some(change) => {
                  Pojos.selectFund match {
                    case Some(fund) => {
                      if (fund.rollId != op.id) {
                        if (Constant.LEND == change.cause || Constant.RETURN == change.cause) {
                          Pojos.billFund(change.money, 0, 0, 0, change.money, op.id, op.logTime)
                        } else {
                          Pojos.billFund(change.money, change.money, 0, 0, 0, op.id, op.logTime)
                        }
                      }
                      Pojos.rollOp(op.id, OpFlag.COMMITTED.id, System.currentTimeMillis())
                    }
                    case None => {
                      Pojos.rollOp(op.id, OpFlag.INVALIDATE.id, System.currentTimeMillis())
                    }
                  }
                }
                case None => {
                  Pojos.rollOp(op.id, OpFlag.INVALIDATE.id, System.currentTimeMillis())
                }
              }
            }
            case OpType.CHANGE_GOODS_METADATA => {
              Pojos.selectGoodsChange(op.id) match {
                case Some(change) => {
                  Pojos.updateGoodsMetadata(change.goodsId, change.amount, change.unitprice, op.id);
                  Pojos.rollOp(op.id, OpFlag.COMMITTED.id, System.currentTimeMillis())
                }
                case None => {
                  Pojos.rollOp(op.id, OpFlag.INVALIDATE.id, System.currentTimeMillis())
                }
              }
            }
            case OpType.BUY => {
              Pojos.selectBuy(op.id) match {
                case Some(buy) => {
                  //update goods amount
                  if (0 == buy.originalId) {
                    Pojos.billFund(-buy.actualmoney, -buy.totalprice, 0, buy.totalprice - buy.actualmoney, 0, op.id, op.logTime)
                    for (c <- Pojos.selectCirculation(buy.id)) {
                      Pojos.updateGoodsAmount(c.goodsId, c.amount, buy.id)
                    }
                    Pojos.updateSupplierBill(0, buy.rewardcredit - buy.paycredit, buy.objectId, buy.id)
                  } else {
                    val original = Pojos.selectBuy(buy.originalId)
                    val (drealAmount, deffectiveAmount, dsupplierDebt, circulations, dcredit) =
                      if (original.isDefined) {
                        Pojos.updateBuyDataFlag(buy.originalId, Constant.DataFlag.DELETED.id);
                        val tmp0 = for (one <- Pojos.selectCirculation(original.get.id)) yield one.copy(amount = -one.amount)
                        val tmp1 = Pojos.selectCirculation(buy.id)
                        val compressed = for (group <- (tmp0 ++ tmp1).groupBy(x => x.goodsId))
                          yield group._2.foldRight(group._2.head.copy(amount = 0))((x, y) => y.copy(amount = y.amount + x.amount))

                        (original.get.actualmoney - buy.actualmoney,
                          original.get.totalprice - buy.totalprice,
                          -original.get.totalprice + original.get.actualmoney + buy.totalprice - buy.actualmoney,
                          compressed,
                          buy.rewardcredit - buy.paycredit - original.get.rewardcredit + original.get.paycredit)
                      } else {
                        (-buy.actualmoney,
                          -buy.totalprice,
                          buy.totalprice - buy.actualmoney, Pojos.selectCirculation(buy.id), buy.rewardcredit - buy.paycredit)
                      }
                    Pojos.billFund(drealAmount, deffectiveAmount, 0, dsupplierDebt, 0, op.id, op.logTime)
                    for (c <- circulations) {
                      Pojos.updateGoodsAmount(c.goodsId, c.amount, buy.id)
                    }
                    Pojos.updateSupplierBill(0, dcredit, buy.objectId, buy.id)
                  }
                  Pojos.rollOp(op.id, OpFlag.COMMITTED.id, System.currentTimeMillis())
                }
                case None => {
                  Pojos.rollOp(op.id, OpFlag.INVALIDATE.id, System.currentTimeMillis())
                }
              }
            }
            case OpType.SELL => {
              Pojos.selectSell(op.id) match {
                case Some(sell) => {
                  //update goods amount
                  if (0 == sell.originalId) {
                    Pojos.billFund(sell.actualmoney, sell.totalprice, sell.totalprice - sell.actualmoney, 0, 0, op.id, op.logTime)
                    for (c <- Pojos.selectCirculation(sell.id)) {
                      Pojos.updateGoodsAmount(c.goodsId, -c.amount, sell.id)
                    }
                    Pojos.updateCustomerBill(0, sell.rewardcredit - sell.paycredit, sell.objectId, sell.id)
                  } else {
                    val original = Pojos.selectBuy(sell.originalId)
                    val (drealAmount, deffectiveAmount, dcustomerDebt, circulations, dcredit) =
                      if (original.isDefined) {
                        Pojos.updateSellDataFlag(sell.originalId, Constant.DataFlag.DELETED.id);
                        val tmp0 = Pojos.selectCirculation(original.get.id)
                        val tmp1 = for (one <- Pojos.selectCirculation(sell.id)) yield one.copy(amount = -one.amount)
                        val compressed = for (group <- (tmp0 ++ tmp1).groupBy(x => x.goodsId))
                          yield group._2.foldRight(group._2.head.copy(amount = 0))((x, y) => y.copy(amount = y.amount + x.amount))

                        (sell.actualmoney - original.get.actualmoney,
                          sell.totalprice - original.get.totalprice,
                          -original.get.totalprice + original.get.actualmoney + sell.totalprice - sell.actualmoney,
                          compressed,
                          sell.rewardcredit - sell.paycredit - original.get.rewardcredit + original.get.paycredit)
                      } else {
                        (sell.actualmoney,
                          sell.totalprice,
                          sell.totalprice - sell.actualmoney,
                          for (one <- Pojos.selectCirculation(sell.id)) yield one.copy(amount = -one.amount),
                          sell.rewardcredit - sell.paycredit)
                      }
                    Pojos.billFund(drealAmount, deffectiveAmount, dcustomerDebt, 0, 0, op.id, op.logTime)
                    for (c <- circulations) {
                      Pojos.updateGoodsAmount(c.goodsId, c.amount, sell.id)
                    }
                  }
                  Pojos.rollOp(op.id, OpFlag.COMMITTED.id, System.currentTimeMillis())
                }
                case None => {
                  Pojos.rollOp(op.id, OpFlag.INVALIDATE.id, System.currentTimeMillis())
                }
              }
            }
          }
        }
        case Constant.OpFlag.COMMITTED =>
        case Constant.OpFlag.INVALIDATE =>
        case x => throw new Exception("FATAL ERROR -> " + x)
      }
    }
  }
}