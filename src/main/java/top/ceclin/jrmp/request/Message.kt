package top.ceclin.jrmp.request

import sun.rmi.transport.TransportConstants
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

@JvmInline
value class Message(val value: ByteArray)

fun Operation.singleOp(): Message = Message(
    ByteArrayOutputStream(1 + value.size).also {
        with(DataOutputStream(it)) {
            writeByte((TransportConstants.SingleOpProtocol.toInt()))
            write(value)
        }
    }.toByteArray()
)

/**
 * [host] and [port] are read and discarded in server, so they are useless.
 */
fun Operation.stream(host: String = "127.0.0.1", port: Int = 0xABCD): Message = Message(
    ByteArrayOutputStream(16 + value.size).also {
        with(DataOutputStream(it)) {
            writeByte((TransportConstants.StreamProtocol.toInt()))
            writeUTF(host)
            writeInt(port)
            write(value)
        }
    }.toByteArray()
)

class MultiplexBuilder(host: String, port: Int) {

    companion object {
        private const val OPEN = 0xE1
        private const val CLOSE = 0xE2
        private const val CLOSE_ACK = 0xE3
        private const val REQUEST = 0xE4
        private const val TRANSMIT = 0xE5
    }

    private val b = ByteArrayOutputStream()

    private val d = DataOutputStream(b)

    init {
        d.writeByte((TransportConstants.MultiplexProtocol.toInt()))
        d.writeUTF(host)
        d.writeInt(port)
    }

    fun open(id: Int) {
        require(id in 0..0xFFFF)
        d.writeByte(OPEN)
        d.writeShort(id)
    }

    fun close(id: Int) {
        require(id in 0..0xFFFF)
        d.writeByte(CLOSE)
        d.writeShort(id)
    }

    fun closeAck(id: Int) {
        require(id in 0..0xFFFF)
        d.writeByte(CLOSE_ACK)
        d.writeShort(id)
    }

    fun request(id: Int, len: Int) {
        require(id in 0..0xFFFF)
        d.writeByte(REQUEST)
        d.writeShort(id)
        d.writeInt(len)
    }

    fun transmit(id: Int, operation: Operation) {
        transmit(id, operation, 0, operation.value.size)
    }

    fun transmit(id: Int, operation: Operation, off: Int, len: Int) {
        val data = operation.value
        require(id in 0..0xFFFF)
        d.writeByte(TRANSMIT)
        d.writeShort(id)
        d.writeInt(len)
        d.write(data, off, len)
    }

    fun build(): Message = Message(b.toByteArray())
}

fun buildMultiplex(host: String, port: Int, action: MultiplexBuilder.() -> Unit): Message =
    MultiplexBuilder(host, port).apply(action).build()


