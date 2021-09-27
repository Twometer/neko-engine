package de.twometer.neko.util

import org.lwjgl.system.MemoryStack
import org.lwjgl.util.meow.Meow
import org.lwjgl.util.meow.MeowHash
import java.nio.ByteBuffer
import java.nio.LongBuffer

interface HashResult

class MeowHashResult(buf: LongBuffer) : HashResult {
    private val v0: Long = buf[0]
    private val v1: Long = buf[1]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MeowHashResult) return false

        if (v0 != other.v0) return false
        if (v1 != other.v1) return false
        return true
    }

    override fun hashCode(): Int {
        var result = v0.hashCode()
        result = 31 * result + v1.hashCode()
        return result
    }
}

object Hash {
    private const val seed = 3141592653589793238L

    fun meow(data: ByteBuffer): HashResult {
        MemoryStack.stackPush().use {
            val dst = MeowHash(it.malloc(16))
            Meow.MeowHash_Accelerated(seed, data, dst)
            return MeowHashResult(dst.u64())
        }
    }
}