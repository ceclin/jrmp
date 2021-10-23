package top.ceclin.jrmp.ext

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MethodKtTest {

    @Test
    fun getSignature() {
        val method = String::class.java.getDeclaredMethod("substring", Int::class.java, Int::class.java)
        assertEquals("substring(II)Ljava/lang/String;", method.signature.value)
    }

    @Test
    fun getJrmpHash() {
        val method = String::class.java.getDeclaredMethod("substring", Int::class.java, Int::class.java)
        assertEquals(sun.rmi.server.Util.computeMethodHash(method), method.jrmpHash)
    }

    @Test
    fun signatureToHash() {
        val method = String::class.java.getDeclaredMethod("substring", Int::class.java, Int::class.java)
        assertEquals(sun.rmi.server.Util.computeMethodHash(method), method.signature.jrmpHash)
    }
}
