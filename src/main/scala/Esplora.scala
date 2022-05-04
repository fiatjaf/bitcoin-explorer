import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import sttp.client3._

object Esplora {
  val backend = FetchBackend()

  def request(path: String) =
    basicRequest
      .get(uri"https://blockstream.info/api$path")
      .send(backend)
      .map(_.body)
}
