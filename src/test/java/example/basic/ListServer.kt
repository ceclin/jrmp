package example.basic

import top.ceclin.jrmp.RegistryV2
import top.ceclin.jrmp.request.Packet
import top.ceclin.jrmp.request.packet
import top.ceclin.jrmp.request.singleOp
import top.ceclin.jrmp.response.Return
import java.net.Socket
import java.rmi.registry.Registry

fun main() {
    Socket("127.0.0.1", Registry.REGISTRY_PORT).use {
        it.tcpNoDelay = true
        val packet: Packet = RegistryV2.list().singleOp().packet()
        println(packet.value.contentToString())
        it.outputStream.write(packet.value)
        val bytes = it.inputStream.readBytes()
        val ret = Return.decode(bytes)
        val result = ret.stream.readObject() as Array<*>
        println(result.contentToString())
    }
}
