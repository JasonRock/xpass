package controllers

import javax.inject._

import domains._
import org.apache.commons.codec.binary.Base64
import play.api.libs.json._
import play.api.mvc._
import utils.RSA

/**
  * Encrypt and Decrypt data for test.
  *
  * @author js.ee
  */
@Singleton
class EncryptController @Inject()() extends Controller {

  import utils.crypto.AES
  import utils.protocol.defaults._

  /**
    * Encrypt transport data with AES.
    *
    * @return
    */
  def AESEncrypt() = Action(BodyParsers.parse.json) {
    request => {
      val encryptStr = Base64.encodeBase64String(AES.encrypt(request.body.toString(), AES.AES_KEY))
      Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> encryptStr))
    }
  }

  /**
    * Decrypt transport data with AES.
    *
    * @return
    */
  def AESDecrypt() = Action(BodyParsers.parse.json) {
    request => {
      val transportRequest = request.body.validate[TransportRequest]
      transportRequest.fold(
        errors => {
          BadRequest(Json.obj("status" -> "KO", "message" -> "aaa"))
        },
        info => {
          val parse = Json.parse(info.info.get)
          Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> parse))
        }
      )
    }
  }

  def RSAEncrypt() = Action(BodyParsers.parse.json) {
    request => {

      val publicKey: String = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbq7UBSlyc0aYkIMdA3nGDUjBHOiUWeS84EYUwxzyZZ" +
        "2+ItVIyOEvxh79x3vRSmw9W2GqUfda60VxT+0JJu92a3OVYjjjatMfD9SSsfPmFJRYcwmljhR0nb+hod2u" +
        "NGxDxWSevYCs2Nj4H1oq/P+NKsxtKfKmIhnmQ8PBPyFgXUQIDAQAB"
      val encrypted: String = Base64.encodeBase64String(
        RSA.encryptByPublicKey(request.body.toString().getBytes, publicKey))
      Ok(encrypted)
    }
  }

  def RSADecrypt() = Action {
    request => {
      val privateKey: String = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANurtQFKXJzRpiQg" +
        "x0DecYNSMEc6JRZ5LzgRhTDHPJlnb4i1UjI4S/GHv3He9FKbD1bYapR91rrRXFP7" +
        "Qkm73Zrc5ViOONq0x8P1JKx8+YUlFhzCaWOFHSdv6Gh3a40bEPFZJ69gKzY2PgfW" +
        "ir8/40qzG0p8qYiGeZDw8E/IWBdRAgMBAAECgYEA1ix2gPiYjUkWnFjdDFEVCX1j" +
        "lr6JFH043YivfFx0p/iiVP68UjxzRt0cehBv0+5cqUa9u2NpraGcTEFIYw1ow9sm" +
        "UBoxbvQsm9mRTB1Ut2PvJXinFNb1z+bdT3RYU3tADNUn7MIgsqvyZWH2WUZXMZgu" +
        "qXzGklEZDuTE3tmg2fECQQDxeTT+plmt8O2DbSoR/JCPs5st0vcDCSKXGhTeDjBw" +
        "suAAeH0Wc8gcFdV2tESbArwzNIhKjLe6d5hGm+RG90PdAkEA6OK6j9OYn0xe6DkU" +
        "SUItCSk1VaLVeAgLUZA+0KR9ODavHZH4QRzj4pluADqglyIYKN9P2ZgHj/cvn1tn" +
        "muiUBQJAaqQ72kZ/Dol7a3J3hPAEq+IHI0qrGiUbqJ21H4gmrm7g7HRJ0fOaKYUe" +
        "+8iLD+Y6VWba1gmlTm1oy64nN4wV5QJAS9ItfVAdu5douuCCi0thUD87XxMxvu+X" +
        "h8mXueQj5J5hKxZwJfra8taTKr3rtOjjxsLVw3ks1SFcPtzKgXPBZQJBALiUQJII" +
        "mEVsbF0SyQMk/4OnPf4urGuJYdKr4bX/j5I779t7yKZTRipm2N14KLDs3qPAKnYc" +
        "LXkL138RiPfXDNM="

      val encryptedData = request.body.asText.orNull
      Ok(new String(RSA.decryptByPrivateKey(Base64.decodeBase64(encryptedData), privateKey)))
    }
  }
}
