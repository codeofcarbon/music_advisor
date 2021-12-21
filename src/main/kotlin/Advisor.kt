import java.util.*
import kotlin.system.exitProcess

/**
 * Advisor class. Interface for communicating with the user.
 */
object Advisor {
    private val sc = Scanner(System.`in`)
    private val service: Service = Service()
    private lateinit var query: Array<String>
    var command = ""

    fun start() {
        println(
            """
            ----->>> Spotify Music Advisor <<<-----
            
                    Type 'auth' to authorize.
                    (write requests without quotation marks)
                    """.trimIndent()
        )

        while (true) {
            query =
                if (command.isBlank()) {
                    sc.nextLine().trim().split(" ").toTypedArray()
                } else command.trim().split(" ").toTypedArray()

            when (query[0]) {
                "auth" -> {
                    service.setAuthorization()
                    if (service.authorised) {
                        printHelp()
                    }
                }
                "new", "featured", "categories", "playlists" -> service.getResults(query)
                "exit" -> {
                    println("---GOODBYE!---")
                    exitProcess(0)
                }
                else -> {
                    println("Wrong command. Try again.")
                    command = ""
                    printHelp()
                }
            }
        }
    }

    private fun printHelp() = println(
                """
        Type:
                'new' -> to get new releases list
                'featured' -> to get featured list
                'categories' -> to list all categories
                'playlists' 'category name' -> choose the category you are interested in by entering 'playlists' + category name
                'exit' -> to exit the program
                
                Advisor shows five items at once.
                'next' -> to go to the next page
                'prev' -> to go to the previous page
                
                """.trimIndent()
    )
}