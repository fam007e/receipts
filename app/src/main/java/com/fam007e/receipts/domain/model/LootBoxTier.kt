package com.fam007e.receipts.domain.model

enum class LootBoxTier(
    val sku: String,
    val price: String,
    val deletesCount: Int,
    val emoji: String,
    val label: String
) {
    BASIC     ("loot_box_basic",     "1 Credit", 1, "📦", "Basic"),
    RARE      ("loot_box_rare",      "3 Credits", 3, "💜", "Rare"),
    LEGENDARY ("loot_box_legendary", "5 Credits", 7, "🌟", "Legendary")
}
