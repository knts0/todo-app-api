package json.writes

import model.item.TodoList
import play.api.libs.json.{Json, Writes}

case class JsValueTodoListItem(
  id:       Long,
  title:    String,
  body:     String,
  state:    JsValueState,
  category: Option[JsValueTodoCategoryItem]
)

object JsValueTodoListItem {
  implicit val writes: Writes[JsValueTodoListItem] = Json.writes[JsValueTodoListItem]

  def apply(v: TodoList): JsValueTodoListItem =
    JsValueTodoListItem(
      id       = v.todo.id,
      title    = v.todo.title,
      body     = v.todo.body,
      state    = JsValueState.apply(v.todo.state),
      category = v.category.map(category => JsValueTodoCategoryItem.apply(category))
    )
}
