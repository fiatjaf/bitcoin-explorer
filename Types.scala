import upickle.default.{OptionReader, OptionWriter, ReadWriter => RW, macroRW}

case class Block(
    id: String,
    height: Int,
    timestamp: Long,
    tx_count: Int,
    size: Int,
    weight: Int
)

case class Transaction(
    txid: String,
    locktime: Int,
    vin: Array[TransactionInput],
    vout: Array[TransactionOutput],
    size: Int,
    weight: Int,
    status: TransactionStatus,
    fee: Long
)

case class TransactionInput(
    txid: String,
    vout: Int,
    prevout: TransactionOutput,
    scriptsig: String,
    scriptsig_asm: String,
    // witness: Option[Array[String]],
    sequence: Int
)

case class TransactionOutput(
    scriptpubkey: String,
    scriptpubkey_asm: String,
    scriptpubkey_type: String,
    // scriptpubkey_address: Option[String],
    value: Long
)

case class TransactionStatus(
    confirmed: Boolean,
    block_height: Int,
    block_hash: String,
    block_time: Long
)

object Block {
  implicit val rw: RW[Block] = macroRW
}
object Transaction {
  implicit val rw: RW[Transaction] = macroRW
}
object TransactionInput {
  implicit val rw: RW[TransactionInput] = macroRW
}
object TransactionOutput {
  implicit val rw: RW[TransactionOutput] = macroRW
}
object TransactionStatus {
  implicit val rw: RW[TransactionStatus] = macroRW
}
