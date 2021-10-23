package top.ceclin.jrmp

import sun.rmi.server.MarshalOutputStream
import top.ceclin.jrmp.request.Operation
import top.ceclin.jrmp.request.rmiCall
import java.rmi.server.ObjID

sealed interface Dgc {

    fun dirty(writeParams: MarshalOutputStream.() -> Unit): Operation

    fun clean(writeParams: MarshalOutputStream.() -> Unit): Operation
}

object DgcV1 : Dgc {

    private const val INTERFACE_HASH = -669196253586618813L

    override fun dirty(writeParams: MarshalOutputStream.() -> Unit): Operation =
        rmiCall(ObjID(ObjID.DGC_ID), INTERFACE_HASH, 1, writeParams)

    override fun clean(writeParams: MarshalOutputStream.() -> Unit): Operation =
        rmiCall(ObjID(ObjID.DGC_ID), INTERFACE_HASH, 0, writeParams)
}
