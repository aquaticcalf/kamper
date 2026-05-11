import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM `Plan` ORDER BY deadline ASC")
    fun getAllPlans(): Flow<List<Plan>>

    @Insert
    suspend fun insert(plan: Plan)

    @Query("SELECT * FROM `Plan` WHERE id = :id")
    suspend fun getPlanById(id: Long): Plan?

    @androidx.room.Delete
    suspend fun delete(plan: Plan)

    @androidx.room.Update
    suspend fun update(plan: Plan)
}
