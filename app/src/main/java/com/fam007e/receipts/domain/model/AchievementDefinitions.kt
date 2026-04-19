package com.fam007e.receipts.domain.model

data class AchievementDef(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val tier: String
)

object AchievementDefinitions {
    val ALL = listOf(
        // Evidence Thresholds
        AchievementDef("first_receipt", "First Case", "Logged your first receipt.", "📄", "BRONZE"),
        AchievementDef("five_receipts", "Evidence Locker", "Logged 5 receipts.", "📁", "BRONZE"),
        AchievementDef("fifty_receipts", "Paper Trail", "Logged 50 receipts.", "📝", "SILVER"),
        AchievementDef("century", "The Century Mark", "Logged 100 receipts.", "💯", "GOLD"),
        
        // Category Repeats
        AchievementDef("repeat_5", "Repeat Offender", "Caught them in the same act 5 times.", "🔁", "BRONZE"),
        AchievementDef("repeat_10", "Habitual", "Caught them in the same act 10 times.", "🔄", "SILVER"),
        AchievementDef("repeat_25", "Pattern of Behavior", "Caught them in the same act 25 times.", "📈", "GOLD"),
        
        // Streaks
        AchievementDef("streak_2", "Double Tap", "Logged receipts 2 days in a row.", "🔥", "BRONZE"),
        AchievementDef("streak_7", "Weekly Audit", "Logged receipts 7 days in a row.", "🎗️", "SILVER"),
        AchievementDef("streak_30", "Internal Affairs", "Logged receipts 30 days in a row.", "🕵️", "GOLD"),
        
        // Positives
        AchievementDef("first_positive", "Silver Lining", "Logged something positive. Rare.", "✨", "BRONZE"),
        AchievementDef("ten_positives", "Optimist", "Logged 10 positive receipts.", "🌈", "SILVER"),
        
        // Multi-Person
        AchievementDef("three_people", "Small Group", "Tracking 3 different people.", "👥", "SILVER"),
        AchievementDef("five_people", "The Whole Fam", "Tracking 5 different people.", "👨‍👩‍👧‍👦", "GOLD"),
        
        // Feature Usage
        AchievementDef("first_loot", "Gacha Luck", "Used your first Loot Box to hide shame.", "🎰", "BRONZE"),
        AchievementDef("uno_reverse", "Actually, No", "Pulled a successful Uno Reverse.", "🔄", "SILVER"),
        AchievementDef("first_expose", "Going Viral", "Generated an Expose shame video.", "🎬", "GOLD")
    )
}
