package json.writes

import model.item.TodoItem
import play.api.libs.json.{Json, Writes}

case class JsValueTodo(
  id:         Long,
  title:      String,
  body:       String,
  state:      Short,
  categoryId: Long
)

object JsValueTodo {
  implicit val writes: Writes[JsValueTodo] = Json.writes[JsValueTodo]

  def apply(v: TodoItem): JsValueTodo =
    JsValueTodo(
      id         = v.id,
      title      = v.title,
      body       = v.body,
      state      = v.state.code,
      categoryId = v.categoryId,
    )
}
