package models

object Constant {
  object OpType extends Enumeration {
    val UNKNOWN_OP_TYPE, SELL, BUY, CHANGE_GOODS, CHANGE_GOODS_METADATA, CHANGE_SUPPLIER_BILL, CHANGE_CUSTOMER_BILL, CHANGE_FUND_BILL = Value
  }
  object OpFlag extends Enumeration {
    val UNKNOWN_OP_FLAG, INITIALIZED, CONFIRMED, INVALIDATE, COMMITTED = Value
  }

  object DataFlag extends Enumeration {
    val UNKNOWN_DATA_FLAG, NORMAL, DELETED = Value
  }

  object SexFlag extends Enumeration {
    val UNKNOWN_SEX_FLAG, MALE, FEMALE = Value
  }
  def sex(flag: Int) = {
    SexFlag(flag) match {
      case SexFlag.MALE => "男";
      case SexFlag.FEMALE => "女";
      case _ => "未知";
    }
  }

  val SupplierChangeCause = Map(
    0 -> "未知原因",
    RETURN -> "还款给供货商",
    3 -> "预付款",
    OTHER -> "其他")

  val CustomerChangeCause = Map(
    0 -> "未知原因",
    RETURN -> "客户还款给店铺",
    OTHER -> "其他")

  val LEND = 1
  val RETURN = 2
  val OTHER = 1000

  val FundChangeCause = Map(
    0 -> "未知原因",
    LEND -> "从店铺借款",
    RETURN -> "还款给店铺",
    3 -> "房租",
    4 -> "税费",
    5 -> "注资",
    6 -> "水费",
    7 -> "电费",
    8 -> "购置或维修物品",
    OTHER -> "其他");

  val Navigator = List(
    "customer" -> ("客 户", true),
    "sell" -> ("售 货", true),
    "sellhistory" -> ("售货历史", true),
    "goods" -> ("商品/库存", true),
    "buy" -> ("进 货", true),
    "buyhistory" -> ("进货历史", true),
    "brand" -> ("品 牌", true),
    "supplier" -> ("供货商", true),
    "notepad" -> ("记事本", true),
    "history" -> ("店铺日志", false),
    "fund" -> ("账务总揽", true),
    "tmp" -> ("测试页面", false))
  val NavigatorMap = {
    var tmp = Map[String, String]()
    for (one <- Navigator) {
      tmp = tmp + (one._1 -> one._2._1)
    }
    tmp
  }
  def title(nav: String) = NavigatorMap.get(nav) match {
    case Some(x) => x + "| 恭喜发财 | 健康宝贝";
    case _ => "欢迎 | 恭喜发财 | 健康宝贝"
  }

  object ResponseStatus extends Enumeration {
    val UNKNOWN_RESPONSE_STATUS, SUCCESS_RESPONSE_STATUS, FAILED_RESPONSE_STEATUS = Value
  }

}