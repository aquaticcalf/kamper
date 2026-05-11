import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

lateinit var appContext: Context

actual fun getDatabaseBuilder(): RoomDatabase.Builder<PlannerDatabase> {
    val dbFile = appContext.getDatabasePath("planner.db")
    return Room.databaseBuilder<PlannerDatabase>(
        context = appContext.applicationContext,
        name = dbFile.absolutePath
    )
}
