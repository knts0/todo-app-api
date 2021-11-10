package json.writes

import play.api.libs.json.{Json, Writes}

case class JsValueTodoCategoryItem(
  id:    Long,
  name:  String,
  slug:  String,
  color: Short
)

object JsValueTodoCategoryItem {
  implicit val writes: Writes[JsValueTodoCategoryItem] = Json.writes[JsValueTodoCategoryItem]
}
