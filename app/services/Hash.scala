package services

import java.security.MessageDigest

import org.apache.commons.codec.binary.Hex

/**
  * Created by unoedx on 17/05/16.
  */
object Hash {
  private def md = MessageDigest.getInstance("MD5");

  private val HASH_SALT = "sad9834jkfad89gvv3qy1@#%agad4wvaguy8ab5sr46sr8eg435fb4s6e8r3g54btr68h3fd5g4hs8th6sd5fh4683hdh445yhw54w"

  def hashEmail(email: String): String = md5(HASH_SALT + md5(email))

  private def md5(s:String) = Hex.encodeHexString(md.digest(s.getBytes))
}
