# 📸 Receipts — Android APK Implementation Guide
> *"Use at your own risk."*  
> Based on the original iOS app by Trash Dev, with all community-suggested features, the "Littles" mode, and every chaotic idea from the standup pod.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack](#2-tech-stack)
3. [Project Structure](#3-project-structure)
4. [Database Schema](#4-database-schema)
5. [Core Features Implementation](#5-core-features-implementation)
6. [Littles Mode (Constructive Mode)](#6-littles-mode-constructive-mode)
7. [Widget Implementation](#7-widget-implementation)
8. [Premium & Monetization](#8-premium--monetization)
9. [Loot Box System](#9-loot-box-system)
10. [Uno Reverse System](#10-uno-reverse-system)
11. [Expose Feature (Hype Video)](#11-expose-feature-hype-video)
12. [Achievements & Badges](#12-achievements--badges)
13. [AI Profile Picture System](#13-ai-profile-picture-system)
14. [Email Threshold Feature](#14-email-threshold-feature)
15. [Private Leaderboards & Groups](#15-private-leaderboards--groups)
16. [AI Coaching Layer (Littles Integration)](#16-ai-coaching-layer-littles-integration)
17. [Onboarding Flow](#17-onboarding-flow)
18. [Settings & Config Screen](#18-settings--config-screen)
19. [Building & Signing the APK](#19-building--signing-the-apk)
20. [Google Play Submission Checklist](#20-google-play-submission-checklist)

---

## 1. Project Overview

**App Name:** Receipts  
**Package ID:** `com.trashdev.receipts`  
**Platform:** Android (minSdk 26 / API 26+)  
**Architecture:** MVVM + Repository pattern  
**Storage:** 100% local (Room DB + internal file storage) — no cloud sync in v1  
**Modes:** Receipts Mode (chaotic) + Littles Mode (wholesome/constructive)

### Feature Summary

| Feature | Free | Premium ($2.99/mo or $19.99/yr) |
|---|---|---|
| Track up to 3 people | ✅ | ✅ |
| Track unlimited people | ❌ | ✅ |
| Photo receipts | ✅ | ✅ |
| Video receipts | ✅ | ✅ |
| Home screen widget | ✅ | ✅ |
| Streaks & stats | ✅ | ✅ |
| Achievements & badges | ✅ | ✅ |
| Email threshold blasts | ❌ | ✅ |
| Expose hype video | ❌ | ✅ |
| Private group leaderboards | ❌ | ✅ |
| Loot boxes | ❌ | ✅ |
| Uno Reverse mode | ❌ | ✅ |
| Littles AI coaching | ❌ | ✅ |
| Family shared plan | ❌ | ✅ |
| Auto AI profile picture | ✅ | ✅ |

---

## 2. Tech Stack

```
Language:         Kotlin
UI Framework:     Jetpack Compose
Architecture:     MVVM + Repository
Database:         Room (SQLite)
DI:               Hilt
Image Loading:    Coil
Video:            ExoPlayer / MediaRecorder
Widget:           Glance (Jetpack)
Notifications:    WorkManager
AI Coaching:      OpenAI API (GPT-4o-mini) or Gemini API
Video Compilation: FFMPEG Kit for Android
In-App Purchase:  Google Play Billing Library 6.x
Navigation:       Compose Navigation
Permissions:      Accompanist Permissions
Serialization:    Kotlinx Serialization
```

### `build.gradle.kts` (app level) — Key Dependencies

```kotlin
dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.activity:activity-compose:1.9.0")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Coil
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-video:2.6.0")

    // Glance Widget
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-material3:1.0.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

    // FFmpeg
    implementation("com.arthenica:ffmpeg-kit-full:6.0-2")

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:6.2.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // OpenAI (for Littles AI coaching)
    implementation("com.aallam.openai:openai-client:3.7.2")
    implementation("io.ktor:ktor-client-okhttp:2.3.11")
}
```

---

## 3. Project Structure

```
com.trashdev.receipts/
├── data/
│   ├── db/
│   │   ├── ReceiptsDatabase.kt
│   │   ├── dao/
│   │   │   ├── PersonDao.kt
│   │   │   ├── ReceiptDao.kt
│   │   │   ├── CategoryDao.kt
│   │   │   ├── AchievementDao.kt
│   │   │   └── GroupDao.kt
│   │   └── entities/
│   │       ├── PersonEntity.kt
│   │       ├── ReceiptEntity.kt
│   │       ├── CategoryEntity.kt
│   │       ├── AchievementEntity.kt
│   │       └── GroupEntity.kt
│   ├── repository/
│   │   ├── ReceiptRepository.kt
│   │   ├── PersonRepository.kt
│   │   ├── AchievementRepository.kt
│   │   └── PremiumRepository.kt
│   └── preferences/
│       └── UserPreferences.kt
├── domain/
│   ├── model/
│   │   ├── Person.kt
│   │   ├── Receipt.kt
│   │   ├── Category.kt
│   │   ├── Achievement.kt
│   │   └── LootBox.kt
│   └── usecase/
│       ├── AddReceiptUseCase.kt
│       ├── GetFeedUseCase.kt
│       ├── TriggerExposeUseCase.kt
│       ├── UnoReverseUseCase.kt
│       └── GetCoachingAdviceUseCase.kt
├── ui/
│   ├── theme/
│   │   ├── Theme.kt           // Receipts Mode (dark, evidence-room vibes)
│   │   ├── LittlesTheme.kt    // Littles Mode (spring, pastel, wholesome)
│   │   └── Type.kt
│   ├── screens/
│   │   ├── onboarding/
│   │   │   └── OnboardingScreen.kt
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   ├── feed/
│   │   │   ├── FeedScreen.kt
│   │   │   └── FeedViewModel.kt
│   │   ├── capture/
│   │   │   ├── CaptureScreen.kt
│   │   │   └── CaptureViewModel.kt
│   │   ├── person/
│   │   │   ├── PersonDetailScreen.kt
│   │   │   └── PersonDetailViewModel.kt
│   │   ├── stats/
│   │   │   ├── StatsScreen.kt
│   │   │   └── StatsViewModel.kt
│   │   ├── leaderboard/
│   │   │   ├── LeaderboardScreen.kt
│   │   │   └── LeaderboardViewModel.kt
│   │   ├── achievements/
│   │   │   └── AchievementsScreen.kt
│   │   ├── expose/
│   │   │   └── ExposeScreen.kt
│   │   ├── littles/
│   │   │   ├── LittlesCoachScreen.kt
│   │   │   └── LittlesViewModel.kt
│   │   ├── premium/
│   │   │   └── PremiumScreen.kt
│   │   └── settings/
│   │       └── SettingsScreen.kt
│   └── components/
│       ├── ReceiptCard.kt
│       ├── PersonChip.kt
│       ├── BadgeView.kt
│       ├── LootBoxDialog.kt
│       └── UnoReverseDialog.kt
├── widget/
│   ├── QuickCaptureWidget.kt
│   └── WidgetReceiver.kt
├── worker/
│   ├── EmailThresholdWorker.kt
│   └── StreakWorker.kt
├── billing/
│   └── BillingManager.kt
└── ai/
    ├── CoachingClient.kt
    └── CoachingPrompts.kt
```

---

## 4. Database Schema

### `PersonEntity.kt`

```kotlin
@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationship: String,          // "partner", "friend", "child", "self", "other"
    val avatarPath: String?,           // local file path
    val autoAvatarCategoryId: Long?,   // auto-set from most-photographed category
    val isMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val mode: String = "receipts"      // "receipts" or "littles"
)
```

### `CategoryEntity.kt`

```kotlin
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personId: Long,
    val name: String,                  // e.g. "Food in Sink", "Laundry on Floor"
    val emoji: String,                 // e.g. "🍓", "👕"
    val threshold: Int = 10,           // trigger email blast at this count
    val isPositive: Boolean = false,   // true = affirmation/compliment, false = receipt
    val totalCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
```

### `ReceiptEntity.kt`

```kotlin
@Entity(
    tableName = "receipts",
    foreignKeys = [
        ForeignKey(entity = PersonEntity::class, parentColumns = ["id"],
            childColumns = ["personId"], onDelete = CASCADE),
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"],
            childColumns = ["categoryId"], onDelete = SET_NULL)
    ]
)
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personId: Long,
    val categoryId: Long?,
    val mediaPath: String,             // local path to photo or video
    val mediaType: String,             // "photo" or "video"
    val thumbnailPath: String?,
    val note: String = "",
    val isPositive: Boolean = false,
    val isHidden: Boolean = false,     // for loot box "delete" mechanic
    val lootBoxUsed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null
)
```

### `AchievementEntity.kt`

```kotlin
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,        // e.g. "repeat_offender_5"
    val personId: Long?,               // null = global achievement
    val name: String,
    val description: String,
    val emoji: String,
    val tier: String,                  // "common", "rare", "legendary"
    val unlockedAt: Long?,
    val isUnlocked: Boolean = false
)
```

### `GroupEntity.kt`

```kotlin
@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val inviteCode: String,            // 6-char code for local share
    val createdAt: Long = System.currentTimeMillis()
)
```

---

## 5. Core Features Implementation

### 5.1 — Adding a Receipt

```kotlin
// CaptureViewModel.kt
@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    fun saveReceipt(
        personId: Long,
        categoryId: Long?,
        mediaPath: String,
        mediaType: String,
        isPositive: Boolean,
        note: String = ""
    ) {
        viewModelScope.launch {
            val receipt = ReceiptEntity(
                personId = personId,
                categoryId = categoryId,
                mediaPath = mediaPath,
                mediaType = mediaType,
                isPositive = isPositive,
                note = note,
                thumbnailPath = if (mediaType == "video") generateThumbnail(mediaPath) else null
            )
            val newId = receiptRepository.insert(receipt)

            // Check for threshold email trigger
            categoryId?.let { checkEmailThreshold(it) }

            // Evaluate achievements
            achievementRepository.evaluate(personId)

            // Update auto-profile-picture
            updateAutoAvatar(personId, categoryId)
        }
    }

    private fun generateThumbnail(videoPath: String): String {
        // Use FFmpeg to extract frame at 0.5s
        val thumbPath = videoPath.replace(".mp4", "_thumb.jpg")
        FFmpegKit.execute("-i $videoPath -ss 00:00:00.500 -vframes 1 $thumbPath")
        return thumbPath
    }
}
```

### 5.2 — Feed Screen

```kotlin
// FeedScreen.kt
@Composable
fun FeedScreen(
    personId: Long,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val receipts by viewModel.receipts.collectAsStateWithLifecycle()
    val person by viewModel.person.collectAsStateWithLifecycle()

    LaunchedEffect(personId) { viewModel.load(personId) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            PersonHeader(
                person = person,
                onSwitchPerson = { /* navigate */ },
                onAddPerson = { /* navigate to add */ }
            )
        }
        items(receipts, key = { it.id }) { receipt ->
            ReceiptCard(
                receipt = receipt,
                onLongPress = { showOptions(receipt) }
            )
        }
    }
}
```

### 5.3 — Quick Capture Camera

```kotlin
// CaptureScreen.kt — Camera + Gallery picker
@Composable
fun CaptureScreen(
    preSelectedPersonId: Long? = null,    // set from widget
    viewModel: CaptureViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var isVideoMode by remember { mutableStateOf(false) }
    var selectedPerson by remember { mutableStateOf<PersonEntity?>(null) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    // Camera preview using CameraX
    CameraPreview(
        isVideo = isVideoMode,
        onCapture = { mediaPath, type ->
            if (selectedPerson != null) {
                viewModel.saveReceipt(
                    personId = selectedPerson!!.id,
                    categoryId = selectedCategory?.id,
                    mediaPath = mediaPath,
                    mediaType = type,
                    isPositive = selectedCategory?.isPositive ?: false
                )
            } else {
                // Save to inbox — assign person later
                viewModel.saveToInbox(mediaPath, type)
            }
        }
    )

    // Bottom bar — photo/video toggle + person selector
    Row(modifier = Modifier.align(Alignment.BottomCenter)) {
        PersonSelector(
            persons = viewModel.persons,
            selected = selectedPerson,
            onSelect = { selectedPerson = it }
        )
        Spacer(Modifier.weight(1f))
        ModeToggle(isVideo = isVideoMode, onToggle = { isVideoMode = !isVideoMode })
        SwitchToSelfieButton(onClick = { /* flip camera */ })
        GalleryPickerButton(onClick = { /* open gallery */ })
    }
}
```

---

## 6. Littles Mode (Constructive Mode)

> *"Little things build up into something big."*  
> The original wholesome concept Trash had before the strawberry incident.

Littles mode wraps the same receipt infrastructure in a different UI skin and adds an AI coaching layer.

### 6.1 — Theme Switching

```kotlin
// In UserPreferences.kt
val appMode: Flow<String> = dataStore.data.map { it[APP_MODE_KEY] ?: "receipts" }

suspend fun setMode(mode: String) {
    dataStore.edit { it[APP_MODE_KEY] = mode }
}
```

```kotlin
// In MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mode by userPreferences.appMode.collectAsState(initial = "receipts")

            if (mode == "littles") {
                LittlesTheme { ReceiptsNavHost() }
            } else {
                ReceiptsTheme { ReceiptsNavHost() }
            }
        }
    }
}
```

### 6.2 — Littles Theme

```kotlin
// LittlesTheme.kt
private val LittlesColorScheme = lightColorScheme(
    primary = Color(0xFF7EC8A4),           // Sage green
    onPrimary = Color.White,
    secondary = Color(0xFFFFB6B9),         // Soft blush pink
    background = Color(0xFFF9F6F0),        // Warm cream
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF3D3D3D),
    tertiary = Color(0xFFA8D8EA),          // Sky blue
)

// Icon: a single water droplet 💧
// Tagline: "Little things become big things."
// Tone: gentle, journaling aesthetic, spring colors
```

### 6.3 — Littles Category Model

In Littles mode, categories have a **tension level** and a **growth tag**:

```kotlin
data class LittlesCategory(
    val id: Long,
    val name: String,
    val emoji: String,
    val tensionLevel: Int,             // 1–5: how much friction this creates
    val growthTag: String,             // e.g. "Communication", "Shared Responsibility"
    val isPositive: Boolean,
    val notes: String = ""             // personal journaling notes
)
```

Example default Littles categories:

```
💧 Dishes left in sink          | Tension: 2 | Tag: Shared Responsibility
🧺 Laundry left out             | Tension: 2 | Tag: Shared Responsibility
🗣️ Raised voice during argument | Tension: 4 | Tag: Communication
💛 Said I love you unprompted   | Positive   | Tag: Affection
🍳 Made breakfast for everyone  | Positive   | Tag: Acts of Service
```

---

## 7. Widget Implementation

> *"You don't have time to open the app. You're at the restaurant. They didn't split the bill."*

Using **Jetpack Glance** for the home screen widget.

### 7.1 — Widget Layout

```kotlin
// QuickCaptureWidget.kt
class QuickCaptureWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val persons = currentState<List<String>>()

            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp)
            ) {
                Text(
                    "📸 Quick Receipt",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                )
                Spacer(GlanceModifier.height(8.dp))
                Row {
                    // One button per person (up to 4 on widget)
                    persons.take(4).forEach { personName ->
                        Button(
                            text = personName,
                            onClick = actionStartActivity<CaptureActivity>(
                                actionParametersOf(PERSON_NAME_KEY to personName)
                            ),
                            modifier = GlanceModifier.padding(4.dp)
                        )
                    }
                }
                Spacer(GlanceModifier.height(4.dp))
                // Big shutter button — opens camera immediately
                Button(
                    text = "📷  Snap Receipt",
                    onClick = actionStartActivity<CaptureActivity>(),
                    modifier = GlanceModifier.fillMaxWidth()
                )
            }
        }
    }
}
```

### 7.2 — Widget Receiver

```kotlin
// WidgetReceiver.kt
class WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = QuickCaptureWidget()
}
```

### 7.3 — AndroidManifest Widget Registration

```xml
<receiver
    android:name=".widget.WidgetReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/quick_capture_widget_info" />
</receiver>
```

```xml
<!-- res/xml/quick_capture_widget_info.xml -->
<appwidget-provider
    android:minWidth="250dp"
    android:minHeight="110dp"
    android:updatePeriodMillis="0"
    android:description="@string/widget_description"
    android:widgetCategory="home_screen" />
```

---

## 8. Premium & Monetization

### 8.1 — Google Play Billing Setup

```kotlin
// BillingManager.kt
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    // Product IDs — must match Play Console
    companion object {
        const val SKU_PREMIUM_MONTHLY = "receipts_premium_monthly"   // $2.99/month
        const val SKU_PREMIUM_ANNUAL  = "receipts_premium_annual"    // $19.99/year
        const val SKU_EXTRA_PERSON    = "receipts_extra_person"      // $2.99/month add-on
        const val SKU_LOOT_BOX_COMMON    = "loot_box_common"         // $0.99
        const val SKU_LOOT_BOX_RARE      = "loot_box_rare"           // $1.99
        const val SKU_LOOT_BOX_LEGENDARY = "loot_box_legendary"      // $4.99
    }

    fun launchPremiumFlow(activity: Activity, sku: String) {
        // Standard BillingClient flow
        val productDetails = cachedProducts[sku] ?: return
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            ).build()
        billingClient.launchBillingFlow(activity, params)
    }
}
```

### 8.2 — Premium Screen UI

```kotlin
@Composable
fun PremiumScreen(billingManager: BillingManager) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Text("⚖️ Go Premium", style = MaterialTheme.typography.headlineMedium)
        Text("For people who are serious about their receipts.")

        Spacer(Modifier.height(24.dp))

        // Monthly plan
        PremiumCard(
            title = "Monthly",
            price = "$2.99 / month",
            features = listOf(
                "Unlimited people",
                "Email threshold blasts",
                "Expose hype video",
                "Private group leaderboards",
                "Loot boxes",
                "Uno Reverse mode",
                "Littles AI coaching"
            ),
            onSelect = { billingManager.launchPremiumFlow(activity, SKU_PREMIUM_MONTHLY) }
        )

        Spacer(Modifier.height(16.dp))

        // Annual plan — 40% off badge
        PremiumCard(
            title = "Annual",
            price = "$19.99 / year",
            badge = "40% off",
            note = "Best value. Results not guaranteed.",
            onSelect = { billingManager.launchPremiumFlow(activity, SKU_PREMIUM_ANNUAL) }
        )

        Spacer(Modifier.height(32.dp))

        // Divorce attorney referral footer (organic monetization)
        DivorceLawyerReferralBanner()
    }
}

@Composable
fun DivorceLawyerReferralBanner() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("⚖️", fontSize = 28.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Having second thoughts?", fontWeight = FontWeight.Bold)
                Text(
                    "Find a family law attorney near you.",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { openLawyerLocator() }) {
                    Text("Find attorneys in your area →")
                }
            }
        }
    }
}
```

---

## 9. Loot Box System

> *"Pay to make receipts on the other person's device mysteriously disappear."*

### 9.1 — Loot Box Tiers

```kotlin
enum class LootBoxTier(
    val sku: String,
    val price: String,
    val deletesCount: Int,
    val emoji: String,
    val label: String
) {
    COMMON    ("loot_box_common",    "$0.99", 1, "📦", "Common"),
    RARE      ("loot_box_rare",      "$1.99", 3, "💜", "Rare"),
    LEGENDARY ("loot_box_legendary", "$4.99", 7, "🌟", "Legendary")
}
```

### 9.2 — Loot Box Use Case

```kotlin
// Since the app is local-only in v1, the "loot box" hides receipts
// from your own feed view. In a future shared/sync version,
// it could request deletion from the partner's device.

class UseLootBoxUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository
) {
    suspend fun invoke(tier: LootBoxTier, targetPersonId: Long) {
        val worstReceipts = receiptRepository
            .getMostEmbarrassingReceipts(targetPersonId, limit = tier.deletesCount)

        worstReceipts.forEach { receipt ->
            receiptRepository.markHidden(receipt.id)
        }
    }

    // "Most embarrassing" = most receipts from the same category = repeat offenses
    private suspend fun getMostEmbarrassingReceipts(personId: Long, limit: Int) =
        receiptRepository.getTopCategoryReceipts(personId, limit)
}
```

### 9.3 — Loot Box Dialog

```kotlin
@Composable
fun LootBoxDialog(onDismiss: () -> Unit, onPurchase: (LootBoxTier) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎰 Open a Loot Box") },
        text = {
            Column {
                Text("Make your most embarrassing receipts disappear.")
                Spacer(Modifier.height(12.dp))
                LootBoxTier.values().forEach { tier ->
                    LootBoxRow(tier = tier, onClick = { onPurchase(tier) })
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Never mind") }
        }
    )
}
```

---

## 10. Uno Reverse System

> *"They could cancel it out and say 'Uno Reverse' — maybe hit you with an 11."*

### 10.1 — Reverse Logic

```kotlin
data class EmailBlastRequest(
    val fromPersonId: Long,
    val toPersonId: Long,
    val categoryId: Long,
    val receiptCount: Int,
    val status: String   // "pending", "reversed", "sent"
)

class UnoReverseUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    private val emailThresholdWorker: EmailThresholdWorker
) {
    /**
     * Called when person B tries to blast person A.
     * If person A has >= person B's count on person B, they can reverse.
     */
    suspend fun canReverse(
        targetPersonId: Long,
        initiatorPersonId: Long,
        incomingCount: Int
    ): UnoReverseResult {
        val reverseCount = receiptRepository.getCategoryCount(
            personId = initiatorPersonId,
            targetPersonId = targetPersonId
        )

        return if (reverseCount >= incomingCount) {
            UnoReverseResult.CanReverse(
                reverseCount = reverseCount,
                bonusCount = reverseCount - incomingCount + 1   // the "+1" bonus
            )
        } else {
            UnoReverseResult.CannotReverse
        }
    }

    suspend fun executeReverse(request: EmailBlastRequest): ReverseOutcome {
        // Cancel original blast
        emailThresholdWorker.cancelBlast(request)

        // Fire back with their receipts + 1 bonus
        val reverseRequest = request.copy(
            fromPersonId = request.toPersonId,
            toPersonId = request.fromPersonId,
            receiptCount = request.receiptCount + 1,
            status = "pending"
        )
        emailThresholdWorker.scheduleBlast(reverseRequest)

        return ReverseOutcome.Success(bonusCount = 1)
    }
}
```

### 10.2 — Uno Reverse UI Dialog

```kotlin
@Composable
fun UnoReverseDialog(
    incoming: EmailBlastRequest,
    reverseCount: Int,
    onAccept: () -> Unit,     // take the hit
    onReverse: () -> Unit     // counter-blast
) {
    AlertDialog(
        title = { Text("🔄 Incoming Receipt Blast!") },
        text = {
            Column {
                Text("${incoming.fromPersonId.toName()} is about to send you ${incoming.receiptCount} receipts.")
                Spacer(Modifier.height(8.dp))
                Divider()
                Spacer(Modifier.height(8.dp))
                Text(
                    "You have $reverseCount receipts on them. You can Uno Reverse this.",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "They'll receive ${reverseCount + 1} receipts back.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onReverse,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Text("🔄 UNO REVERSE (+1)")
            }
        },
        dismissButton = {
            TextButton(onClick = onAccept) { Text("Take the hit") }
        }
    )
}
```

---

## 11. Expose Feature (Hype Video)

> *"Like a Google Photos memories thing — but it calls you out."*  
> *"Peter Gabriel's Solsbury Hill playing while dirty sink photos fade in and out."*

### 11.1 — Threshold Check

```kotlin
// Default: unlock Expose button when a person has 20+ receipts in a single category

class TriggerExposeUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository
) {
    suspend fun canExpose(personId: Long): ExposeEligibility {
        val topCategory = receiptRepository.getTopCategory(personId)
        return if ((topCategory?.count ?: 0) >= 20) {
            ExposeEligibility.Ready(topCategory!!)
        } else {
            ExposeEligibility.NotYet(
                current = topCategory?.count ?: 0,
                needed = 20
            )
        }
    }
}
```

### 11.2 — Video Compilation with FFmpeg

```kotlin
// ExposeVideoBuilder.kt
class ExposeVideoBuilder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Builds a shame compilation video:
     * - Slideshow of top-category receipt photos
     * - Each photo shown for 2.5 seconds with a sad cross-dissolve
     * - (Optional) Background audio track from assets
     * - Text overlay: category name + date
     * - Final frame: total count + 😬
     */
    suspend fun buildExposeVideo(
        personName: String,
        categoryName: String,
        receipts: List<ReceiptEntity>
    ): String = withContext(Dispatchers.IO) {

        val outputPath = "${context.filesDir}/expose_${System.currentTimeMillis()}.mp4"
        val photoList = receipts.mapIndexed { i, r ->
            // Scale each image to 1080x1920 with letter-boxing
            "file '${r.mediaPath}'\nduration 2.5"
        }.joinToString("\n")

        val concatFile = "${context.cacheDir}/concat_list.txt"
        File(concatFile).writeText(photoList)

        val audioPath = "${context.filesDir}/assets/sad_piano.mp3"   // bundled asset

        val cmd = buildString {
            append("-f concat -safe 0 -i $concatFile ")
            append("-i $audioPath ")
            append("-vf \"scale=1080:1920:force_original_aspect_ratio=decrease,")
            append("pad=1080:1920:(ow-iw)/2:(oh-ih)/2,")
            append("drawtext=text='$categoryName':fontsize=48:fontcolor=white:")
            append("x=(w-text_w)/2:y=h-100:shadowcolor=black:shadowx=2:shadowy=2\" ")
            append("-c:v libx264 -c:a aac -shortest $outputPath")
        }

        FFmpegKit.execute(cmd)
        outputPath
    }
}
```

### 11.3 — Expose Screen

```kotlin
@Composable
fun ExposeScreen(
    personId: Long,
    viewModel: ExposeViewModel = hiltViewModel()
) {
    val eligibility by viewModel.eligibility.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Text("💥 Expose", style = MaterialTheme.typography.headlineMedium)

        when (val e = eligibility) {
            is ExposeEligibility.NotYet -> {
                Text("${e.needed - e.current} more receipts until you can expose them.")
                LinearProgressIndicator(progress = e.current.toFloat() / e.needed)
            }
            is ExposeEligibility.Ready -> {
                Text("You're ready to expose ${e.category.name}.")
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.buildVideo(personId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("🎬 Generate Expose Video")
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Note: Anthropic's legal team may have opinions about this feature.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

---

## 12. Achievements & Badges

### 12.1 — Achievement Definitions

```kotlin
// AchievementDefinitions.kt
object Achievements {

    val all = listOf(

        // --- Receipt collecting ---
        Achievement("first_receipt",    "📸 First Receipt",        "You took your first receipt. It begins.", "common"),
        Achievement("five_receipts",    "📁 Building a Case",      "5 receipts logged.", "common"),
        Achievement("fifty_receipts",   "🗂️ Evidence Locker",     "50 receipts. Seeking justice.", "rare"),
        Achievement("century",          "💯 The Century",          "100 receipts. You're committed.", "legendary"),

        // --- Repeat offender (same category) ---
        Achievement("repeat_5",        "🔁 Repeat Offender",      "Same category, 5 times.", "common"),
        Achievement("repeat_10",       "🔁🔁 Can't Stop Won't Stop", "Same category, 10 times.", "rare"),
        Achievement("repeat_25",       "♾️ Pattern of Behavior", "25 receipts in one category. It's chronic.", "legendary"),

        // --- Streaks ---
        Achievement("streak_2",        "🔥 On a Roll",            "2-day receipt streak.", "common"),
        Achievement("streak_7",        "🔥🔥 Hot Week",           "7-day streak. Wow.", "rare"),
        Achievement("streak_30",       "☠️ Month of Evidence",   "30-day streak. Seek help.", "legendary"),

        // --- Positive receipts ---
        Achievement("first_positive",  "💛 Caught Them Being Good", "First positive receipt.", "common"),
        Achievement("ten_positives",   "🌻 Actually Nice",        "10 positive receipts. Balance.", "rare"),

        // --- Multi-person ---
        Achievement("three_people",    "👨‍👩‍👧 Extended Network",  "Tracking 3+ people.", "common"),
        Achievement("five_people",     "🕵️ Full Surveillance",   "Tracking 5+ people. Concerning.", "rare"),

        // --- Loot box ---
        Achievement("first_loot",      "📦 Big Spender",          "Opened your first loot box.", "common"),
        Achievement("legendary_loot",  "🌟 Nuclear Option",       "Used a Legendary loot box.", "legendary"),

        // --- Uno Reverse ---
        Achievement("uno_reverse",     "🔄 Not Today",            "Successfully pulled an Uno Reverse.", "rare"),
        Achievement("double_reverse",  "🔄🔄 Uno Wild Card",      "Double reversed the same person.", "legendary"),

        // --- Expose ---
        Achievement("first_expose",    "🎬 Director's Cut",       "Generated your first Expose video.", "legendary"),
    )
}
```

### 12.2 — Achievement Evaluator

```kotlin
class AchievementEvaluator @Inject constructor(
    private val receiptDao: ReceiptDao,
    private val achievementDao: AchievementDao
) {
    suspend fun evaluate(personId: Long) {
        val totalCount  = receiptDao.getTotalCount(personId)
        val topCategory = receiptDao.getTopCategory(personId)
        val streak      = receiptDao.getCurrentStreak(personId)
        val positives   = receiptDao.getPositiveCount(personId)

        val toUnlock = mutableListOf<String>()

        // Check each threshold
        if (totalCount >= 1)   toUnlock += "first_receipt"
        if (totalCount >= 5)   toUnlock += "five_receipts"
        if (totalCount >= 50)  toUnlock += "fifty_receipts"
        if (totalCount >= 100) toUnlock += "century"

        topCategory?.let {
            if (it.count >= 5)  toUnlock += "repeat_5"
            if (it.count >= 10) toUnlock += "repeat_10"
            if (it.count >= 25) toUnlock += "repeat_25"
        }

        if (streak >= 2)  toUnlock += "streak_2"
        if (streak >= 7)  toUnlock += "streak_7"
        if (streak >= 30) toUnlock += "streak_30"

        if (positives >= 1)  toUnlock += "first_positive"
        if (positives >= 10) toUnlock += "ten_positives"

        // Unlock newly earned achievements
        toUnlock.forEach { id ->
            if (!achievementDao.isUnlocked(id)) {
                achievementDao.unlock(id)
                triggerAchievementNotification(id)
            }
        }
    }
}
```

---

## 13. AI Profile Picture System

> *"Your profile picture is automatically set to whatever people have most often snapped of you."*  
> *"You are what you do."*

```kotlin
// AutoAvatarManager.kt
class AutoAvatarManager @Inject constructor(
    private val receiptDao: ReceiptDao,
    private val personDao: PersonDao
) {
    /**
     * Gets the most recent photo from the person's top receipt category.
     * Sets it as their avatar thumbnail.
     */
    suspend fun refreshAutoAvatar(personId: Long) {
        val topCategoryId = receiptDao.getTopCategoryId(personId) ?: return
        val latestReceipt = receiptDao.getLatestInCategory(personId, topCategoryId) ?: return

        val avatarPath = when (latestReceipt.mediaType) {
            "photo" -> latestReceipt.mediaPath
            "video" -> latestReceipt.thumbnailPath ?: return
            else    -> return
        }

        personDao.updateAutoAvatar(personId, avatarPath, topCategoryId)
    }
}
```

```kotlin
// PersonChip.kt
@Composable
fun PersonChip(
    person: PersonEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val avatarPath = person.avatarPath

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(person.name) },
        leadingIcon = {
            if (avatarPath != null) {
                AsyncImage(
                    model = avatarPath,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // Tooltip: shows category label on long press
            } else {
                Text(person.name.first().toString())
            }
        }
    )
}
```

---

## 14. Email Threshold Feature

> *"At 10 photos in a category, email them: 'It's happened another 10 times.'"*  
> *"With divorce attorney ads in the footer."*

### 14.1 — Threshold Worker

```kotlin
// EmailThresholdWorker.kt
@HiltWorker
class EmailThresholdWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val receiptDao: ReceiptDao,
    private val categoryDao: CategoryDao,
    private val premiumRepository: PremiumRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!premiumRepository.isPremium()) return Result.success()

        val personId   = inputData.getLong("person_id", -1)
        val categoryId = inputData.getLong("category_id", -1)
        val email      = inputData.getString("email") ?: return Result.failure()

        val category = categoryDao.getById(categoryId) ?: return Result.failure()
        val count    = receiptDao.getCategoryCount(personId, categoryId)
        val receipts = receiptDao.getRecentsInCategory(personId, categoryId, limit = count)

        val emailBody = buildEmailBody(category.name, count, receipts)
        sendEmail(to = email, subject = "It's happened another $count times.", body = emailBody)

        // Reset counter for next batch
        categoryDao.resetThresholdCount(categoryId)

        return Result.success()
    }

    private fun buildEmailBody(
        categoryName: String,
        count: Int,
        receipts: List<ReceiptEntity>
    ): String = buildString {
        appendLine("Hi,")
        appendLine()
        appendLine("I just wanted to share that \"$categoryName\" has happened $count more times.")
        appendLine()
        appendLine("Here are the receipts (timestamps included):")
        appendLine()
        receipts.forEach { r ->
            appendLine("• ${formatDate(r.timestamp)} — ${r.note.ifEmpty { "(no note)" }}")
        }
        appendLine()
        appendLine("Sent with care via Receipts™")
        appendLine()
        // Monetization footer
        appendLine("─────────────────────────────")
        appendLine("Are you tired of receiving emails like this? 🙂")
        appendLine("Family law resources are available in your area.")
        appendLine("[Find an attorney near you] → https://receipts.app/legal")
    }
}
```

### 14.2 — Threshold Check in Repository

```kotlin
suspend fun checkAndTriggerEmailThreshold(personId: Long, categoryId: Long) {
    val category = categoryDao.getById(categoryId) ?: return
    val count    = receiptDao.getCategoryCount(personId, categoryId)

    if (count > 0 && count % category.threshold == 0) {
        val email = personDao.getEmail(personId) ?: return
        scheduleEmailBlast(personId, categoryId, email)
    }
}

private fun scheduleEmailBlast(personId: Long, categoryId: Long, email: String) {
    val workRequest = OneTimeWorkRequestBuilder<EmailThresholdWorker>()
        .setInputData(
            workDataOf(
                "person_id"   to personId,
                "category_id" to categoryId,
                "email"       to email
            )
        )
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}
```

---

## 15. Private Leaderboards & Groups

> *"Private leaderboards among a group of friends — who's the worst?"*

### 15.1 — Group Data Model

```kotlin
// Groups are local-first. Sharing happens via an export/import JSON mechanism.
// v2 could add optional Supabase sync if user opts in.

data class GroupLeaderboard(
    val groupName: String,
    val members: List<GroupMember>
)

data class GroupMember(
    val displayName: String,
    val totalReceipts: Int,
    val worstCategory: String,
    val streak: Int,
    val badges: List<String>,
    val autoAvatarDescription: String   // e.g. "Dirty sink (12x)"
)
```

### 15.2 — Share Group Profile

```kotlin
// Generates a shareable JSON payload (no server required)
fun exportMyProfile(person: PersonEntity, stats: PersonStats): String {
    val payload = GroupMember(
        displayName          = person.name,
        totalReceipts        = stats.totalReceipts,
        worstCategory        = stats.topCategory?.name ?: "None",
        streak               = stats.currentStreak,
        badges               = stats.unlockedBadges.map { it.emoji },
        autoAvatarDescription = "${stats.topCategory?.name ?: "—"} (${stats.topCategory?.count ?: 0}x)"
    )
    return Json.encodeToString(payload)
}

// Other person imports via QR code or clipboard paste
fun importMemberProfile(json: String): GroupMember =
    Json.decodeFromString(json)
```

### 15.3 — Leaderboard Screen

```kotlin
@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val members by viewModel.members.collectAsStateWithLifecycle()

    LazyColumn {
        item {
            Text("🏆 Who's the Worst?", style = MaterialTheme.typography.headlineMedium)
            Text("Lower is better. Allegedly.", style = MaterialTheme.typography.bodySmall)
        }
        itemsIndexed(members.sortedByDescending { it.totalReceipts }) { index, member ->
            LeaderboardRow(rank = index + 1, member = member)
        }
        item {
            OutlinedButton(
                onClick = viewModel::shareMyProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📤 Share My Profile")
            }
        }
    }
}
```

---

## 16. AI Coaching Layer (Littles Integration)

> *"How can I approach the person in a nice way? Building blocks and dialogue."*

### 16.1 — Coaching Client

```kotlin
// CoachingClient.kt
class CoachingClient @Inject constructor() {
    private val client = OpenAI(token = BuildConfig.OPENAI_API_KEY)

    suspend fun getAdvice(receipt: ReceiptEntity, category: CategoryEntity): CoachingResponse {
        val prompt = CoachingPrompts.buildPrompt(receipt, category)

        val response = client.chat(
            request = chatCompletionRequest {
                model = ModelId("gpt-4o-mini")
                messages {
                    system(CoachingPrompts.SYSTEM_PROMPT)
                    user(prompt)
                }
            }
        )

        return parseCoachingResponse(response.choices.first().message.content ?: "")
    }
}
```

### 16.2 — Coaching Prompts

```kotlin
// CoachingPrompts.kt
object CoachingPrompts {

    const val SYSTEM_PROMPT = """
        You are a calm, empathetic relationship coach. 
        When a user describes something that's been bothering them about someone they live with or 
        care about, you help them:
        1. Validate the feeling without escalating it
        2. Suggest specific, non-accusatory language to bring it up
        3. Anticipate the other person's likely response and how to handle it
        4. Offer a constructive reframe where the behavior might have an innocent explanation
        
        Keep your advice warm, brief, and practical. Avoid therapy-speak. 
        Use natural, conversational language. You're a wise friend, not a clinician.
    """.trimIndent()

    fun buildPrompt(receipt: ReceiptEntity, category: CategoryEntity): String = """
        I noticed something that bothered me: "${category.name}".
        This is the ${category.totalCount} time I've noticed this.
        ${if (receipt.note.isNotEmpty()) "My note was: ${receipt.note}" else ""}
        
        How should I bring this up without it turning into an argument?
        What specific words could I use?
        And what should I expect them to say?
    """.trimIndent()
}
```

### 16.3 — Coaching UI

```kotlin
@Composable
fun LittlesCoachScreen(
    receiptId: Long,
    viewModel: LittlesViewModel = hiltViewModel()
) {
    val advice by viewModel.advice.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Text("💧 Let's Talk About This", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Here's how you might bring this up.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        when (val a = advice) {
            is CoachingState.Loading -> CircularProgressIndicator()
            is CoachingState.Ready   -> {
                // What to say
                CoachCard(
                    title = "💬 Try saying this",
                    content = a.suggestedLanguage,
                    tone = "calm"
                )
                Spacer(Modifier.height(16.dp))
                // What they might say back
                CoachCard(
                    title = "🤔 They might respond",
                    content = a.likelyResponse,
                    tone = "neutral"
                )
                Spacer(Modifier.height(16.dp))
                // Reframe
                CoachCard(
                    title = "🌱 Another way to see it",
                    content = a.constructiveReframe,
                    tone = "positive"
                )
                Spacer(Modifier.height(24.dp))
                // Mark as resolved
                Button(
                    onClick = { viewModel.markResolved(receiptId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✅ We talked about it — mark resolved")
                }
            }
            is CoachingState.Error -> Text("Couldn't load advice. Try again.")
        }
    }
}
```

---

## 17. Onboarding Flow

```kotlin
// OnboardingScreen.kt
sealed class OnboardingPage {
    object Welcome    : OnboardingPage()
    object Privacy    : OnboardingPage()
    object ModeSelect : OnboardingPage()   // Receipts vs. Littles
    object FirstPerson: OnboardingPage()
    object Done       : OnboardingPage()
}

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pagerState = rememberPagerState()

    HorizontalPager(pageCount = 5, state = pagerState) { page ->
        when (page) {
            0 -> WelcomePage()
            1 -> PrivacyPage()     // "Your data never leaves your device."
            2 -> ModeSelectPage()  // Pick Receipts Mode or Littles Mode
            3 -> FirstPersonPage() // Add first person to track
            4 -> DonePage(onComplete)
        }
    }
}

@Composable
fun ModeSelectPage() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("How do you want to use Receipts?")
        Spacer(Modifier.height(24.dp))
        ModeCard(
            emoji = "📸",
            title = "Receipts Mode",
            description = "Document what happened. Get proof. Use at your own risk.",
            onClick = { setMode("receipts") }
        )
        Spacer(Modifier.height(12.dp))
        ModeCard(
            emoji = "💧",
            title = "Littles Mode",
            description = "Track little things. Get coaching. Actually talk about it.",
            onClick = { setMode("littles") }
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "You can switch anytime in Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

---

## 18. Settings & Config Screen

```kotlin
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val mode         by viewModel.mode.collectAsStateWithLifecycle()
    val isPremium    by viewModel.isPremium.collectAsStateWithLifecycle()
    val emailEnabled by viewModel.emailEnabled.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        item { SectionHeader("App Mode") }
        item {
            ToggleRow(
                title = "Littles Mode",
                subtitle = "Switch to constructive / wholesome tracking",
                checked = mode == "littles",
                onToggle = { viewModel.setMode(if (it) "littles" else "receipts") }
            )
        }

        item { SectionHeader("Premium") }
        item {
            if (!isPremium) {
                SettingsRow("⭐ Go Premium", onClick = navigateToPremium)
            } else {
                Text("✅ Premium Active — thank you for supporting Trash Dev.")
            }
        }

        item { SectionHeader("Threshold Emails") }
        item {
            ToggleRow(
                title = "Enable threshold email blasts",
                subtitle = "Premium only — at N receipts, email the subject",
                checked = emailEnabled && isPremium,
                enabled = isPremium,
                onToggle = viewModel::setEmailEnabled
            )
        }
        item {
            SliderRow(
                title = "Receipt threshold",
                value = viewModel.threshold,
                range = 5f..25f,
                onValueChange = viewModel::setThreshold
            )
        }

        item { SectionHeader("Data") }
        item { SettingsRow("📤 Export all receipts",  onClick = viewModel::exportData) }
        item { SettingsRow("🗑️ Delete all data",     onClick = viewModel::confirmDeleteAll) }

        item { SectionHeader("About") }
        item { SettingsRow("⚖️ Find a divorce attorney", onClick = openLawyerLocator) }
        item { SettingsRow("Privacy Policy") }
        item { SettingsRow("Version 1.0.0  •  Built with vibes") }
    }
}
```

---

## 19. Building & Signing the APK

### 19.1 — Generate a Keystore

```bash
# Run once. Keep the keystore file safe. Without it you can't update your app.
keytool -genkey -v \
  -keystore receipts-release-key.jks \
  -alias receipts \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### 19.2 — `gradle.properties` (local, do NOT commit)

```properties
RECEIPTS_STORE_FILE=../receipts-release-key.jks
RECEIPTS_STORE_PASSWORD=your_store_password
RECEIPTS_KEY_ALIAS=receipts
RECEIPTS_KEY_PASSWORD=your_key_password
```

### 19.3 — `build.gradle.kts` Signing Config

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile     = file(project.property("RECEIPTS_STORE_FILE") as String)
            storePassword = project.property("RECEIPTS_STORE_PASSWORD") as String
            keyAlias      = project.property("RECEIPTS_KEY_ALIAS") as String
            keyPassword   = project.property("RECEIPTS_KEY_PASSWORD") as String
        }
    }
    buildTypes {
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            signingConfig     = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### 19.4 — Build Commands

```bash
# Debug APK (for testing, no signing required)
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Release AAB (required for Play Store upload)
./gradlew bundleRelease

# Output locations:
# APK: app/build/outputs/apk/release/app-release.apk
# AAB: app/build/outputs/bundle/release/app-release.aab
```

### 19.5 — Required Permissions in `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.INTERNET" />          <!-- For AI coaching + email -->
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Optional — for location tagging receipts -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

---

## 20. Google Play Submission Checklist

```
[ ] App icon: 512x512 PNG — suggested: a 📸 receipt on a red background
[ ] Feature graphic: 1024x500 PNG
[ ] Screenshots: min 2 phone screenshots (Feed, Capture, Stats, Widget)
[ ] Short description (80 chars): "Timestamped photo & video proof. Use at your own risk."
[ ] Long description: mention Littles mode for wholesome use case
[ ] Content rating: Everyone (no violence, no adult content)
[ ] Privacy policy URL: required — host at receipts.app/privacy
[ ] Data safety form:
    - Data collected: None (all local)
    - Data shared: None
    - Data encrypted in transit: N/A (local only)
[ ] In-app purchases declared: Yes
[ ] Target API: 34 (Android 14)
[ ] AAB uploaded (not APK for Play Store)
[ ] Signed with release keystore
[ ] Billing integration tested via Play test track
[ ] Remove Expose video feature or add "mature content" flag if included
    (edge case: Play Store may flag "compile evidence against a person")
[ ] Legal review: ensure the email threshold feature has a clear opt-in
    from BOTH parties (or clearly scope it as self-only in v1)
```

---

## Appendix A — Feature Roadmap

| Phase | Feature |
|---|---|
| v1.0 | Core receipts, camera, feed, widget, stats, achievements, premium paywall |
| v1.1 | Littles mode + AI coaching layer |
| v1.2 | Email threshold blasts + Uno Reverse |
| v1.3 | Expose hype video |
| v1.4 | Private group leaderboards |
| v2.0 | Optional family sync (Supabase opt-in), Android ↔ iOS shared profiles |
| v2.1 | AR mode (Meta glasses integration — because someone on the pod mentioned it) |

---

## Appendix B — Notes from the Source Material

- **The strawberry leaf** in the sink is the canonical use case. Use it in your app store screenshots.
- **The original "Littles" concept** had a water droplet icon. Consider using 💧 as the Littles mode icon.
- The family plan should **not** recommend the annual subscription. Monthly only. The court date is sooner than you think.
- The expose feature was inspired by **Google Photos memories** and **Sarah McLachlan ASPCA ads**. Both are valid creative references.
- **"You are what you do"** — this line from the pod is the perfect tagline for the auto-avatar feature.
- Trash was banned from his own app after **24 hours**. Ship with that warning prominently displayed.

---

*Built with vibes, launched with consequences.*  
*Receipts™ — Use at your own risk.*
