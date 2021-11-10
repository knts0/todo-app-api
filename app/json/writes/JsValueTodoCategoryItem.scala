package json.writes

import model.item.TodoCategoryItem
import play.api.libs.json.{Json, Writes}

case class JsValueTodoCategoryItem(
  id:    Long,
  name:  String,
  slug:  String,
  color: Short
)

object JsValueTodoCategoryItem {
  implicit val writes: Writes[JsValueTodoCategoryItem] = Json.writes[JsValueTodoCategoryItem]

  def apply(category: TodoCategoryItem): JsValueTodoCategoryItem =
    JsValueTodoCategoryItem(
      id    = category.id,
      name  = category.name,
      slug  = category.slug,
      color = category.color.code
    )
}
