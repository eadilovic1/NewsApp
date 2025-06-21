package etf.ri.rma.newsfeedapp.data.local.entity

import androidx.room.*

@Entity(tableName = "News")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val uuid: String,
    @ColumnInfo val title: String,
    @ColumnInfo val snippet: String,
    @ColumnInfo val source: String,
    @ColumnInfo val publishedDate: String,
    @ColumnInfo val imageUrl: String,
    @ColumnInfo val category: String,
    @ColumnInfo val isFeatured: Boolean
)


@Entity(tableName = "Tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val value: String
)

@Entity(
    tableName = "NewsTags",
    primaryKeys = ["newsId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = NewsEntity::class,
            parentColumns = ["id"],
            childColumns = ["newsId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tagId")]
)

data class NewsTagCrossRef(
    val newsId: Int,
    val tagId: Int
)