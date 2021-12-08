package json.reads

import play.api.libs.json.{Json, Reads}

case class JsValueUpdateCategory(
  name:  String,
  slug:  String,
  color: Short
)

object JsValueUpdateCategory {
  implicit val reads: Reads[JsValueUpdateCategory] = Json.reads[JsValueUpdateCategory]
}
