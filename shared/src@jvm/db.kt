import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<PlannerDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "planner.db")
    return Room.databaseBuilder<PlannerDatabase>(
        name = dbFile.absolutePath,
    )
}
