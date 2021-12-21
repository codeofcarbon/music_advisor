import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Receive and process information on requests
 */
class Content {
    private var categoriesURI = "/v1/browse/categories"
    private val newURI = "/v1/browse/new-releases"
    private val featuredURI = "/v1/browse/featured-playlists"
    private val playlistsURI = "/v1/browse/categories/"
    private lateinit var infoList: MutableList<Info>
    var categoriesList: MutableList<String> = ArrayList()
    var newReleasesList: MutableList<String> = ArrayList()
    var featuredList: MutableList<String> = ArrayList()
    var playlistsList: MutableList<String> = ArrayList()
    private var resultsLimit = "?limit=50" // ex. (=%d == 20)

/*
//  static String country = "&country=";  // ex. (=%s == "PL")
//  static String localeLang = "&locale="; // ex. (=%s == "pl_PL")
    if there is any additional query parameters, then:
    "?" is needed as a prefix before first one,
    "&" as prefix before any next.
*/

    /**
     * GET request with access token
     *
     * @param _path - String, uri path,
     * @return - String, answer of the server
     */
    private fun getRequest(_path: String): String {
        val httpRequest = HttpRequest.newBuilder()
            .header("Authorization", "Bearer " + Authorisation.accessToken)
            .uri(URI.create(_path))
            .GET()
            .build()

        return try {
            val client = HttpClient.newBuilder().build()
            val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
            response.body()
        } catch (e: InterruptedException) {
            "Error response"
        } catch (e: IOException) {
            "Error response"
        }
    }

    /**
     * Getting list of categories from Spotify
     */
    fun getCategories() {
        infoList = ArrayList()
        val response = getRequest(MusicAdvisor.RESOURCE + categoriesURI + resultsLimit)
        val categories: JsonObject = JsonParser().parse(response).asJsonObject
            .getAsJsonObject("categories")

        for (item in categories.getAsJsonArray("items")) {
            val element = Info()

            element.categories = item.asJsonObject["name"]
                .toString().replace("\"".toRegex(), "")

            infoList.add(element)
        }

        for (each in infoList) {
            categoriesList.add(each.categories + "\n\n")
        }
    }

    /**
     * Getting list of new releases from Spotify
     */
    fun getNewReleases() {
        infoList = ArrayList()
        val response = getRequest(MusicAdvisor.RESOURCE + newURI + resultsLimit)
        val albums: JsonObject = JsonParser().parse(response).asJsonObject
            .getAsJsonObject("albums")

        for (item in albums.getAsJsonArray("items")) {
            val element = Info()

            element.album = item.asJsonObject["name"]
                .toString().replace("\"".toRegex(), "")

            val artists = StringBuilder("[")

            for (name in item.asJsonObject.getAsJsonArray("artists")) {
                if (!artists.toString().endsWith("[")) {
                    artists.append(", ")
                }
                artists.append(name.asJsonObject["name"])
            }

            element.name = artists.append("]").toString().replace("\"".toRegex(), "")

            element.link = item.asJsonObject["external_urls"]
                .asJsonObject["spotify"]
                .toString().replace("\"".toRegex(), "")

            infoList.add(element)
        }

        for (each in infoList) {
            newReleasesList.add(each.album + "\n" + each.name + "\n" + each.link + "\n\n")
        }
    }

    /**
     * Getting list of featured playlists from Spotify
     */
    fun getFeatured() {
        infoList = ArrayList()
        val response = getRequest(MusicAdvisor.RESOURCE + featuredURI + resultsLimit)
        val playlists: JsonObject = JsonParser().parse(response).asJsonObject
            .getAsJsonObject("playlists")

        for (item in playlists.getAsJsonArray("items")) {
            val element = Info()

            element.album = item.asJsonObject["name"]
                .toString().replace("\"".toRegex(), "")

            element.link =
                item.asJsonObject["external_urls"]
                    .asJsonObject["spotify"]
                    .toString().replace("\"".toRegex(), "")

            infoList.add(element)
        }

        for (each in infoList) {
            featuredList.add(each.album + "\n" + each.link + "\n\n")
        }
    }

    /**
     * Getting list of playlists (by chosen category) from Spotify
     * First we get the category id by its name, then we get the playlist by category id.
     *
     * @param categoryName - String, category NAME! (not ID)
     */
    fun getPlaylist(categoryName: String) {
        infoList = ArrayList()
        var response = getRequest(MusicAdvisor.RESOURCE + categoriesURI + resultsLimit)
        var categoryID = ""
        val catItemsAll: MutableList<JsonObject> = ArrayList()
        var categories: JsonObject = JsonParser().parse(response).asJsonObject
            .getAsJsonObject("categories")

        for (item in categories.getAsJsonArray("items")) {
            catItemsAll.add(item.asJsonObject)
        }

        for (item in catItemsAll) {
            if (categoryName == item["name"].asString) {
                categoryID = item["id"].asString
            }
        }

        if (categoryID.isBlank()) {
            println("Unknown category name.")
            return
        } else {
            response = getRequest(MusicAdvisor.RESOURCE + playlistsURI + categoryID + "/playlists" + resultsLimit)
        }

        if (response.contains("Test unpredictable error message")) {
            println("Test unpredictable error message")
            return
        } else {
            categories = JsonParser().parse(response).asJsonObject.getAsJsonObject("playlists")
        }

        for (item in categories.getAsJsonArray("items")) {
            val element = Info()

            element.album = item.asJsonObject["name"]
                .toString().replace("\"".toRegex(), "")

            element.link = item.asJsonObject["external_urls"]
                .asJsonObject["spotify"]
                .toString().replace("\"".toRegex(), "")

            infoList.add(element)
        }

        playlistsList.clear()
        for (each in infoList) {
            playlistsList.add(each.album + "\n" + each.link + "\n\n")
        }
    }
}