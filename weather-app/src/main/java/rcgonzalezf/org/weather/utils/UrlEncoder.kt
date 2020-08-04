package rcgonzalezf.org.weather.utils

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

interface UrlEncoder {
    @Throws(UnsupportedEncodingException::class)
    fun encodeUtf8(urlToEncode:String):String
}

class WeatherAppUrlEncoder:UrlEncoder {
    @Throws(UnsupportedEncodingException::class)
    override fun encodeUtf8(urlToEncode: String): String
        = URLEncoder.encode(urlToEncode, "UTF-8")
}
