package util

import util.JavaUtil._

object CommonUtil {
  implicit val formator = new java.text.SimpleDateFormat("yyyy年MM月dd日 HH时mm分 E")
  def format(time: Long) = formator.format(new java.util.Date(time));
  implicit val formator1 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
  def format1(time: Long) = formator1.format(new java.util.Date(time));
  implicit def now = formator.format(new java.util.Date())

  implicit val decimalformator = new java.text.DecimalFormat("#,###0.00");
  def format(d: Double) = decimalformator format d

  class PowWrapper[T](seed: T) {
    def **(pow: Int) = {
      var tmp = List[T]()
      for (i <- 1 to pow) {
        tmp = seed :: tmp
      }
      tmp
    }
  }

  implicit def powWrapper[T](seed: T) = new PowWrapper(seed)

  def synchronized[T](lock: AnyRef)(x: => T) = {
    val worker: Worker[T] = new Worker[T] {
      def work = x
    }
    sync(lock, worker);
  }

  import views.html.helper._
  implicit val textareaField0 = new FieldConstructor {
    def apply(elts: FieldElements) = views.html.textareaFieldConstructor0(elts)
  }
  implicit val textareaField1 = new FieldConstructor {
    def apply(elts: FieldElements) = views.html.textareaFieldConstructor1(elts)
  }

  def pager(pageCount: Int, pageIndex: Int, factor: Int) = {
    pageIndex match {
      case x: Int if x <= 3 => {
        1 to Math.min(factor, pageCount)
      }
      case x: Int if x >= pageCount - 4 => {
        Math.max(pageCount - factor + 1, 1) to pageCount
      }
      case x: Int => {
        (x - 2) to (x + 3)
      }
    }
  }

  def toInt(raw: String, default: Int): Int = raw match {
    case null => default
    case x if 0 == raw.trim.length => default
    case x => try { x.toInt } catch { case _: Throwable => default }
  }
  def toInt(raw: String): Int = toInt(raw, 0)
  def toLong(raw: String, default: Long): Long = raw match {
    case null => default
    case x if 0 == raw.trim.length => default
    case x => try { x.toLong } catch { case _: Throwable => default }
  }

  def toLong(raw: String): Long = toLong(raw, 0)

  def params(implicit request: play.api.mvc.Request[play.api.mvc.AnyContent]) = {
    val params1: collection.mutable.Map[String, Seq[String]] = collection.mutable.Map()
    params1 ++= request.body.asFormUrlEncoded.getOrElse[Map[String, Seq[String]]] { Map.empty }
    params1 ++= request.queryString
    params1
  }

  def clean(raw: String) = {
    if (null == raw) {
      ""
    } else {
      val under = '_'
      raw.trim.replace(' ', under).replace('"', under).replace(''', under)
    }
  }
}