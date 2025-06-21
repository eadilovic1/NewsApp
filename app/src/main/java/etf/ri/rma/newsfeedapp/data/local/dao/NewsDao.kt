package etf.ri.rma.newsfeedapp.data.local.dao

import androidx.room.*
import etf.ri.rma.newsfeedapp.data.local.entity.NewsEntity
import etf.ri.rma.newsfeedapp.data.local.relation.NewsWithTags

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)  // Promijenjeno iz ABORT u REPLACE
    fun insert(news: NewsEntity): Long

    @Query("SELECT * FROM News")
    fun loadAll(): List<NewsEntity>

    @Query("SELECT * FROM News WHERE category = :category")
    fun loadByCategory(category: String): List<NewsEntity>

    @Transaction
    @Query("SELECT * FROM News WHERE id = :newsId")
    fun loadWithTags(newsId: Int): NewsWithTags

    @Transaction
    @Query("""
      SELECT *
      FROM News
      INNER JOIN NewsTags ON News.id = NewsTags.newsId
      INNER JOIN Tags ON Tags.id = NewsTags.tagId
      WHERE Tags.value IN(:tags)
      ORDER BY publishedDate DESC
    """)
    fun loadSimilar(tags: List<String>): List<NewsWithTags>
}