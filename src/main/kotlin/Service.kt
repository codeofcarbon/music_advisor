import java.util.*

class Service {
    private val sc = Scanner(System.`in`)
    private val content = Content()
    private val paging = Paging()
    var authorised = false

    /**
     * Getting authorization and setting the authorization flag
     */
    fun setAuthorization() {
        Authorisation.getAccessCode()
        Authorisation.requestAccessToken()
        authorised = true
    }

    fun getResults(query: Array<String>) {
        if (authorised) {

            when (query[0]) {
                "new" -> {
                    if (content.newReleasesList.isEmpty()) content.getNewReleases()
                    println(paging.showCurrentPage(content.newReleasesList, paging.firstPage))
                }
                "featured" -> {
                    if (content.featuredList.isEmpty()) content.getFeatured()
                    println(paging.showCurrentPage(content.featuredList, paging.firstPage))
                }
                "categories" -> {
                    if (content.categoriesList.isEmpty()) content.getCategories()
                    println(paging.showCurrentPage(content.categoriesList, paging.firstPage))
                }
                "playlists" -> {
                    val category = StringJoiner(" ")
                    for (i in 1 until query.size) {
                        category.add(query[i])
                    }
                    content.getPlaylist(category.toString())
                    println(paging.showCurrentPage(content.playlistsList, paging.firstPage))
                }
            }

            while (true) {
                val command = sc.nextLine().trim()
                if (command == "next" || command == "prev") {
                    println(
                        paging.navigateThroughPages(
                            command,
                            if ("new" == query[0]) content.newReleasesList
                            else if ("featured" == query[0]) content.featuredList
                            else if ("categories" == query[0]) content.categoriesList
                            else if ("playlists" == query[0]) content.playlistsList
                            else emptyList()
                        )
                    )
                } else {
                    Advisor.command = command
                    break
                }
            }

        } else println("Please, provide access for application.")
    }
}