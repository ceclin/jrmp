package top.ceclin.jrmp

import sun.rmi.server.MarshalOutputStream
import top.ceclin.jrmp.ext.jrmpHash
import top.ceclin.jrmp.request.Operation
import top.ceclin.jrmp.request.rmiCall
import java.rmi.Remote
import java.rmi.server.ObjID

sealed interface Registry {

    fun lookup(writeParams: MarshalOutputStream.() -> Unit): Operation

    fun bind(writeParams: MarshalOutputStream.() -> Unit): Operation

    fun unbind(writeParams: MarshalOutputStream.() -> Unit): Operation

    fun rebind(writeParams: MarshalOutputStream.() -> Unit): Operation

    fun list(): Operation
}

object RegistryV1 : Registry {

    private const val INTERFACE_HASH = 4905912898345647071L

    override fun lookup(writeParams: MarshalOutputStream.() -> Unit): Operation =
        rmiCall(ObjID(ObjID.REGISTRY_ID), INTERFACE_HASH, 2, writeParams)

    override fun bind(writeParams: MarshalOutputStream.() -> Unit): Operation =
        rmiCall(ObjID(ObjID.REGISTRY_ID), INTERFACE_HASH, 0, writeParams)

    override fun unbind(writeParams: MarshalOutputStream.() -> Unit): Operation =
        rmiCall(ObjID(ObjID.REGISTRY_ID), INTERFACE_HASH, 4, writeParams)

    override fun rebind(writeParams: MarshalOutputStream.() -> Unit): Operation =
        rmiCall(ObjID(ObjID.REGISTRY_ID), INTERFACE_HASH, 3, writeParams)

    override fun list(): Operation =
        rmiCall(ObjID(ObjID.REGISTRY_ID), INTERFACE_HASH, 1)
}

object RegistryV2 : Registry {

    override fun lookup(writeParams: MarshalOutputStream.() -> Unit): Operation = rmiCall(
        ObjID(ObjID.REGISTRY_ID),
        java.rmi.registry.Registry::class.java.getDeclaredMethod("lookup", String::class.java).jrmpHash,
        writeParams = writeParams,
    )

    override fun bind(writeParams: MarshalOutputStream.() -> Unit): Operation {
        val method = java.rmi.registry.Registry::class.java.getDeclaredMethod(
            "bind",
            String::class.java, Remote::class.java,
        )
        return rmiCall(ObjID(ObjID.REGISTRY_ID), method.jrmpHash, writeParams = writeParams)
    }

    override fun unbind(writeParams: MarshalOutputStream.() -> Unit): Operation = rmiCall(
        ObjID(ObjID.REGISTRY_ID),
        java.rmi.registry.Registry::class.java.getDeclaredMethod("unbind", String::class.java).jrmpHash,
        writeParams = writeParams,
    )

    override fun rebind(writeParams: MarshalOutputStream.() -> Unit): Operation {
        val method = java.rmi.registry.Registry::class.java.getDeclaredMethod(
            "rebind",
            String::class.java, Remote::class.java,
        )
        return rmiCall(ObjID(ObjID.REGISTRY_ID), method.jrmpHash, writeParams = writeParams)
    }

    override fun list(): Operation = rmiCall(
        ObjID(ObjID.REGISTRY_ID),
        java.rmi.registry.Registry::class.java.getDeclaredMethod("list").jrmpHash,
    )
}


