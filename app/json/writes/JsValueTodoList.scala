package json.writes

import model.item.TodoList
import play.api.libs.json.{Json, Writes}

case class JsValueTodoList(
  todoList: Seq[JsValueTodoListItem]
)

object JsValueTodoList {
  implicit val writes: Writes[JsValueTodoList] = Json.writes[JsValueTodoList]

  def apply(seq: Seq[TodoList]): JsValueTodoList =
    JsValueTodoList(
      todoList = seq.map(v => JsValueTodoListItem.apply(v))
    )
}
