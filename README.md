# LAB_WEEK_10 - Android ViewModel, LiveData & Room Database Integration

**Link Google Drive**  
[Keseluruhan Project](https://drive.google.com/drive/u/5/folders/1CbqdKc-EyqVs2zg01C_MRZMphNLaRALr)

[Images & Screenshots](https://drive.google.com/drive/u/5/folders/16mWpew-tT1fL6jji7XBi7BxmgnpYN3AF)

[APK File](https://drive.google.com/drive/u/5/folders/1j04gS7YUGFpxcaZJ1m79TNxzdKVaEDP1)

## Commit History
- **Commit No. 1** - Implement ViewModel to Handle Rotation
- **Commit No. 2** - Implement LiveData to Sync Activity and Fragment UI  
- **Commit No. 3** - Integrate Room Database for Data Persistence
- **Commit No. 4** - Bonus: Add Date Tracking with @Embedded Annotation

## Fitur Aplikasi

### **ViewModel & State Management**
- **Rotation Handling** - Mempertahankan state saat device rotation dengan ViewModel
- **Lifecycle Awareness** - ViewModel survive configuration changes
- **Data Persistence** - Data tetap tersimpan meskipun activity direcreate
- **Separation of Concerns** - Business logic terpisah dari UI components

### **LiveData & Reactive UI**
- **Observer Pattern** - UI otomatis update ketika data berubah
- **Activity-Fragment Sync** - Multiple UI components sinkron dengan data yang sama
- **Lifecycle Safety** - Tidak ada memory leaks dengan lifecycle-aware components
- **Real-time Updates** - Perubahan data langsung ter-reflect di semua observer

### **Room Database Integration**
- **Local Data Persistence** - SQLite database dengan Room abstraction
- **CRUD Operations** - Create, Read, Update, Delete operations dengan DAO
- **Schema Management** - Database versioning dan migration handling
- **Type Safety** - Compile-time query verification

### **@Embedded Annotation Feature**
- **Complex Data Structure** - Menyimpan object dalam single database column
- **Date Tracking** - Automatic timestamp tracking untuk last update
- **Toast Notification** - Menampilkan last updated date saat app start
- **Auto-persistence** - Data tersimpan otomatis saat app paused

## Teknologi yang Digunakan

### **Android Architecture Components**
- **ViewModel** - UI-related data holder yang survive configuration changes
- **LiveData** - Observable data holder yang lifecycle-aware
- **Room Database** - SQLite abstraction layer dengan compile-time verification
- **Lifecycle Awareness** - Automatic lifecycle management

### **Room Database Components**
- **Entity** - Data model dengan @Entity annotation dan @Embedded objects
- **DAO** - Data Access Object dengan @Query, @Insert, @Update operations
- **Database** - RoomDatabase class dengan version management
- **Migration** - Schema migration dengan fallbackToDestructiveMigration

### **UI Components**
- **Activity** - MainActivity dengan FragmentContainerView
- **Fragment** - FirstFragment untuk multiple UI synchronization
- **LiveData Observers** - Reactive UI updates dengan observer pattern
- **Toast Notifications** - User feedback untuk last updated timestamp

### **Data Management**
- **@Embedded Objects** - Complex data structure dalam database
- **Date Tracking** - Java Date library untuk timestamp management
- **Shared ViewModel** - Single source of truth untuk multiple UI components
- **Data Consistency** - Guaranteed data sync antara Activity dan Fragment

## Struktur Data & Components

### **Data Models**
```kotlin
@Entity(tableName = "total")
data class Total(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @Embedded val total: TotalObject
)

data class TotalObject(
    @ColumnInfo(name = "value") val value: Int,
    @ColumnInfo(name = "date") val date: String
)
```

### **ViewModel Components**
```kotlin
class TotalViewModel : ViewModel() {
    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total
    
    private val _lastUpdated = MutableLiveData<String>()
    val lastUpdated: LiveData<String> = _lastUpdated
}
```

### **Database Components**
```kotlin
@Database(entities = [Total::class], version = 3, exportSchema = false)
abstract class TotalDatabase : RoomDatabase() {
    abstract fun totalDao(): TotalDao
}

@Dao
interface TotalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(total: Total)
    
    @Update
    fun update(total: Total)
    
    @Query("SELECT * FROM total WHERE id = :id")
    fun getTotal(id: Long): Total?
}
```

## Execution Chronology

### **Phase 1: App Initialization**
- **Database Setup** - Room database initialization dengan destructive migration
- **ViewModel Creation** - TotalViewModel instantiation dengan lazy initialization
- **Data Loading** - Load previous state dari database atau set default values
- **UI Binding** - Setup LiveData observers untuk reactive updates

### **Phase 2: User Interaction**
- **Button Click** - Increment total value melalui ViewModel
- **LiveData Update** - MutableLiveData value change trigger UI update
- **Multi-UI Sync** - Activity dan Fragment otomatis update secara simultan
- **Real-time Feedback** - User melihat perubahan langsung di kedua UI components

### **Phase 3: Data Persistence**
- **App Pause** - onPause callback trigger database update
- **Date Tracking** - Current timestamp disimpan bersama total value
- **Auto-save** - Data otomatis tersimpan tanpa user intervention
- **Schema Management** - Room handle database version dan migration

### **Phase 4: App Restoration**
- **App Start** - onStart callback trigger last updated toast
- **Data Retrieval** - Load persisted data dari database
- **State Restoration** - Previous state fully restored
- **User Notification** - Toast menampilkan last updated timestamp

## Key Features Implementation

### **ViewModel Setup**
```kotlin
private val viewModel by lazy {
    ViewModelProvider(this)[TotalViewModel::class.java]
}
```

### **LiveData Observation**
```kotlin
viewModel.total.observe(this) { currentTotal ->
    updateText(currentTotal)
}

viewModel.lastUpdated.observe(this) { date ->
    Toast.makeText(this, "Last updated: $date", Toast.LENGTH_LONG).show()
}
```

### **Room Database Integration**
```kotlin
private fun initializeValueFromDatabase() {
    val total = database.totalDao().getTotal(1)
    if (total == null) {
        val currentDate = Date().toString()
        database.totalDao().insert(Total(id = 1, total = TotalObject(0, currentDate)))
    } else {
        viewModel.setTotal(total.total.value)
        viewModel.setLastUpdated(total.total.date)
    }
}
```

### **Auto-persistence Mechanism**
```kotlin
override fun onPause() {
    super.onPause()
    val currentDate = Date().toString()
    val currentTotal = viewModel.total.value ?: 0
    database.totalDao().update(Total(id = 1, total = TotalObject(currentTotal, currentDate)))
    viewModel.setLastUpdated(currentDate)
}
```

### **@Embedded Annotation Implementation**
```kotlin
@Embedded val total: TotalObject

data class TotalObject(
    @ColumnInfo(name = "value") val value: Int,
    @ColumnInfo(name = "date") val date: String
)
```

## Architecture Benefits

### **Maintainability**
- Clean separation antara UI, business logic, dan data layer
- Modular components yang mudah di-test dan di-maintain
- Clear responsibility distribution across components

### **Scalability**
- Mudah menambah features baru tanpa mengganggu existing code
- Support untuk multiple UI components dengan shared data source
- Flexible database schema dengan Room migration

### **Performance**
- Efficient data loading dengan LiveData observers
- Minimal database operations dengan smart update triggers
- Optimized UI updates dengan reactive programming pattern

### **User Experience**
- Smooth state preservation across app lifecycle
- Instant feedback dengan real-time UI updates
- Persistent data storage dengan automatic backup mechanism
