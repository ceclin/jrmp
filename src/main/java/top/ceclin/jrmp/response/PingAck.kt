package top.ceclin.jrmp.response

import sun.rmi.transport.TransportConstants

object PingAck {
    fun check(packet: ByteArray): Boolean {
        return packet.isNotEmpty() && packet[0] == TransportConstants.PingAck
    }
}
