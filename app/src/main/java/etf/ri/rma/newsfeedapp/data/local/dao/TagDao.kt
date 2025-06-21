package etf.ri.rma.newsfeedapp.data.local.dao

import androidx.room.*
import etf.ri.rma.newsfeedapp.data.local.entity.TagEntity
import etf.ri.rma.newsfeedapp.data.local.entity.NewsTagCrossRef

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(tag: TagEntity): Long

    @Query("SELECT * FROM Tags WHERE value = :value LIMIT 1")
    fun findByValue(value: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCrossRef(ref: NewsTagCrossRef)
}
