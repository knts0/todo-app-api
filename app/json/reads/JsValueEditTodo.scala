package json.reads

import play.api.libs.json.{Json, Reads}

case class JsValueEditTodo(
  title:      String,
  body:       String,
  state:      Short,
  categoryId: Long
)

object JsValueEditTodo {
  implicit val reads: Reads[JsValueEditTodo] = Json.reads[JsValueEditTodo]
}

