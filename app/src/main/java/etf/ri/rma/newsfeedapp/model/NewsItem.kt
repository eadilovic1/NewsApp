package etf.ri.rma.newsfeedapp.model

class NewsItem(
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String,
    var isFeatured: Boolean,
    val source: String,
    val publishedDate: String,
    val imageTags: List<String> = ArrayList()
) {

    fun copyByType(
        isFeatured: Boolean = this.isFeatured
    ): NewsItem {
        return NewsItem(
            uuid = this.uuid,
            title = this.title,
            snippet = this.snippet,
            imageUrl = this.imageUrl,
            category = this.category,
            isFeatured = isFeatured,
            source = this.source,
            publishedDate = this.publishedDate,
            imageTags = ArrayList(this.imageTags)
        )
    }

    fun copyByCategory(
        cat: String = this.category
    ): NewsItem {
        return NewsItem(
            uuid = this.uuid,
            title = this.title,
            snippet = this.snippet,
            imageUrl = this.imageUrl,
            category = cat,
            isFeatured = this.isFeatured,
            source = this.source,
            publishedDate = this.publishedDate,
            imageTags = ArrayList(this.imageTags)
        )
    }

    fun copyByTags(
        tags: List<String> = this.imageTags
    ): NewsItem {
        return NewsItem(
            uuid = this.uuid,
            title = this.title,
            snippet = this.snippet,
            imageUrl = this.imageUrl,
            category = this.category,
            isFeatured = this.isFeatured,
            source = this.source,
            publishedDate = this.publishedDate,
            imageTags = tags
        )
    }
}