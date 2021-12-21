import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Class where we get the authorization code and use it to get the authorization token
 */
object Authorisation {
    private const val redirectUri = "http://localhost:8080"
    private var clientID = "69833cec017741f4823d7163efdd50bd"
    private var clientSecret = "aeac4ca2cc6142549e3e172e20b1b9c3"
    var accessToken = ""
    private var accessCode = ""

    /**
     * Getting access_code
     * Creating a line to go to in the browser.
     * Creating a server and listening to the request.
     */
    fun getAccessCode() {
        val uri = (MusicAdvisor.SERVER_PATH + "/authorize"
                + "?client_id=" + clientID
                + "&redirect_uri=" + redirectUri
                + "&response_type=code")
        println("use this link to request the access code:")
        println(uri)
        try {
            val server = HttpServer.create()
            server.bind(InetSocketAddress(8080), 0)
            server.createContext("/") { exchange: HttpExchange ->
                val query = exchange.requestURI.query
                val response: String
                if (query != null && query.contains("code")) {
                    accessCode = query.substring(5)
                    println("code received")
                    response = "Got the code. Return back to your program."
                } else {
                    response = "Authorization code not found. Try again."
                }
                exchange.sendResponseHeaders(200, response.length.toLong())
                exchange.responseBody.write(response.toByteArray())
                exchange.responseBody.close()
            }
            server.start()
            println("waiting for code...")
            while (accessCode.isEmpty()) {
                Thread.sleep(100)
            }
            server.stop(50)
        } catch (e: IOException) {
            println("Server error")
        } catch (e: InterruptedException) {
            println("Server error")
        }
    }

    /**
     * Getting access_token based on access_code
     */
    fun requestAccessToken() {
        println("Making http request for access_token...")
        val request = HttpRequest.newBuilder()
            .header("Content-Type", "application/x-www-form-urlencoded")
            .uri(URI.create(MusicAdvisor.SERVER_PATH + "/api/token"))
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    "grant_type=authorization_code"
                            + "&code=" + accessCode
                            + "&client_id=" + clientID
                            + "&client_secret=" + clientSecret
                            + "&redirect_uri=" + redirectUri
                )
            )
            .build()
        try {
            val client = HttpClient.newBuilder().build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response != null) accessToken = getAccessToken(response.body())
            println("Success!\n")
        } catch (e: InterruptedException) {
            println("Error response")
        } catch (e: IOException) {
            println("Error response")
        }
    }

    /**
     * Parsing access token from response
     *
     * @param response - String, JSON response with token
     */
    private fun getAccessToken(response: String): String {
        val jsonObject: JsonObject = JsonParser().parse(response).asJsonObject
        accessToken = jsonObject["access_token"].asString
        return accessToken
    }
}