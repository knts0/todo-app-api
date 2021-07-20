package forms

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class TodoAddData(title: String, body: String, categoryId: Long)
case class TodoEditData(title: String, body: String, state: Int, categoryId: Long)

object TodoForm {
  def add(): Form[TodoAddData] = {
    Form(
      mapping(
        "title"      -> nonEmptyText,
        "body"       -> nonEmptyText,
        "categoryId" -> longNumber(min = 1)
      )(TodoAddData.apply)(TodoAddData.unapply)
    )
  }

  def edit(): Form[TodoEditData] = {
    Form(
      mapping(
        "title"      -> nonEmptyText,
        "body"       -> nonEmptyText,
        "state"      -> number,
        "categoryId" -> longNumber(min = 1)
      )(TodoEditData.apply)(TodoEditData.unapply)
    )
  }
}
