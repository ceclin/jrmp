package top.ceclin.jrmp.ext

import org.joor.Reflect
import sun.rmi.transport.LiveRef
import sun.rmi.transport.tcp.TCPEndpoint

val LiveRef.tcpEndpoint: TCPEndpoint
    get() = Reflect.on(this).get("ep")
