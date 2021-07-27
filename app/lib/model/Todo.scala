/** This is a sample of Todo Application.
  */

package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// TODOを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import Todo._
//Point 以下、データ用にはcase classで、同じ名前のTodoのコンパニオンオブジェクト化されているところを上手く表現して使えているか？
case class Todo(
    //Point すごい細かいですが、弊社のインデントルールに沿って書いているか？を確認 インデント警察
    id:          Option[Id],
    //Point Long型ではなく、「TodoCategory.Id」型で有ることを確認！ 
    categoryId:  TodoCategory.Id,
    title:       String,
    body:        String,
    state:       Status,
    updatedAt:   LocalDateTime = NOW,
    createdAt:   LocalDateTime = NOW
) extends EntityModel[Id] 
//EntityModelWithNoTimeRecも存在
//updatedAt、createdAtが別にいらないテーブルは使う

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object Todo {

  val Id = the[Identity[Id]]
  type Id = Long @@ Todo
  type WithNoId = Entity.WithNoId[Id, Todo]
  type EmbeddedId = Entity.EmbeddedId[Id, Todo]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  // 1:TODO    (着手前)
  // 2:PROGRESS(進行中)
  // 3:END     (完了)
  sealed abstract class Status(val code: Short, val name: String)
      extends EnumStatus
  //Point EnumStatusを以下の様にちゃんと設定できているか？　※インデント警察もお願いします。
  object Status extends EnumStatus.Of[Status] {
    case object TODO     extends Status(code = 0, name = "着手前")
    case object PROGRESS extends Status(code = 1, name = "進行中")
    case object END      extends Status(code = 2, name = "終了")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(
      //Point ここも「TodoCategory.Id」型であることを確認
      categoryId:  TodoCategory.Id,
      title:       String,
      body:        String,
      state:       Status
  ): WithNoId = {
    new Entity.WithNoId(
      new Todo(
        id         = None,
        categoryId = categoryId,
        title      = title,
        body       = body,
        state      = state
      )
    )
  }
}
