package json.writes

import play.api.libs.json.{Json, Writes}

case class JsValueTodoItem(
  id:         Long,
  title:      String,
  body:       String,
  state:      Short,
  categoryId: Long
)

object JsValueTodoItem {
  implicit val writes: Writes[JsValueTodoItem] = Json.writes[JsValueTodoItem]
}
