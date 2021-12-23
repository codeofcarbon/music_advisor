package com.codeofcarbon.musicadvisor

class Paging {
    val firstPage = 1
    private var currentPage = 0

    fun showCurrentPage(sourceList: List<String>, currPage: Int): String {
        val numberOfPages = sourceList.size / PAGE_SIZE
        val fromIndex = PAGE_SIZE * (currPage - 1)
        val toIndex = (fromIndex + PAGE_SIZE).coerceAtMost(sourceList.size)
        val sb = StringBuilder()

        for (i in fromIndex until toIndex) {
            sb.append(sourceList[i])
        }
        sb.append(String.format("---PAGE %d OF %d---\n", currPage, numberOfPages))

        currentPage = currPage
        return sb.toString()
    }

    fun navigateThroughPages(command: String, sourceList: List<String>): String {
        val numberOfPages = sourceList.size / PAGE_SIZE

        return if (command.equals("next", ignoreCase = true)) {
            if (currentPage < numberOfPages) {
                currentPage++
                showCurrentPage(sourceList, currentPage)
            } else "No more pages."
        } else if (command.equals("prev", ignoreCase = true)) {
            if (currentPage > firstPage) {
                currentPage--
                showCurrentPage(sourceList, currentPage)
            } else "No more pages."
        } else command
    }

    companion object {
        val PAGE_SIZE = MusicAdvisor.PAGE_SIZE
    }
}