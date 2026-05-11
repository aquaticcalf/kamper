import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [Plan::class], version = 1, exportSchema = false)
abstract class PlannerDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
}

fun createDatabase(builder: RoomDatabase.Builder<PlannerDatabase>): PlannerDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

private var database: PlannerDatabase? = null
fun provideDatabase(): PlannerDatabase {
    return database ?: createDatabase(getDatabaseBuilder()).also { database = it }
}
