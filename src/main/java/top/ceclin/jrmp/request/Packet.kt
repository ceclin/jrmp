package top.ceclin.jrmp.request

import sun.rmi.transport.TransportConstants
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

@JvmInline
value class Packet(val value: ByteArray)

fun Message.packet(): Packet = Packet(
    ByteArrayOutputStream(6 + value.size).also {
        with(DataOutputStream(it)) {
            writeInt(TransportConstants.Magic)
            writeShort(TransportConstants.Version.toInt())
            write(value)
        }
    }.toByteArray()
)


