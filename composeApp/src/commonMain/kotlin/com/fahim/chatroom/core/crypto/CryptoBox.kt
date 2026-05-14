package com.fahim.chatroom.core.crypto

/**
 * Symmetric seal/open seam. Isolated behind an interface so E2EE and encrypted local cache
 * can be added later without touching call sites.
 */
interface CryptoBox {
    fun seal(plaintext: ByteArray): ByteArray
    fun open(ciphertext: ByteArray): ByteArray
}

/** Pass-through placeholder. NOT secure — replace before persisting secrets or enabling E2EE. */
class NoopCryptoBox : CryptoBox {
    override fun seal(plaintext: ByteArray): ByteArray = plaintext
    override fun open(ciphertext: ByteArray): ByteArray = ciphertext
}