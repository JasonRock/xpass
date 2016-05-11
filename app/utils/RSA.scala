package utils

import java.io.ByteArrayOutputStream
import java.security._
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import javax.crypto._

import org.apache.commons.codec.binary.Base64

/**
  * Created by js.lee on 5/8/16.
  */
object RSA {

  /**
    * Encrypt algorithm
    */
  val KEY_ALGORITHM: String = "RSA"

  /**
    * Sign algorithm
    */
  val SIGNATURE_ALGORITHM: String = "MD5withRSA"

  /**
    * Max encrypt block
    */
  val MAX_ENCRYPT_BLOCK: Int = 117

  /**
    * Max decrypt block
    */
  val MAX_DECRYPT_BLOCK: Int = 128

  /**
    * Sign data by private key
    *
    * @param data       data that have been encrypted
    * @param privateKey private key(BASE64 encoding)
    *
    */
  def sign(data: Array[Byte], privateKey: String): String = {
    val keyBytes: Array[Byte] = Base64.decodeBase64(privateKey)
    val pkcs8KeySpec: PKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes)
    val keyFactory: KeyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
    val privateK: PrivateKey = keyFactory.generatePrivate(pkcs8KeySpec)
    val signature: Signature = Signature.getInstance(SIGNATURE_ALGORITHM)
    signature.initSign(privateK)
    signature.update(data)
    Base64.encodeBase64String(signature.sign())
  }

  /**
    * Check sign
    *
    * @param data      data that have been encrypted
    * @param publicKey public key(BASE64 encoding)
    * @param sign      sign
    *
    */
  def verify(data: Array[Byte], publicKey: String, sign: String): Boolean = {
    val keyBytes: Array[Byte] = Base64.decodeBase64(publicKey)
    val keySpec: X509EncodedKeySpec = new X509EncodedKeySpec(keyBytes)
    val keyFactory: KeyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
    val publicK: PublicKey = keyFactory.generatePublic(keySpec)
    val signature: Signature = Signature.getInstance(SIGNATURE_ALGORITHM)
    signature.initVerify(publicK)
    signature.update(data)
    signature.verify(Base64.decodeBase64(sign))
  }

  /**
    * Decrypt by private key
    *
    * @param encryptedData data that that have encrypted by public key
    * @param privateKey    private key(BASE64 encoding)
    * @return
    */
  def decryptByPrivateKey(encryptedData: Array[Byte], privateKey: String): Array[Byte] = {
    decrypt(encryptedData, privateKey, 1)
  }

  /**
    * Decrypt by public key
    *
    * @param encryptedData data that that have encrypted by private key
    * @param publicKey     public key(BASE64 encoding)
    * @return
    */
  def decryptByPublicKey(encryptedData: Array[Byte], publicKey: String): Array[Byte] = {
    decrypt(encryptedData, publicKey, 2)
  }


  /**
    * Encrypt by public key
    *
    * @param data      original data
    * @param publicKey public key(BASE64 encoding)
    * @return
    */
  def encryptByPublicKey(data: Array[Byte], publicKey: String): Array[Byte] = {
    encrypt(data, publicKey, 2)
  }

  /**
    * Encrypt by private key
    *
    * @param data       original data
    * @param privateKey private key(BASE64 encoding)
    * @return
    */
  def encryptByPrivateKey(data: Array[Byte], privateKey: String): Array[Byte] = {
    encrypt(data, privateKey, 1)
  }

  private def decrypt(encryptedData: Array[Byte], keyStr: String, keyFlag: Int): Array[Byte] = {
    val keyBytes: Array[Byte] = Base64.decodeBase64(keyStr)
    val keyFactory: KeyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
    var key: Key = null
    val cipher: Cipher = keyFlag match {
      case 1 => {
        val pkcs8KeySpec: PKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes)
        key = keyFactory.generatePrivate(pkcs8KeySpec)
        Cipher.getInstance(keyFactory.getAlgorithm)
      }
      case 2 => {
        val x509KeySpec: X509EncodedKeySpec = new X509EncodedKeySpec(keyBytes)
        key = keyFactory.generatePublic(x509KeySpec)
        Cipher.getInstance(keyFactory.getAlgorithm)
      }
    }

    cipher.init(Cipher.DECRYPT_MODE, key)
    val inputLen: Int = encryptedData.length
    val out: ByteArrayOutputStream = new ByteArrayOutputStream
    var offSet: Int = 0
    var cache: Array[Byte] = null
    var i: Int = 0
    while (inputLen - offSet > 0) {
      if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
        cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK)
      } else {
        cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet)
      }
      out.write(cache, 0, cache.length)
      i += 1
      offSet = i * MAX_DECRYPT_BLOCK
    }
    val decryptedData: Array[Byte] = out.toByteArray
    out.close()
    decryptedData
  }

  private def encrypt(data: Array[Byte], keyStr: String, keyFlag: Int): Array[Byte] = {
    val keyBytes: Array[Byte] = Base64.decodeBase64(keyStr)

    val keyFactory: KeyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
    var key: Key = null
    val cipher: Cipher = keyFlag match {
      case 1 => {
        val pkcs8KeySpec: PKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes)
        key = keyFactory.generatePrivate(pkcs8KeySpec)
        Cipher.getInstance(keyFactory.getAlgorithm)
      }
      case 2 => {
        val x509KeySpec: X509EncodedKeySpec = new X509EncodedKeySpec(keyBytes)
        key = keyFactory.generatePublic(x509KeySpec)
        Cipher.getInstance(keyFactory.getAlgorithm)
      }
    }

    cipher.init(Cipher.ENCRYPT_MODE, key)

    val inputLen: Int = data.length
    val out: ByteArrayOutputStream = new ByteArrayOutputStream
    var offSet: Int = 0
    var cache: Array[Byte] = null
    var i: Int = 0
    while (inputLen - offSet > 0) {
      if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
        cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK)
      } else {
        cache = cipher.doFinal(data, offSet, inputLen - offSet)
      }
      out.write(cache, 0, cache.length)
      i += 1
      offSet = i * MAX_ENCRYPT_BLOCK
    }
    val encryptedData: Array[Byte] = out.toByteArray
    out.close()
    encryptedData
  }
}
