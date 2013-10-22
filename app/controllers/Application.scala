package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import _root_.util._
import _root_.util.CommonUtil._
import models.Constant._
import models.Pojos._
import models.Pojos
import models.Constant
import models.AppWrapper
import models.TransactionManager
import views.html.supplier_insert
import views.html.fund_history

object Application extends Controller {

  def mytodo(title: String, nav: String) = Action {
    Ok(views.html.todo(title, nav))
  }
  def mytodo(nav: String): Action[AnyContent] = {
    mytodo(title(nav), nav)
  }
  def index = Action {
    import views.html.helper.twitterBootstrap.twitterBootstrapField
    Ok(views.html.index("欢迎 | 恭喜发财 | 健康宝贝"))
  }

  def goods = Action { implicit request =>
    Ok(views.html.goods("goods", Pojos.selectGoods, x => {
      Pojos.selectBrand(x) match {
        case Some(brand) => brand.brandName
        case None => "未知品牌"
      }
    }))
  }
  val brandResolver = (x: Long) => {
    Pojos.selectBrand(x) match {
      case Some(brand) => brand.brandName
      case None => "未知品牌"
    }
  }
  def insertGoods = Action { implicit request =>
    Ok(views.html.goods_insert("goods", goodsForm))
  }
  def insertGoods1 = Action { implicit request =>
    goodsForm.bindFromRequest.fold(
      errors => BadRequest(views.html.goods_insert("goods", errors)),
      content => {
        val id = Pojos.insertGoods(content).id
        Ok(views.html.transfer("goods", "添加商品成功", routes.Application.showGoods(id).url))
      })
  }
  def showGoods(id: Long) = Action { implicit request =>
    val (goods, changes) = TransactionManager.wrap {
      (Pojos.selectGoods(id), Pojos.selectGoodsChangeForGoods(id))
    }
    Ok(views.html.goods_detail("goods", goods.get, changes, brandResolver))
  }
  def updateGoods(id: Long) = Action { implicit request =>
    val goods = TransactionManager.wrap {
      Pojos.selectGoods(id)
    }.get
    Ok(views.html.goods_update("goods", goods, updateGoodsForm.fill((goods.goodsName, goods.brand.toInt)), brandResolver))
  }
  def updateGoods1(id: Long) = Action { implicit request =>
    val goods = Pojos.selectGoods(id).get
    updateGoodsForm.bindFromRequest.fold(
      errors => BadRequest(views.html.goods_update("goods", goods, errors, brandResolver)),
      content => {
        Pojos.updateGoods(id, content._1.trim, content._2)
        Ok(views.html.transfer("supplier", "更改商品成功", routes.Application.showGoods(id).url))
      })
  }
  def updateGoodsMetadata(id: Long) = Action { implicit request =>
    val goods = TransactionManager.wrap {
      Pojos.selectGoods(id)
    }.get
    Ok(views.html.goods_updatemetadata("goods", goods, updateGoodsMetadataForm.fill((goods.amount.toInt, goods.unitprice.toInt, ""))))
  }
  def updateGoodsMetadata1(id: Long) = Action { implicit request =>
    val goods = Pojos.selectGoods(id).get
    updateGoodsMetadataForm.bindFromRequest.fold(
      errors => BadRequest(views.html.goods_updatemetadata("goods", goods, errors)),
      content => {
        TransactionManager.wrap {
          val op = Pojos.insertOp(Op(0, AppWrapper.operator, Constant.OpType.CHANGE_GOODS_METADATA.id, 0, Constant.OpFlag.INITIALIZED.id, System.currentTimeMillis(), 0));
          val change = GoodsChange(op.id, id, content._1, op.logTime, content._2, content._3.trim)
          Pojos.insertGoodsChange(change)
        }
        Ok(views.html.transfer("supplier", "更改商品成功", routes.Application.showGoods(id).url))
      })
  }
  def sellhistory(customerId: Long, goodsId: Long) = Action { implicit request =>
    val transactions = TransactionManager.wrap {
      val tmp = Pojos.selectSell
      for (x <- tmp) yield (x, Pojos.selectCirculation(x.id))
    }
    val count = countSell
    val factor = 10
    val pagerCount = count / factor + (if (0 < count % factor) 1 else 0)
    val pagerIndex = toInt(request.getQueryString(INDEX_KEY).getOrElse(null), 1)
    Ok(views.html.sell_history("sellhistory", transactions,
      x => x match {
        case 0 => (0, "未知客户")
        case x => (x, Pojos.selectCustomer(x).get.name)
      },
      x => Pojos.selectGoods(x).get,
      pagerIndex, pagerCount, pagerPrefix(request)))
  }
  def sell = Action { implicit request =>
    val goodses = TransactionManager.wrap {
      Pojos.selectGoods
    }
    Ok(views.html.sell("sell", goodses, sellForm, 0, "未知客户"))
  }
  def customerName(id: Long) = {
    if (0 == id) {
      "未知客户"
    } else {
      Pojos.selectCustomer(id) match {
        case Some(x) => x.name
        case _ => "未知客户"
      }
    }
  }
  def sell1 = Action { implicit request =>
    sellForm.bindFromRequest.fold(
      errors => {
        val goodses = TransactionManager.wrap {
          Pojos.selectGoods
        }
        val objectId = toLong(params.apply("objectId").headOption.getOrElse("0"))
        BadRequest(views.html.sell("sell", goodses, errors, objectId, customerName(objectId)))
      },
      content => {
        TransactionManager.wrap {
          //TODO unitprice should be filled
          Pojos.insertSell(content._1.copy(operator = AppWrapper.operator, calctotalprice = calc(content._2).toInt, originalId = 0), fillunitprice(content._2).toList)
        }
        Ok(views.html.transfer("sell", "销售成功", routes.Application.sell.url))
      })
  }
  def buyhistory(customerId: Long, goodsId: Long) = Action { implicit request =>
    val transactions = TransactionManager.wrap {
      val tmp = Pojos.selectBuy
      for (x <- tmp) yield (x, Pojos.selectCirculation(x.id))
    }
    val count = countSell
    val factor = 10
    val pagerCount = count / factor + (if (0 < count % factor) 1 else 0)
    val pagerIndex = toInt(request.getQueryString(INDEX_KEY).getOrElse(null), 1)
    Ok(views.html.buy_history("buyhistory", transactions, x => Pojos.selectSupplier(x).get, x => Pojos.selectGoods(x).get, pagerIndex, pagerCount, pagerPrefix(request)))
  }
  def buy = Action { implicit request =>
    val (suppliers, goodses) = TransactionManager.wrap {
      (Pojos.selectSupplier, Pojos.selectGoods)
    }
    Ok(views.html.buy("buy", goodses, for (x <- suppliers) yield (x.id.toString, x.supplierName + ", 积分: " + x.credit), buyForm))
  }
  def calc(circulations: Seq[Circulation]) = {
    circulations.foldLeft(0: Long)(
      (x, y) => {
        x + y.amount * Pojos.selectGoods(y.goodsId).get.unitprice
      })
  }
  def fillunitprice(circulations: Seq[models.Pojos.Circulation]) = {
    val compressed = for (group <- circulations.groupBy(x => x.goodsId))
      yield group._2.foldRight(group._2.head.copy(amount = 0))((x, y) => y.copy(amount = y.amount + x.amount))
    for (c <- compressed; goodsId = c.goodsId)
      yield c.copy(unitprice = Pojos.selectGoods(goodsId).get.unitprice.toInt)
  }
  def buy1 = Action { implicit request =>
    buyForm.bindFromRequest.fold(
      errors => {
        val (suppliers, goodses) = TransactionManager.wrap {
          (Pojos.selectSupplier, Pojos.selectGoods)
        }
        BadRequest(views.html.buy("buy", goodses, for (x <- suppliers) yield (x.id.toString, x.supplierName + ", 积分: " + x.credit), errors))
      },
      content => {
        TransactionManager.wrap {
          //TODO unitprice should be filled
          Pojos.insertBuy(content._1.copy(operator = AppWrapper.operator, calctotalprice = calc(content._2).toInt, originalId = 0), fillunitprice(content._2).toList)
        }
        Ok(views.html.transfer("buy", "进货成功", routes.Application.buy.url))
      })
  }
  def brand = Action { implicit request =>
    Ok(views.html.brand("brand", brandForm, selectBrand))
  }
  def insertBrand = Action { implicit request =>

    brandForm.bindFromRequest.fold(
      errors => BadRequest(views.html.brand("brand", errors, selectBrand, true)),
      content => {
        val id = Pojos.insertBrand(Brand(0, content.trim, System.currentTimeMillis(), System.currentTimeMillis())).id
        Ok(views.html.brand("brand", brandForm, selectBrand, true))
      })
  }
  def updateBrand: play.api.mvc.Action[play.api.mvc.AnyContent] =
    Action.async { implicit request =>
      val params = CommonUtil.params
      val brandId = toLong(params("brandId").headOption.getOrElse(""))
      val brandName = params("brandName").headOption.getOrElse("");
      updateBrand(brandId, brandName).apply(request)
    }
  def updateBrand(brandId: Long, brandName: String): play.api.mvc.Action[play.api.mvc.AnyContent] =
    Action { implicit request =>
      val brandName1 = CommonUtil.clean(brandName);
      val json =
        if (brandId <= 0 || 0 >= CommonUtil.clean(brandName).length()) {
          Json.obj("status" -> JsNumber(ResponseStatus.FAILED_RESPONSE_STEATUS.id), "msg" -> JsString("品牌名称填写错误"), "brandId" -> JsNumber(brandId), "brandName" -> JsString(brandName))
        } else if (Pojos.hasBrand(brandName)) {
          Json.obj("status" -> JsNumber(ResponseStatus.FAILED_RESPONSE_STEATUS.id), "msg" -> JsString("品牌名称已经存在"))
        } else {
          Pojos.updateBrand(Brand(brandId, brandName, 0, System.currentTimeMillis()))
          Json.obj("status" -> JsNumber(ResponseStatus.SUCCESS_RESPONSE_STATUS.id))
        }
      Ok(Json.stringify(json))
    }

  /**********supplier**********/
  def supplier = Action { implicit request =>
    Ok(views.html.supplier("supplier", Pojos.selectSupplier))
  }
  def insertSupplier = Action { implicit request =>
    Ok(views.html.supplier_insert("supplier", supplierForm))
  }
  def insertSupplier1 = Action { implicit request =>
    supplierForm.bindFromRequest.fold(
      errors => BadRequest(views.html.supplier_insert("supplier", errors)),
      content => {
        val id = Pojos.insertSupplier(Supplier(0, content.supplierName, 0, 0, content.linkman, content.contact, 0, System.currentTimeMillis())).id
        Ok(views.html.transfer("supplier", "添加供货商成功", routes.Application.showSupplier(id).url))
      })
  }
  def updateSupplier(id: Long) = Action { implicit request =>
    val supplier = Pojos.selectSupplier(id).get
    val original = supplierForm.fill(supplier)
    Ok(views.html.supplier_update("supplier", original, supplier))
  }
  def updateSupplier1(id: Long) = Action { implicit request =>
    val supplier = Pojos.selectSupplier(id).get
    supplierForm.bindFromRequest.fold(
      errors => BadRequest(views.html.supplier_update("supplier", errors, supplier)),
      content => {
        Pojos.updateCompactedSupplier(Supplier(id, content.supplierName, 0, 0, content.linkman, content.contact, 0, System.currentTimeMillis()))
        Ok(views.html.transfer("supplier", "编辑供货商成功", routes.Application.showSupplier(id).url))
      })
  }
  def billSupplier(id: Long) = Action { implicit request =>
    val (supplier, changes) = TransactionManager.wrap {
      (Pojos.selectSupplier(id), Pojos.selectSupplierChangeForObject(id))
    }
    Ok(views.html.supplier_bill("supplier", supplier.get, changes, supplierBillForm))
  }
  def billSupplier1(id: Long) = Action { implicit request =>
    supplierBillForm.bindFromRequest.fold(
      errors => {
        val (supplier, changes) = TransactionManager.wrap {
          (Pojos.selectSupplier(id), Pojos.selectSupplierChangeForObject(id))
        }
        BadRequest(views.html.supplier_bill("supplier", supplier.get, changes, errors))
      },
      content => {
        TransactionManager.wrap {
          val op = Pojos.insertOp(Op(0, AppWrapper.operator, Constant.OpType.CHANGE_SUPPLIER_BILL.id, 0, Constant.OpFlag.INITIALIZED.id, System.currentTimeMillis(), 0));
          val change = content.copy(id = op.id, operator = op.operator, opTime = op.logTime, objectId = id, dataFlag = DataFlag.NORMAL.id)
          Pojos.insertSupplierChange(change)
        }
        Ok(views.html.transfer("supplier", "变更供货商账务成功", routes.Application.billSupplier(id).url))
      })
  }
  def showSupplier(id: Long) = Action { implicit request =>
    val (supplier, changes) = TransactionManager.wrap {
      (Pojos.selectSupplier(id), Pojos.selectSupplierChangeForObject(id))
    }
    Ok(views.html.supplier_detail("supplier", supplier.get, changes))
  }
  /**********supplier**********/

  /**********customer**********/
  def customer(liked: Option[String]) = Action { implicit request =>
    val (select, all, liked1) = liked match {
      case Some(x) if 0 <= _root_.util.CommonUtil.clean(x).length => (Pojos.selectCustomer(_root_.util.CommonUtil.clean(x), _: Long, _: Int), false, _root_.util.CommonUtil.clean(x))
      case _ => (Pojos.selectCustomer(_: Long, _: Int), true, "")
    }
    val count = countCustomer
    val factor = 10
    val index = pagerIndex
    Ok(views.html.customer("customer", select((index - 1) * factor, factor), index, pagerCount(count, factor), pagerPrefix, all, liked1))
  }
  def customerSnippet(liked: Option[String]) = Action { implicit request =>
    val (select, all) = liked match {
      case Some(x) => (Pojos.selectCustomer(x, _: Long, _: Int), false)
      case None => (Pojos.selectCustomer(_: Long, _: Int), true)
    }
    val count = countCustomer
    val factor = 10
    val pagerCount = count / factor + (if (0 < count % factor) 1 else 0)
    val pagerIndex = toInt(request.getQueryString(INDEX_KEY).getOrElse(null), 1)
    Ok(views.html.customer_snippet(select((pagerIndex - 1) * factor, factor)))
  }
  def insertCustomer = Action { implicit request =>
    Ok(views.html.customer_insert("customer", customerForm))
  }
  def insertCustomer1 = Action { implicit request =>
    customerForm.bindFromRequest.fold(
      errors => BadRequest(views.html.customer_insert("customer", errors)),
      content => {
        val id = Pojos.insertCustomer(content).id
        Ok(views.html.transfer("customer", "添加客户成功", routes.Application.showCustomer(id).url))
      })
  }
  def updateCustomer(id: Long) = Action { implicit request =>
    val customer = Pojos.selectCustomer(id).get
    val original = customerForm.fill(customer)
    Ok(views.html.customer_update("customer", original, customer))
  }
  def updateCustomer1(id: Long) = Action { implicit request =>
    val customer = Pojos.selectCustomer(id).get
    customerForm.bindFromRequest.fold(
      errors => BadRequest(views.html.customer_update("customer", errors, customer)),
      content => {
        Pojos.updateCompactedCustomer(content.copy(id = id))
        Ok(views.html.transfer("customer", "编辑客户成功", routes.Application.showCustomer(id).url))
      })
  }
  def billCustomer(id: Long) = Action { implicit request =>
    val (customer, changes) = TransactionManager.wrap {
      (Pojos.selectCustomer(id), Pojos.selectCustomerChangeForObject(id))
    }
    Ok(views.html.customer_bill("customer", customer.get, changes, customerBillForm))
  }
  def billCustomer1(id: Long) = Action { implicit request =>
    customerBillForm.bindFromRequest.fold(
      errors => {
        val (customer, changes) = TransactionManager.wrap {
          (Pojos.selectCustomer(id), Pojos.selectCustomerChangeForObject(id))
        }
        BadRequest(views.html.customer_bill("customer", customer.get, changes, errors))
      },
      content => {
        TransactionManager.wrap {
          val op = Pojos.insertOp(Op(0, AppWrapper.operator, Constant.OpType.CHANGE_CUSTOMER_BILL.id, 0, Constant.OpFlag.INITIALIZED.id, System.currentTimeMillis(), 0));
          val change = content.copy(id = op.id, operator = op.operator, opTime = op.logTime, objectId = id, dataFlag = DataFlag.NORMAL.id)
          Pojos.insertCustomerChange(change)
        }
        Ok(views.html.transfer("customer", "变更客户账务成功", routes.Application.billCustomer(id).url))
      })
  }
  def showCustomer(id: Long) = Action { implicit request =>
    val (customer, changes) = TransactionManager.wrap {
      (Pojos.selectCustomer(id), Pojos.selectCustomerChangeForObject(id))
    }
    Ok(views.html.customer_detail("customer", customer.get, changes))
  }
  /**********customer**********/

  /**********pager**********/
  val INDEX_KEY = "index"
  def pagerPrefix(implicit request: Request[_]) = request.path + "?" +
    (for (one <- request.queryString; one1 <- one._2 if INDEX_KEY != one._1) yield one._1 + "=" + one1).mkString("&") +
    "&" + INDEX_KEY + "="
  def pagerIndex(implicit request: Request[_]) = toInt(request.getQueryString(INDEX_KEY).getOrElse(null), 1)
  @inline def pagerCount(count: Int, factor: Int) = count / factor + (if (0 < count % factor) 1 else 0)
  /**********pager**********/

  def notepad = Action { implicit request =>
    val count = countNote
    val factor = 5
    val pagerCount = count / factor + (if (0 < count % factor) 1 else 0)
    val pagerIndex = toInt(request.getQueryString(INDEX_KEY).getOrElse(null), 1)
    Ok(views.html.notepad("notepad", notepadForm, selectNote((pagerIndex - 1) * factor, factor).map(x => new ReadableNote(x)), pagerIndex, pagerCount, pagerPrefix(request)))
  }
  def insertNotepad = Action { implicit request =>
    val count = countNote
    val factor = 5
    val pagerCount = count / factor + (if (0 < count % factor) 1 else 0)
    val pagerIndex = toInt(request.getQueryString(INDEX_KEY).getOrElse(null), 1)

    notepadForm.bindFromRequest.fold(
      errors => BadRequest(views.html.notepad("notepad", errors, selectNote((pagerIndex - 1) * factor, factor).map(x => new ReadableNote(x)), pagerIndex, pagerCount, pagerPrefix(request))),
      content => {
        val id = insertNote(Note(0, content.trim, System.currentTimeMillis(), 0)).id
        Ok(views.html.notepad("notepad", notepadForm, selectNote((pagerIndex - 1) * factor, factor).map(x => new ReadableNote(x)), pagerIndex, pagerCount, pagerPrefix(request)))
      })
  }
  def history = mytodo("history")
  def fund = Action { implicit request =>
    Ok(views.html.fund("fund", TransactionManager.wrap { Pojos.selectFund.get }))
  }
  def fundhistory = Action { implicit request =>
    val changes = TransactionManager.wrap {
      Pojos.selectFundChange
    }
    Ok(views.html.fund_history("fund", changes))
  }
  def billFund = Action { implicit request =>
    Ok(views.html.fund_bill("fund", fundChangeForm))
  }
  def billFund1 = Action { implicit request =>
    fundChangeForm.bindFromRequest.fold(
      errors => {
        Ok(views.html.fund_bill("fund", errors))
      },
      content => {
        TransactionManager.wrap {
          val op = Pojos.insertOp(Op(0, AppWrapper.operator, Constant.OpType.CHANGE_FUND_BILL.id, 0, Constant.OpFlag.INITIALIZED.id, System.currentTimeMillis(), 0));
          val change = content.copy(id = op.id, operator = op.operator, opTime = op.logTime, objectId = 0, dataFlag = DataFlag.NORMAL.id)
          Pojos.insertFundChange(change)
        }
        Ok(views.html.transfer("supplier", "变更店铺账务成功", routes.Application.fund.url))
      })
  }
  def tmp = Action {
    Ok(views.html.tmp())
  }

  def time = Action {
    val json = Json.obj("now" -> JsString(now))
    Ok(Json.stringify(json))
  }

  def img(file: String) = controllers.Assets.at("/public/images/", file);

}