import org.scalajs.dom
import com.raquo.laminar.api.L._

class TransactionView(
    $txid: Signal[Option[String]],
    $txhex: Signal[Option[String]],
    $tx: Signal[Option[Transaction]]
) {
  enum View:
    case Hex
    case Details
  end View

  val showing = Var(View.Details)

  def render(): HtmlElement =
    div(
      onMountCallback(ctx => ctx.thisNode.ref.scrollIntoView(true)),
      styleAttr := "width: 625px",
      cls := "p-8 whitespace-pre-wrap break-all",
      child.maybe <-- $txid.map {
        _.map { txid =>
          div(
            span(cls := "font-bold", s"transaction $txid"),
            child.maybe <-- $tx.map {
              _.map { tx =>
                div(
                  cls.toggle("hidden") <-- showing.signal.map(
                    _ != View.Details
                  ),
                  div(
                    cls := "cursor-pointer underline",
                    onClick.preventDefault.map(_ =>
                      View.Hex
                    ) --> showing.writer,
                    "see hex"
                  ),
                  div(cls := "break-words", "fee paid: ", tx.fee.toString),
                  div(cls := "break-words", "locktime: ", tx.locktime)
                )
              }
            },
            child.maybe <-- $txhex.map {
              _.map { txhex =>
                div(
                  cls.toggle("hidden") <-- showing.signal.map(_ != View.Hex),
                  div(
                    cls := "cursor-pointer underline",
                    onClick.preventDefault.map(_ =>
                      View.Details
                    ) --> showing.writer,
                    "see details"
                  ),
                  div(cls := "font-mono break-all", txhex)
                )
              }
            }
          )
        }
      }
    )
}
