package etf.ri.rma.newsfeedapp.data.local.relation

import androidx.room.*
import etf.ri.rma.newsfeedapp.data.local.entity.*

data class NewsWithTags(
    @Embedded val news: NewsEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NewsTagCrossRef::class,
            parentColumn = "newsId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
