package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

import TodoCategory._
case class TodoCategory(
  id:        Option[Id],
  name:      String,
  slug:      String,
  color:     Color,
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

object TodoCategory {
  val  Id = the[Identity[Id]]
  type Id = Long @@ TodoCategory
  type WithNoId = Entity.WithNoId [Id, TodoCategory]
  type EmbeddedId = Entity.EmbeddedId[Id, TodoCategory]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class Color(val code: Short, val name: String) extends EnumStatus
  object Color extends EnumStatus.Of[Color] {
    case object RED    extends Color(code = 1, name = "red")
    case object GREEN  extends Color(code = 2, name = "green")
    case object BLUE   extends Color(code = 3, name = "blue")
    case object YELLOW extends Color(code = 4, name = "yellow")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(name: String, slug: String, color: Color): WithNoId = {
    new Entity.WithNoId(
      new TodoCategory(
        id    = None,
        name  = name,
        slug  = slug,
        color = color
      )
    )
  }
}
