package json.writes

import lib.model.Todo
import play.api.libs.json.{Json, Writes}

case class JsValueState (
  code: Short,
  name: String
)

object JsValueState {
  implicit val writes: Writes[JsValueState] = Json.writes[JsValueState]

  def apply(v: Todo.Status): JsValueState =
    JsValueState(
      code = v.code,
      name = v.name
    )
}

