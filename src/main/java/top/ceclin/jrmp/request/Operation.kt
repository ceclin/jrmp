package top.ceclin.jrmp.request

import sun.rmi.server.MarshalOutputStream
import sun.rmi.transport.TransportConstants
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.rmi.server.ObjID
import java.rmi.server.UID

@JvmInline
value class Operation(val value: ByteArray)

fun rmiCall(id: ObjID, hash: Long, op: Int = -1, writeParams: MarshalOutputStream.() -> Unit = {}): Operation {
    val out = ByteArrayOutputStream().apply { write(TransportConstants.Call.toInt()) }
    val marshal = MarshalOutputStream(out)
    marshal.use {
        id.write(it)
        with(it) {
            writeInt(op)
            writeLong(hash)
            writeParams()
        }
    }
    return Operation(out.toByteArray())
}

fun rmiPing(): Operation = Operation(
    ByteArrayOutputStream().apply { write(TransportConstants.Ping.toInt()) }.toByteArray()
)

fun rmiDgcAck(uid: UID): Operation {
    val out = ByteArrayOutputStream().apply { write(TransportConstants.DGCAck.toInt()) }
    uid.write(DataOutputStream(out))
    return Operation(out.toByteArray())
}
