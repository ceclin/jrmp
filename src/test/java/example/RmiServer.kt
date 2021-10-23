package example

import java.rmi.Remote
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.rmi.server.UnicastRemoteObject

fun main() {
    val registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT)
    registry.bind("ccl", UnicastRemoteObject.exportObject(object : Remote {
        fun foo() = "bar"
    }, 0))
    System.`in`.read()
}
