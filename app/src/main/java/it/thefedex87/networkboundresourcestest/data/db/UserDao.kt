package it.thefedex87.networkboundresourcestest.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.thefedex87.networkboundresourcestest.data.db.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM Users WHERE nickName LIKE '%' || :query || '%' OR firstName LIKE '%' || :query || '%' OR lastName LIKE '%' || :query || '%'")
    fun users(query: String): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(users: List<User>)

    @Query("DELETE FROM Users")
    suspend fun deleteAll()
}