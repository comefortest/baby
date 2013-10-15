package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

import anorm._
import anorm.SqlParser._

import play.api._
import play.api.db._

import play.api.Play.current;

import util.CommonUtil
import util.CommonUtil._

object Pojos {
  case class Operator(var id: Long, name: String)
  def selectOperator(id: Long) = {
    val all = operators._1()
    val tmp = for (one <- all if one.id == id) yield one
    tmp.headOption
  }

  val operators = {
    def selectOperator0(id: Long) = DB.withConnection { implicit conn =>
      SQL("select * from operator where id = {id}")
        .on('id -> id)
        .apply
        .map(row => Operator(row[Long]("id"), row[String]("name")))
        .headOption
    }
    def selectOperator1 = DB.withConnection { implicit conn =>
      SQL("select * from operator")
        .apply
        .map(row => Operator(row[Long]("id"), row[String]("name")))
        .toList
    }

    var cache: List[Operator] = null;
    val OPERATOR_LOCK = new Object
    def tmp0 = CommonUtil.synchronized(OPERATOR_LOCK) {
      if (null == cache) {
        cache = selectOperator1
      }
      cache
    }
    def tmp1 = CommonUtil.synchronized(OPERATOR_LOCK) {
      cache = null
    }
    (tmp0 _, tmp1 _)
  }

  case class Supplier(
    var id: Long, supplierName: String, credit: Long,
    money: Long, linkman: String, contact: String,
    rollid: Long, createTime: Long)
  def selectSupplier = DB.withConnection { implicit conn =>
    SQL("select * from supplier order by id desc")
      .apply
      .map(row => Supplier(
        row[Long]("id"), row[String]("supplierName"),
        row[Long]("credit"), row[Long]("money"), row[String]("linkman"),
        row[String]("contact"), row[Long]("rollid"), row[Long]("createTime")))
      .toList
  }
  def selectSupplier(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from supplier where id = {id}")
      .on('id -> id)
      .apply
      .map(row => Supplier(
        row[Long]("id"), row[String]("supplierName"),
        row[Long]("credit"), row[Long]("money"), row[String]("linkman"),
        row[String]("contact"), row[Long]("rollid"), row[Long]("createTime")))
      .headOption

  }
  def hasSupplier(id: Long): Boolean = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from supplier where id = {id}")
      .on('id -> id)
      .as(scalar[Long].single) > 0
  }
  def hasSupplier(supplierName: String): Boolean = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from supplier where supplierName = {supplierName}")
      .on('supplierName -> supplierName)
      .as(scalar[Long].single) > 0
  }
  def insertSupplier(supplier: Supplier) = DB.withConnection { implicit conn =>
    import supplier._
    val id = SQL("""insert into supplier
        (supplierName, credit, money, linkman, contact, rollid, createTime)
        values
        ({supplierName}, {credit}, {money}, {linkman}, {contact}, {rollid}, {createTime})""")
      .on('supplierName -> supplierName, 'credit -> credit,
        'money -> money, 'linkman -> linkman,
        'contact -> contact, 'rollid -> rollid, 'createTime -> createTime)
      .executeInsert()
    supplier.id = id.getOrElse(0)
    supplier
  }
  def updateSupplier(supplier: Supplier) = DB.withConnection { implicit conn =>
    import supplier._
    val tmp = SQL("""update supplier 
        set 
        supplierName = {supplierName}, credit = {credit} 
        money = {money}, linkman = {linkman} 
        contact = {contact}, rollid = {rollid}, createTime = {createTime} 
        where id = {id}""")
      .on('supplierName -> supplierName, 'credit -> credit,
        'money -> money, 'linkman -> linkman,
        'contact -> contact, 'rollid -> rollid,
        'id -> id)
      .executeUpdate()
    //tmp == 1
  }
  def updateCompactedSupplier(supplier: Supplier) = DB.withConnection { implicit conn =>
    import supplier._
    val tmp = SQL("""update supplier 
        set 
        supplierName = {supplierName}, linkman = {linkman}, contact = {contact} 
        where id = {id}""")
      .on('supplierName -> supplierName, 'linkman -> linkman, 'contact -> contact, 'id -> id)
      .executeUpdate()
    //tmp == 1
  }
  def updateSupplierBill(dmoney: Long, dcredit: Long, id: Long, rollId: Long) = DB.withConnection { implicit conn =>
    val tmp = SQL("""update supplier 
        set 
        money = money + {dmoney}, credit = credit + {dcredit}, rollId = {rollId}
        where id = {id} and rollId != {rollId}""")
      .on('dmoney -> dmoney, 'dcredit -> dcredit, 'id -> id, 'rollId -> rollId)
      .executeUpdate()
    //tmp == 1
  }
  def countSupplier = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from supplier")
      .as(scalar[Long].single).toInt
  }
  val supplierForm = Form(
    mapping(
      "supplierName" -> text(minLength = 1, maxLength = 50),
      "linkman" -> text(minLength = 1, maxLength = 50),
      "contact" -> text.verifying("请正确填写联系方式, 多个号码以逗号分隔", x => x.trim.matches("""[0-9 ,.+]+""")))((supplierName, linkman, contact) => Supplier(0, supplierName.trim, 0, 0, linkman.trim, contact.trim, 0, 0))((supplier: Supplier) => Some(supplier.supplierName, supplier.linkman, supplier.contact)));

  case class Brand(var id: Long, brandName: String, createTime: Long, lastModifyTime: Long)
  def selectBrand = DB.withConnection { implicit conn =>
    SQL("select * from brand order by id desc")
      .apply
      .map(row => Brand(row[Long]("id"), row[String]("brandName"), row[Long]("createTime"), row[Long]("lastModifyTime")))
      .toList
  }
  def selectBrand(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from brand where id = {id}")
      .on('id -> id)
      .apply
      .map(row => Brand(row[Long]("id"), row[String]("brandName"), row[Long]("createTime"), row[Long]("lastModifyTime")))
      .headOption
  }
  def hasBrand(brandName: String): Boolean = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from brand where brandName = {brandName}")
      .on('brandName -> brandName)
      .as(scalar[Long].single) > 0
  }
  def countBrand = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from brand").as(scalar[Long].single)
  }
  def sampleBrand = List(Brand(1, "123", 1, 1), Brand(2, "456", 2, 2), Brand(3, "南山奶粉", 3, 3), Brand(4, "雅士利", 4, 4));
  def insertBrand(brand: Brand) = DB.withConnection { implicit conn =>
    import brand._
    val id = SQL("insert into brand(brandName, createTime, lastModifyTime)values({brandName}, {createTime}, {lastModifyTime})")
      .on('brandName -> brandName, 'createTime -> createTime, 'lastModifyTime -> lastModifyTime)
      .executeInsert()
    brand.id = id.getOrElse(0)
    brand
  }
  def updateBrand(brand: Brand) = DB.withConnection { implicit conn =>
    import brand._
    val tmp = SQL("update brand set brandName = {brandName}, lastModifyTime = {lastModifyTime} where id = {id}")
      .on('brandName -> brandName, 'lastModifyTime -> lastModifyTime, 'id -> id)
      .executeUpdate()
    //tmp == 1
  }
  val brandForm = Form(
    single("brandName" -> text(minLength = 1, maxLength = 22)));

  case class Note(var id: Long, content: String, createTime: Long, operator: Long)
  case class ReadableNote(val id: Long, val content: String, val time: String, val operator: String) {
    def this(note: Note) {
      this(note.id, note.content, CommonUtil.format(note.createTime), { val tmp1 = for (tmp <- selectOperator(note.operator)) yield tmp.name; tmp1.getOrElse("无名氏") })
    }
  }
  def sampleNote = selectNote(1024 * 1024, 5).map(x => new ReadableNote(x))
  val sampleNote0 = new ReadableNote(0, ("123456789" ** 10).toString, CommonUtil.format(System.currentTimeMillis()), "健康宝贝管理员") ** 10
  val notepadForm = Form(
    single("content" -> text(minLength = 1, maxLength = 512)));

  def selectNote = DB.withConnection { implicit conn =>
    SQL("select * from notepad order by id desc")
      .apply
      .map(row => Note(row[Long]("id"), row[String]("content"), row[Long]("createTime"), row[Long]("operator")))
      .toList
  }
  def selectNote(start: Long, count: Int) = DB.withConnection { implicit conn =>
    SQL("select * from notepad order by id desc limit {start}, {count}")
      .on('start -> start, 'count -> count)
      .apply()
      .map(row => Note(row[Long]("id"), row[String]("content"), row[Long]("createTime"), row[Long]("operator")))
      .toList
  }
  def insertNote(note: Note) = DB.withConnection { implicit conn =>
    import note._
    val id = SQL("insert into notepad(content, createTime, operator)values({content}, {createTime}, {operator})")
      .on('content -> content, 'createTime -> createTime, 'operator -> operator)
      .executeInsert()
    note.id = id.getOrElse(0)
    note
  }
  def countNote = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from notepad")
      .as(scalar[Long].single).toInt
  }
  case class Change(
    var id: Long, objectId: Long, money: Int, credit: Int,
    operator: Long, cause: Int, remark: String, opTime: Long, dataFlag: Int = Constant.DataFlag.NORMAL.id)
  def changeForm(causeVerifier: (Int => Boolean)) = Form(
    mapping(
      "money" -> number,
      "credit" -> number,
      "cause" -> number.verifying(causeVerifier),
      "remark" -> text(minLength = 0, maxLength = 50))((money, credit, cause, remark) => Change(0, 0, money, credit, 0, cause, remark.trim, 0, 0))((change: Change) => Some(change.money, change.credit, change.cause, change.remark)));

  val changeMapping =
    (row: anorm.SqlRow) => Change(
      row[Long]("id"), row[Long]("objectId"), row[Int]("money"), row[Int]("credit"),
      row[Long]("operator"), row[Int]("cause"), row[String]("remark"), row[Long]("opTime"), row[Int]("dataFlag"))
  def selectSupplierChange = DB.withConnection { implicit conn =>
    SQL("select * from supplierchange order by id desc")
      .apply
      .map(changeMapping)
      .toList
  }
  def selectSupplierChange(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from supplierchange where id = {id}")
      .on('id -> id)
      .apply
      .map(changeMapping)
      .headOption
  }
  def selectSupplierChangeForObject(objectId: Long) = DB.withConnection { implicit conn =>
    SQL("select * from supplierchange where objectId = {objectId}")
      .on('objectId -> objectId)
      .apply
      .map(changeMapping)
      .toList
  }
  def insertSupplierChange(change: Change) = DB.withConnection { implicit conn =>
    import change._
    SQL("""insert into supplierchange
        (id, objectId, money, credit, operator, remark, cause, opTime, dataFlag)
        values
        ({id}, {objectId}, {money}, {credit}, {operator}, {remark}, {cause}, {opTime}, {dataFlag})""")
      .on(
        'id -> id, 'objectId -> objectId, 'money -> money, 'credit -> credit,
        'operator -> operator, 'remark -> remark, 'cause -> cause, 'opTime -> opTime, 'dataFlag -> dataFlag)
      .executeInsert()
  }
  val supplierBillForm = changeForm(x => Constant.SupplierChangeCause.exists(_._1 == x) && x != 0)
  def selectCustomerChange = DB.withConnection { implicit conn =>
    SQL("select * from customerchange order by id desc")
      .apply
      .map(changeMapping)
      .toList
  }
  def selectCustomerChange(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from customerchange where id = {id}")
      .on('id -> id)
      .apply
      .map(changeMapping)
      .headOption
  }
  def selectCustomerChangeForObject(objectId: Long) = DB.withConnection { implicit conn =>
    SQL("select * from customerchange where objectId = {objectId}")
      .on('objectId -> objectId)
      .apply
      .map(changeMapping)
      .toList
  }
  def insertCustomerChange(change: Change) = DB.withConnection { implicit conn =>
    import change._
    SQL("""insert into customerchange
        (id, objectId, money, credit, operator, remark, cause, opTime, dataFlag)
        values
        ({id}, {objectId}, {money}, {credit}, {operator}, {remark}, {cause}, {opTime},j dataFlag)""")
      .on(
        'id -> id, 'objectId -> objectId, 'money -> money, 'credit -> credit,
        'operator -> operator, 'remark -> remark, 'cause -> cause, 'opTime -> opTime, 'dataFlag -> dataFlag)
      .executeInsert()
  }
  val customerBillForm = changeForm(x => Constant.CustomerChangeCause.exists(_._1 == x) && x != 0)
  case class Op(var id: Long, operator: Long, opType: Int, originalId: Long, opFlag: Int, logTime: Long, rollTime: Long)
  def selectOp = DB.withConnection { implicit conn =>
    SQL("select * from op order by id desc")
      .apply
      .map(row => Op(
        row[Long]("id"), row[Long]("operator"), row[Int]("opType"), row[Long]("originalId"),
        row[Int]("opFlag"), row[Long]("logTime"), row[Long]("rollTime")))
      .toList
  }

  def selectOp(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from op where id = {id}")
      .on('id -> id)
      .apply
      .map(row => Op(
        row[Long]("id"), row[Long]("operator"), row[Int]("opType"), row[Long]("originalId"),
        row[Int]("opFlag"), row[Long]("logTime"), row[Long]("rollTime")))
      .headOption
  }
  def selectNewestOp = DB.withConnection { implicit conn =>
    SQL("select * from op order by id desc limit 1")
      .apply
      .map(row => Op(
        row[Long]("id"), row[Long]("operator"), row[Int]("opType"), row[Long]("originalId"),
        row[Int]("opFlag"), row[Long]("logTime"), row[Long]("rollTime")))
      .headOption
  }
  def insertOp(op: Op) = DB.withConnection { implicit conn =>
    import op._
    val tmp = SQL("""insert into op
        (id, operator, opType, originalId, opFlag, logTime, rollTime)
        values
        ({id}, {operator}, {opType}, {originalId}, {opFlag}, {logTime}, {rollTime})""")
      .on(
        'id -> id, 'operator -> operator, 'opType -> opType, 'originalId -> originalId,
        'opFlag -> opFlag, 'logTime -> logTime, 'rollTime -> rollTime)
      .executeInsert()
    op.id = tmp.getOrElse(0)
    op
  }
  def updateOp(op: Op) = DB.withConnection { implicit conn =>
    import op._
    val tmp = SQL("""update Op 
        set 
        operator = {operator}, opType = {opType}, originalId = {originalId}, 
        opFlag = {opFlag}, logTime = {logTime}, rollTime = {rollTime} 
        where id = {id}""")
      .on('operator -> operator, 'opType -> opType, 'originalId -> originalId,
        'opFlag -> opFlag, 'logTime -> logTime, 'rollTime -> rollTime,
        'id -> id)
      .executeUpdate()
    //tmp == 1
  }
  def rollOp(id: Long, opFlag: Int, rollTime: Long) = DB.withConnection { implicit conn =>
    val tmp = SQL("""update Op 
        set 
        opFlag = {opFlag}, rollTime = {rollTime} 
        where id = {id}""")
      .on('opFlag -> opFlag, 'rollTime -> rollTime, 'id -> id)
      .executeUpdate()
    //tmp == 1
  }
  case class Fund(var id: Long, realAmount: Long, effectiveAmount: Long, customerDebt: Long, supplierDebt: Long, generalDebt: Long, rollId: Long, lastModifyTime: Long)
  def selectFund = DB.withConnection { implicit conn =>
    SQL("select * from fund")
      .apply
      .map(row => Fund(
        row[Long]("id"),
        row[Long]("realAmount"), row[Long]("effectiveAmount"),
        row[Long]("customerDebt"), row[Long]("supplierDebt"), row[Long]("generalDebt"),
        row[Long]("rollId"), row[Long]("lastModifyTime")))
      .headOption
  }
  def billFund(drealAmount: Int, deffectiveAmount: Int, dcusomerDebt: Int, dsupplierDebt: Int, dgeneralDebt: Int, rollId: Long, lastModifyTime: Long) = DB.withConnection { implicit conn =>
    val tmp = SQL("""update fund 
        set 
        realAmount = realAmount + {drealAmount}, effectiveAmount = effectiveAmount + {deffectiveAmount}, 
        customerDebt = customerDebt + {dcusomerDebt}, supplierDebt = supplierDebt + {dsupplierDebt}, generalDebt = generalDebt + {dgeneralDebt}, 
        rollId = {rollId}, lastModifyTime = {lastModifyTime}
        where rollId != {rollId}
        """)
      .on('drealAmount -> drealAmount, 'deffectiveAmount -> deffectiveAmount,
        'dcusomerDebt -> dcusomerDebt, 'dsupplierDebt -> dsupplierDebt, 'dgeneralDebt -> dgeneralDebt,
        'rollId -> rollId, 'lastModifyTime -> lastModifyTime)
      .executeUpdate()
    //tmp == 1
  }
  def selectFundChange = DB.withConnection { implicit conn =>
    SQL("select * from fundchange where dataflag = {dataflag} order by id desc")
      .on('dataflag -> Constant.DataFlag.NORMAL.id)
      .apply
      .map(changeMapping)
      .toList
  }
  def selectFundChange(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from fundchange where id = {id} and dataflag = {dataflag} order by id desc")
      .on('dataflag -> Constant.DataFlag.NORMAL.id, 'id -> { id })
      .apply
      .map(changeMapping)
      .headOption
  }
  def fundChangeForm = Form(
    mapping(
      "money" -> number,
      "credit" -> ignored(0),
      "cause" -> number.verifying(x => Constant.FundChangeCause.exists(_._1 == x) && x != 0),
      "remark" -> text(minLength = 0, maxLength = 50))((money, credit, cause, remark) => Change(0, 0, money, credit, 0, cause, remark.trim, 0, 0))((change: Change) => Some(change.money, change.credit, change.cause, change.remark)));
  def insertFundChange(change: Change) = DB.withConnection { implicit conn =>
    import change._
    SQL("""insert into fundchange
        (id, objectId, money, credit, operator, remark, cause, opTime, dataflag)
        values
        ({id}, {objectId}, {money}, {credit}, {operator}, {remark}, {cause}, {opTime}, {dataFlag})""")
      .on(
        'id -> id, 'objectId -> objectId, 'money -> money, 'credit -> credit,
        'operator -> operator, 'remark -> remark, 'cause -> cause, 'opTime -> opTime, 'dataFlag -> dataFlag)
      .executeInsert()
  }

  case class Goods(var id: Long, goodsName: String, brand: Long,
    amount: Long, unitprice: Long,
    createTime: Long, rollId: Long)
  val goodsMapping =
    (row: anorm.SqlRow) => Goods(row[Long]("id"), row[String]("goodsName"), row[Long]("brand"), row[Long]("amount"), row[Long]("unitprice"), row[Long]("createTime"), row[Long]("rollId"))
  def selectGoods = DB.withConnection { implicit conn =>
    SQL("select * from goods order by id desc")
      .apply
      .map(goodsMapping)
      .toList
  }
  def selectGoods(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from goods where id = {id}")
      .on('id -> id)
      .apply
      .map(goodsMapping)
      .headOption
  }
  def insertGoods(goods: Goods) = DB.withConnection { implicit conn =>
    import goods._
    val tmp = SQL("""insert into goods
        (goodsName, brand, amount, unitprice, createTime, rollId)
        values
        ({goodsName}, {brand}, {amount}, {unitprice}, {createTime}, {rollId})""")
      .on(
        'goodsName -> goodsName, 'brand -> brand, 'amount -> amount,
        'unitprice -> unitprice, 'createTime -> createTime, 'rollId -> rollId)
      .executeInsert()
    goods.id = tmp.getOrElse(0)
    goods
  }
  def updateGoods(id: Long, goodsName: String, brand: Int) = DB.withConnection { implicit conn =>
    val tmp = SQL("""update goods 
        set 
        goodsName = {goodsName}, brand = {brand} 
        where id = {id}""")
      .on('goodsName -> goodsName, 'brand -> brand, 'id -> id)
      .executeUpdate()
    //tmp == 1
  }
  val updateGoodsForm = Form(
    tuple(
      "goodsName" -> text(minLength = 1, maxLength = 50),
      "brand" -> number.verifying(x => selectBrand(x.toLong).size > 0)))
  val updateGoodsMetadataForm = Form(
    tuple(
      "amount" -> number.verifying(_ >= 0),
      "unitprice" -> number.verifying(_ >= 0),
      "remark" -> text(minLength = 1, maxLength = 50)))

  def updateGoodsMetadata(id: Long, amount: Long, unitprice: Long, rollId: Long) = DB.withConnection { implicit conn =>
    val tmp = SQL("""update goods 
        set 
        amount = {amount}, unitprice = {unitprice}, rollId = {rollId} 
        where id = {id} and rollId != {rollId}""")
      .on('amount -> amount, 'unitprice -> unitprice, 'id -> id, 'rollId -> rollId)
      .executeUpdate()
    //tmp == 1
  }
  def updateGoodsAmount(id: Long, damount: Long, rollId: Long) = DB.withConnection { implicit conn =>
    val tmp = SQL("""update goods 
        set 
        amount = amount + {damount}, rollId = {rollId} 
        where id = {id} and rollId != {rollId}""")
      .on('damount -> damount, 'id -> id, 'rollId -> rollId)
      .executeUpdate()
    //tmp == 1
  }
  val goodsForm = Form(
    mapping(
      "goodsName" -> text(minLength = 1, maxLength = 50),
      "brand" -> number.verifying(x => selectBrand(x.toLong).size > 0),
      "unitprice" -> number.verifying(_ >= 0))((goodsName, brand, unitprice) => Goods(0, goodsName.trim, brand, 0, unitprice, System.currentTimeMillis(), 0))((goods: Goods) => Some(goods.goodsName, goods.brand.toInt, goods.unitprice.toInt)));

  case class GoodsChange(var id: Long, goodsId: Long, amount: Long, opTime: Long, unitprice: Long, remark: String)
  val goodsChangeMapping =
    (row: anorm.SqlRow) => GoodsChange(row[Long]("id"), row[Long]("goodsId"), row[Long]("amount"), row[Long]("opTime"), row[Long]("unitprice"), row[String]("remark"))

  def insertGoodsChange(change: GoodsChange) = DB.withConnection { implicit conn =>
    import change._
    SQL("""insert into goodschange
        (id, goodsId, amount, opTime, unitprice, remark)
        values
        ({id}, {goodsId}, {amount}, {opTime}, {unitprice}, {remark})""")
      .on(
        'id -> id, 'goodsId -> goodsId, 'amount -> amount, 'opTime -> opTime,
        'unitprice -> unitprice, 'remark -> remark)
      .executeInsert()
  }
  def selectGoodsChangeForGoods(goodsId: Long) = DB.withConnection { implicit conn =>
    SQL("select * from goodschange where goodsId = {goodsId} order by id desc")
      .on('goodsId -> goodsId)
      .apply
      .map(goodsChangeMapping)
      .toList
  }
  def selectGoodsChange(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from goodschange where id = {id}")
      .on('id -> id)
      .apply
      .map(goodsChangeMapping)
      .headOption
  }
  case class Circulation(transactionId: Long, goodsId: Long, amount: Int, unitprice: Int)
  val circulationMapping =
    (row: anorm.SqlRow) => Circulation(row[Long]("transactionId"), row[Long]("goodsId"),
      row[Long]("amount").toInt, row[Long]("unitprice").toInt)
  def insertCirculation(circulation: Circulation) = DB.withConnection { implicit conn =>
    import circulation._
    SQL("""insert into circulation
        (transactionId, goodsId, amount, unitprice)
        values
        ({transactionId}, {goodsId}, {amount}, {unitprice})""")
      .on(
        'transactionId -> circulation.transactionId, 'goodsId -> circulation.goodsId, 'amount -> circulation.amount, 'unitprice -> circulation.unitprice)
      .executeInsert()
  }
  def selectCirculation(transactionId: Long) = DB.withConnection { implicit conn =>
    SQL("select * from circulation where transactionId = {transactionId}")
      .on('transactionId -> transactionId)
      .apply
      .map(circulationMapping)
      .toList
  }
  case class Transaction(var id: Long, objectId: Long, operator: Long, opTime: Long,
    comment: String, dataFlag: Int, calctotalprice: Int, totalprice: Int,
    actualmoney: Int, paycredit: Int, rewardcredit: Int, originalId: Long)
  val transactionMapping =
    (row: anorm.SqlRow) => Transaction(row[Long]("id"), row[Long]("objectId"), row[Long]("operator"), row[Long]("opTime"),
      row[String]("comment"), row[Int]("dataFlag"), row[Long]("calctotalprice").toInt, row[Long]("totalprice").toInt,
      row[Long]("actualmoney").toInt, row[Long]("paycredit").toInt, row[Long]("rewardcredit").toInt, row[Long]("originalId"))

  def selectBuy(start: Long, count: Int) = DB.withConnection { implicit conn =>
    SQL("select * from buy order by id desc limit {start}, {count}")
      .on('start -> start, 'count -> count)
      .apply
      .map(transactionMapping)
      .toList
  }
  def selectBuy = DB.withConnection { implicit conn =>
    SQL("select * from buy order by id desc")
      .apply
      .map(transactionMapping)
      .toList
  }
  def selectBuy(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from buy where id = {id}")
      .on('id -> id)
      .apply
      .map(transactionMapping)
      .headOption
  }
  def selectSell(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from sell where id = {id}")
      .on('id -> id)
      .apply
      .map(transactionMapping)
      .headOption
  }
  def countBuy = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from buy")
      .as(scalar[Long].single).toInt
  }
  def insertBuy(trans: Transaction, circulations: List[Circulation]) = DB.withConnection { implicit conn =>
    val op1 = Op(0, trans.operator, Constant.OpType.BUY.id, trans.originalId, Constant.OpFlag.INITIALIZED.id, System.currentTimeMillis(), 0)
    val op = insertOp(op1)
    for (circulation <- circulations) {
      insertCirculation(circulation.copy(transactionId = op.id))
    }
    trans.id = op.id
    import trans._
    SQL("""insert into buy
        (id, objectId, operator, opTime, comment, dataFlag, 
        calctotalprice, totalprice, actualmoney, paycredit, rewardcredit, originalId)
        values
        ({id}, {objectId}, {operator}, {opTime}, {comment}, {dataFlag}, 
        {calctotalprice}, {totalprice}, {actualmoney}, {paycredit}, {rewardcredit}, {originalId})""")
      .on(
        'id -> id, 'objectId -> objectId, 'operator -> operator, 'opTime -> opTime, 'comment -> comment, 'dataFlag -> dataFlag,
        'calctotalprice -> calctotalprice, 'totalprice -> totalprice, 'actualmoney -> actualmoney, 'paycredit -> paycredit, 'rewardcredit -> rewardcredit, 'originalId -> originalId)
      .executeInsert()
    trans
  }
  def updateBuyDataFlag(id: Long, dataFlag: Int) = DB.withConnection { implicit conn =>
    val tmp = SQL("""update buy 
        set 
        dataFlag = {dataFlag} 
        where id = {id}""")
      .on('dataFlag -> dataFlag, 'id -> id)
      .executeUpdate()
    //tmp == 1
  }
  def updateSellDataFlag(id: Long, dataFlag: Int) = DB.withConnection { implicit conn =>
    val tmp = SQL("""update sell 
        set 
        dataFlag = {dataFlag} 
        where id = {id}""")
      .on('dataFlag -> dataFlag, 'id -> id)
      .executeUpdate()
    //tmp == 1
  }
  def selectSell = DB.withConnection { implicit conn =>
    SQL("select * from sell order by id desc")
      .apply
      .map(transactionMapping)
      .toList
  }
  def countSell = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from sell")
      .as(scalar[Long].single).toInt
  }
  def insertSell(trans: Transaction, circulations: List[Circulation]) = DB.withConnection { implicit conn =>
    val op1 = Op(0, trans.operator, Constant.OpType.SELL.id, trans.originalId, Constant.OpFlag.INITIALIZED.id, System.currentTimeMillis(), 0)
    val op = insertOp(op1)
    for (circulation <- circulations) {
      insertCirculation(circulation.copy(transactionId = op.id))
    }
    trans.id = op.id
    import trans._
    SQL("""insert into sell
        (id, objectId, operator, opTime, comment, dataFlag, 
        calctotalprice, totalprice, actualmoney, paycredit, rewardcredit, originalId)
        values
        ({id}, {objectId}, {operator}, {opTime}, {comment}, {dataFlag}, 
        {calctotalprice}, {totalprice}, {actualmoney}, {paycredit}, {rewardcredit}, {originalId})""")
      .on(
        'id -> id, 'objectId -> objectId, 'operator -> operator, 'opTime -> opTime, 'comment -> comment, 'dataFlag -> dataFlag,
        'calctotalprice -> calctotalprice, 'totalprice -> totalprice, 'actualmoney -> actualmoney, 'paycredit -> paycredit, 'rewardcredit -> rewardcredit, 'originalId -> originalId)
      .executeInsert()
    trans
  }
  def transactionForm(msg: String, x: Int => Boolean) = Form(
    mapping(
      "objectId" -> number.verifying(msg, x),
      "paycredit" -> number.verifying("使用积分必须是自然数", _ >= 0),
      "rewardcredit" -> number.verifying("奖励积分必须是自然数", _ >= 0),
      "totalprice" -> number.verifying("应付价格必须是自然数, 以分为单位", _ >= 0),
      "actualmoney" -> number.verifying("实付价格必须是自然数, 以分为单位", _ >= 0),
      "comment" -> text(minLength = 0, maxLength = 500),
      "circulations" -> seq(
        mapping(
          "goodsId" -> number.verifying("商品不存在", x => selectGoods(x.toLong).size > 0),
          "amount" -> number.verifying("商品数量必须是自然数", _ > 0),
          //"unitprice" -> number.verifying("商品单价必须是自然数, 以分为单位", _ > 0))((x, y, z) => Circulation(0, x, y, z))((c: Circulation) => Option(c.goodsId.toInt, c.amount, c.unitprice)))) //case class Transaction(var id: Long, objectId: Long, operator: Long, opTime: Long,
          "unitprice" -> ignored(0))((x, y, z) => Circulation(0, x, y, z))((c: Circulation) => Option(c.goodsId.toInt, c.amount, c.unitprice)))) //case class Transaction(var id: Long, objectId: Long, operator: Long, opTime: Long,
          ((objectId, paycredit, rewardcredit, totalprice, actualmoney, comment, circulations) => (Transaction(0, objectId, 0, System.currentTimeMillis(), comment.trim, Constant.DataFlag.NORMAL.id, 0, totalprice, actualmoney, paycredit, rewardcredit, 0), circulations))((x: (models.Pojos.Transaction, Seq[models.Pojos.Circulation])) => {
        val (trans, circulations) = x
        Option(trans.objectId.toInt, trans.paycredit.toInt, trans.rewardcredit.toInt, trans.totalprice.toInt, trans.actualmoney.toInt, trans.comment, circulations)
      })).fill(Transaction(0, 0, 0, 0, "", Constant.DataFlag.UNKNOWN_DATA_FLAG.id, 0, 0, 0, 0, 0, 0), Seq());
  val buyForm = transactionForm("供货商不存在", x => hasSupplier(x.toLong))
  val sellForm = transactionForm("客户不存在", x => 0 == x || hasCustomer(x.toLong))
  case class Customer(var id: Long, name: String, sex: Int,
    birthyear: Int, birthmonth: Int, birthdate: Int, money: Int, credit: Int, mobile: String, rollId: Long)
  val customerMapping =
    (row: anorm.SqlRow) => Customer(row[Long]("id"), row[String]("name"), row[Int]("sex"),
      row[Int]("birthyear"), row[Int]("birthmonth"), row[Int]("birthdate"),
      row[Long]("money").toInt, row[Long]("credit").toInt, row[String]("mobile"),
      row[Long]("rollId"))
  def selectCustomer(id: Long) = DB.withConnection { implicit conn =>
    SQL("select * from customer where id = {id}")
      .on('id -> id)
      .apply
      .map(customerMapping)
      .headOption
  }
  def hasCustomer(id: Long): Boolean = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from customer where id = {id}")
      .on('id -> id)
      .as(scalar[Long].single) > 0
  }
  def selectCustomer = DB.withConnection { implicit conn =>
    SQL("select * from customer order by id desc")
      .apply
      .map(customerMapping)
      .toList
  }
  def selectCustomer(liked: String) = DB.withConnection { implicit conn =>
    SQL("select * from customer where name like {liked} or mobile like {liked} order by id desc")
      .on('liked -> liked)
      .apply
      .map(customerMapping)
      .toList
  }
  def selectCustomer(start: Long, count: Int) = DB.withConnection { implicit conn =>
    SQL("select * from customer order by id desc limit {start}, {count}")
      .on('start -> start, 'count -> count)
      .apply()
      .map(customerMapping)
      .toList
  }
  def selectCustomer(liked: String, start: Long, count: Int) = DB.withConnection { implicit conn =>
    SQL("select * from customer where name like {liked} or mobile like {liked} order by id desc limit {start}, {count}")
      .on('start -> start, 'count -> count, 'liked -> liked)
      .apply()
      .map(customerMapping)
      .toList
  }
  def countCustomer = DB.withConnection { implicit conn =>
    SQL("select count(1) as count1 from customer")
      .as(scalar[Long].single).toInt
  }
  def insertCustomer(customer: Customer) = DB.withConnection { implicit conn =>
    import customer._
    val id = SQL("""insert into customer
        (name, sex, birthyear, birthmonth, birthdate, money, credit, mobile, rollId)
        values
        ({name}, {sex}, {birthyear}, {birthmonth}, {birthdate}, {money}, {credit}, {mobile}, {rollId})""")
      .on('name -> name, 'sex -> sex,
        'birthyear -> birthyear, 'birthmonth -> birthmonth, 'birthdate -> birthdate,
        'money -> money, 'credit -> credit, 'mobile -> mobile,
        'rollId -> rollId)
      .executeInsert()
    customer.id = id.getOrElse(0)
    customer
  }
  def updateCompactedCustomer(customer: Customer) = DB.withConnection { implicit conn =>
    import customer._
    val tmp = SQL("""update customer 
        set 
        name = {name}, sex = {sex}, mobile = {mobile} 
        where id = {id}""")
      .on('name -> name, 'sex -> sex, 'mobile -> mobile, 'id -> id)
      .executeUpdate()
    //tmp == 1
  }
  def updateCustomerBill(dmoney: Long, dcredit: Long, id: Long, rollId: Long) = DB.withConnection { implicit conn =>
    val tmp = SQL("""update customer 
        set 
        money = money + {dmoney}, credit = credit + {dcredit}, rollId = {rollId}
        where id = {id} and rollId != {rollId}""")
      .on('dmoney -> dmoney, 'dcredit -> dcredit, 'id -> id, 'rollId -> rollId)
      .executeUpdate()
    //tmp == 1
  }
  val customerForm = Form(
    mapping(
      "name" -> text(minLength = 1, maxLength = 50),
      "sex" -> number,
      "mobile" -> text.verifying("请正确填写联系方式, 多个号码以逗号分隔", x => x.trim.matches("""[0-9 ,.+]+""")))((name, sex, mobile) => Customer(0, name.trim, sex, 0, 0, 0, 0, 0, mobile.trim, 0))((customer: Customer) => Some(customer.name, customer.sex, customer.mobile)));

  def test = {}

}