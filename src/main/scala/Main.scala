import org.scalajs.dom
import com.raquo.laminar.api.L._

object Main {
  val app = div(
    cls := "p-4",
    h1("bitcoin explorer"),
    child <-- Val(Home.render())
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ =>
      render(dom.document.getElementById("main"), app)
    }(unsafeWindowOwner)
  }
}
