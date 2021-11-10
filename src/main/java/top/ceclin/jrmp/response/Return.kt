package top.ceclin.jrmp.response

import sun.rmi.server.LoaderHandler
import sun.rmi.transport.TransportConstants
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.InputStream
import java.io.ObjectInputStream
import java.rmi.server.UID

class Return(val isSuccess: Boolean, val stream: ObjectInputStream) {
    companion object {
        fun decode(packet: ByteArray): Return {
            val input = DataInputStream(ByteArrayInputStream(packet))
            if (input.readByte() != TransportConstants.Return)
                throw IllegalArgumentException("not a return packet")
            val marshal = MarshalInputStream(input)
            val type = marshal.readByte()
            UID.read(marshal)
            return when (type) {
                TransportConstants.NormalReturn -> Return(true, marshal)
                TransportConstants.ExceptionalReturn -> Return(false, marshal)
                else -> throw IllegalArgumentException("unknown return type: $type")
            }
        }
    }

    val isFailure: Boolean = !isSuccess
}

private class MarshalInputStream(input: InputStream) : sun.rmi.server.MarshalInputStream(input) {
    override fun resolveProxyClass(interfaces: Array<String>): Class<*> = kotlin.runCatching {
        super.resolveProxyClass(interfaces)
    }.getOrElse {
        System.err.println("resolveProxyClass failed. Use fallback. Cause: $it")
        LoaderHandler.loadProxyClass(
            null,
            arrayOf("java.rmi.Remote"),
            Thread.currentThread().contextClassLoader,
        )
    }

    override fun readLocation() =
        super.readLocation()?.also { println("JRMP: location=$it") }
}
