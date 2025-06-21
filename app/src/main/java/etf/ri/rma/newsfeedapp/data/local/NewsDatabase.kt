package etf.ri.rma.newsfeedapp.data.local

import android.content.Context
import androidx.room.*
import etf.ri.rma.newsfeedapp.data.local.dao.*
import etf.ri.rma.newsfeedapp.data.local.entity.*
import etf.ri.rma.newsfeedapp.data.local.relation.NewsWithTags
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
    entities = [NewsEntity::class, TagEntity::class, NewsTagCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun tagDao(): TagDao

    companion object {
        private const val DB_NAME = "news-db"
        @Volatile private var INSTANCE: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }

    suspend fun saveNews(item: NewsItem): Boolean = withContext(Dispatchers.IO) {
        try {
            // Provjeri da li već postoji
            if (newsDao().loadAll().any { it.uuid == item.uuid }) return@withContext true

            val entity = NewsEntity(
                uuid = item.uuid,
                title = item.title,
                snippet = item.snippet,
                source = item.source,
                publishedDate = item.publishedDate,
                imageUrl = item.imageUrl ?: "",
                category = item.category,
                isFeatured = item.isFeatured
            )

            val id = newsDao().insert(entity)
            id != -1L
        } catch (e: Exception) {
            false
        }
    }

    suspend fun allNews(): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            newsDao().loadAll().map { e ->
                toNewsItem(e, getTags(e.id))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNewsWithCategory(category: String): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            newsDao().loadByCategory(category).map { e ->
                toNewsItem(e, getTags(e.id))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addTags(tags: List<String>, newsId: Int): Int = withContext(Dispatchers.IO) {
        try {
            var newCount = 0
            tags.forEach { value ->
                val existing = tagDao().findByValue(value)
                val tagId = existing?.id ?: run {
                    val id = tagDao().insert(TagEntity(value = value)).toInt()
                    if (id != -1) newCount++
                    id
                }
                tagDao().insertCrossRef(NewsTagCrossRef(newsId, tagId))
            }
            newCount
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getTags(newsId: Int): List<String> = withContext(Dispatchers.IO) {
        try {
            newsDao().loadWithTags(newsId).tags.map { it.value }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            val firstTwo = tags.take(2)
            newsDao().loadSimilar(firstTwo).map { rel ->
                toNewsItem(rel.news, rel.tags.map { it.value })
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun toNewsItem(entity: NewsEntity, tags: List<String>) = NewsItem(
        uuid = entity.uuid,
        title = entity.title,
        snippet = entity.snippet,
        source = entity.source,
        publishedDate = entity.publishedDate,
        imageUrl = entity.imageUrl,
        category = entity.category,
        isFeatured = entity.isFeatured,
        imageTags = ArrayList(tags)
    )
}