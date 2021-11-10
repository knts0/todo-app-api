package json.writes

import play.api.libs.json.{Json, Writes}

case class JsValueTodoListItem(
  todo:     JsValueTodoItem,
  category: Option[JsValueTodoCategoryItem]
)

object JsValueTodoListItem {
  implicit val writes: Writes[JsValueTodoListItem] = Json.writes[JsValueTodoListItem]
}
