object MusicAdvisor {
    lateinit var SERVER_PATH: String
    lateinit var RESOURCE: String
    var PAGE_SIZE = 0

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isNotEmpty()) {
            if (args[0] == "-access") SERVER_PATH = args[1]
            if (args[2] == "-resource") RESOURCE = args[3]
            if (args[4] == "-page") PAGE_SIZE = args[5].toInt()
        } else {
            SERVER_PATH = "https://accounts.spotify.com"
            RESOURCE = "https://api.spotify.com"
            PAGE_SIZE = 5
        }

        Advisor.start()
    }
}