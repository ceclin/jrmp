package example.basic

import top.ceclin.jrmp.RegistryV2
import top.ceclin.jrmp.request.Request
import top.ceclin.jrmp.request.request
import top.ceclin.jrmp.request.singleOp
import top.ceclin.jrmp.response.Return
import java.net.Socket
import java.rmi.registry.Registry

fun main() {
    Socket("127.0.0.1", Registry.REGISTRY_PORT).use {
        it.tcpNoDelay = true
        val request: Request = RegistryV2.list().singleOp().request()
        println(request.value.contentToString())
        it.outputStream.write(request.value)
        val bytes = it.inputStream.readBytes()
        val ret = Return.decode(bytes)
        val result = ret.stream.readObject() as Array<*>
        println(result.contentToString())
    }
}
