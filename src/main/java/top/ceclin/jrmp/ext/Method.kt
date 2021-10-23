package top.ceclin.jrmp.ext

import org.joor.Reflect
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.lang.reflect.Method
import java.security.DigestOutputStream
import java.security.MessageDigest
import kotlin.math.min

@JvmInline
value class MethodSignature(val value: String) {
    val jrmpHash: Long
        get() {
            var hash = 0L
            val md = MessageDigest.getInstance("SHA")
            // In java 11, OutputStream.nullOutputStream() is better.
            DataOutputStream(DigestOutputStream(ByteArrayOutputStream(127), md))
                .writeUTF(value)
            val digest = md.digest()
            for (i in 0 until min(8, digest.size)) {
                hash += (digest[i].toLong() and 0xFF) shl (i * 8)
            }
            return hash
        }
}

val Method.signature: MethodSignature
    get() = Reflect.onClass(sun.rmi.server.Util::class.java)
        .call("getMethodNameAndDescriptor", this)
        .get<String>().let { MethodSignature(it) }

val Method.jrmpHash: Long
    get() = sun.rmi.server.Util.computeMethodHash(this)
