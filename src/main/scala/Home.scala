import org.scalajs.dom
import com.raquo.laminar.api.L._
import upickle.default._
import urldsl.language.PathSegment.simplePathErrorImpl._
import scodec.bits.ByteVector

object Home {
  val $tick = EventStream.periodic(1000)
  val $time = $tick.map(_ => System.currentTimeMillis() / 1000)

  val $hash = $tick.filter(_ % 30 == 0)
    .flatMap(_ =>
      EventStream
        .fromFuture(Esplora.request("/blocks/tip/hash"))
        .map(_.toOption)
    )
    .toSignal(None)

  val $block: Signal[Option[Block]] = $hash.changes
    .filter(_.isDefined)
    .map(_.get)
    .flatMap(hash =>
      EventStream
        .fromFuture(Esplora.request(s"/block/$hash"))
        .map {
          case Right(b) => Some(read[Block](b))
          case _        => None
        }
    )
    .toSignal(None)

  val $timeSinceLast = EventStream
    .combine($time, $block.changes)
    .map((time, block) => time - block.map(_.timestamp).getOrElse(0L))
    .map(s =>
      if s < 60 then s"$s s"
      else if s < 120 then s"1 minute and ${s - 60} s"
      else s"${s / 60} minutes and ${s % 60} s"
    )

  val $txs = $block.changes
    .filter(_.isDefined)
    .map(_.get)
    .flatMap(block =>
      EventStream
        .fromFuture(Esplora.request(s"/block/${block.id}/txids"))
        .map {
          case Right(b) => read[List[String]](b)
          case _        => List.empty[String]
        }
    )
    .toSignal(List.empty[String])

  val txidClick = new EventBus[String]

  val $txid = txidClick.events
    .map(Some(_))
    .toSignal(
      (root / "tx" / segment[String])
        .matchPath(dom.window.location.pathname)
        .toOption
    )

  val $txhex = $txid.changes
    .filter(x => x.isDefined)
    .map(_.get)
    .flatMap(txid =>
      EventStream
        .fromFuture(Esplora.request(s"/tx/$txid/hex"))
        .map(_.toOption)
    )
    .toSignal(None)

  val $tx = $txid.changes
    .filter(x => x.isDefined)
    .map(_.get)
    .flatMap(txid =>
      EventStream
        .fromFuture(Esplora.request(s"/tx/$txid"))
        .map {
          case Right(b) => Some(read[Transaction](b))
          case _        => None
        }
    )
    .toSignal(None)

  def render(): HtmlElement =
    div(
      new TransactionView($txid, $txhex, $tx).render(),
      div(
        cls := "inline-flex",
        div(
          cls := "p-8 w-80",
          div(
            cls := "font-bold",
            "block ",
            child.text <-- $block.map(_.map(_.height.toString).getOrElse("_"))
          ),
          div(
            cls := "w-full overflow-hidden text-ellipsis",
            child.text <-- $block.map(_.map(_.id).getOrElse("_"))
          ),
          div(
            "mined ",
            child.text <-- $timeSinceLast,
            " ago"
          )
        ),
        div(
          cls := "p-8",
          span(cls := "font-bold", "transactions"),
          div(
            children <-- $txs.map(
              _.map(txid =>
                div(
                  cls := "cursor-pointer font-mono",
                  href := s"/tx/$txid",
                  onClick.preventDefault.map(_ => txid) --> txidClick,
                  txid
                )
              )
            )
          )
        )
      )
    )

  println(scoin.Crypto.sha256(ByteVector(1, 2, 3, 4, 5, 6)))
}
