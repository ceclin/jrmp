package example.basic

import sun.rmi.server.UnicastRef
import top.ceclin.jrmp.RegistryV2
import top.ceclin.jrmp.ext.tcpEndpoint
import top.ceclin.jrmp.request.Packet
import top.ceclin.jrmp.request.packet
import top.ceclin.jrmp.request.singleOp
import top.ceclin.jrmp.response.Return
import java.lang.reflect.Proxy
import java.net.Socket
import java.rmi.registry.Registry
import java.rmi.server.RemoteObject

fun main() {
    Socket("127.0.0.1", Registry.REGISTRY_PORT).use {
        it.tcpNoDelay = true
        val packet: Packet = RegistryV2.lookup {
            writeObject("ccl")
        }.singleOp().packet()
        println(packet.value.contentToString())
        it.outputStream.write(packet.value)
        val bytes = it.inputStream.readBytes()
        val ret = Return.decode(bytes)
        val result = ret.stream.readObject()
        println(result)
        val remote = (result.takeUnless { Proxy.isProxyClass(it.javaClass) }
            ?: Proxy.getInvocationHandler(result)) as RemoteObject
        val ref = remote.ref as UnicastRef
        val tcpEndpoint = ref.liveRef.tcpEndpoint
        println(tcpEndpoint)
    }
}
