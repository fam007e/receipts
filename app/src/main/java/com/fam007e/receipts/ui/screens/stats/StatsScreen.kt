package com.fam007e.receipts.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fam007e.receipts.domain.model.Receipt
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val stats       by viewModel.stats.collectAsStateWithLifecycle()
    val globalTotal by viewModel.globalTotal.collectAsStateWithLifecycle()
    val receipts    by viewModel.allReceipts.collectAsStateWithLifecycle()

    val primarySuspect = stats.firstOrNull()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("🕵️ Evidence Board") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF2C2C2C))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "CASE STATUS: ACTIVE",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Red,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                EvidenceMap(receipts)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                EvidenceTag(label = "Total Receipts Logged", value = "$globalTotal")
                EvidenceTag(
                    label = "Primary Suspect",
                    value = primarySuspect?.person?.name ?: "—",
                    color = Color(0xFFF44336)
                )
                EvidenceTag(
                    label = "Longest Streak",
                    value = "${stats.maxOfOrNull { it.streak } ?: 0} days",
                    color = Color(0xFF4CAF50)
                )
            }

            if (stats.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "TOP SUSPECTS",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(stats) { stat ->
                    SuspectCard(stat)
                }
            }
        }
    }
}

@Composable
fun EvidenceMap(receipts: List<Receipt>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(4.0)
                    controller.setCenter(GeoPoint(0.0, 0.0))
                }
            },
            update = { view ->
                view.overlays.clear()
                receipts.forEach { receipt ->
                    if (receipt.latitude != null && receipt.longitude != null) {
                        val marker = Marker(view)
                        marker.position = GeoPoint(receipt.latitude, receipt.longitude)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = receipt.note.ifEmpty { "Receipt" }
                        view.overlays.add(marker)
                    }
                }
                if (receipts.any { it.latitude != null }) {
                    val firstWithLoc = receipts.first { it.latitude != null }
                    view.controller.setCenter(GeoPoint(firstWithLoc.latitude!!, firstWithLoc.longitude!!))
                    view.controller.setZoom(12.0)
                }
                view.invalidate()
            }
        )
    }
}

@Composable
fun EvidenceTag(label: String, value: String, color: Color = Color.White) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
            Text(value, fontWeight = FontWeight.Black, color = color, fontSize = 20.sp)
        }
    }
}

@Composable
fun SuspectCard(stat: PersonStat) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF9C4), RoundedCornerShape(4.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stat.person.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "${stat.totalReceipts} receipts",
                    color = Color(0xFFB71C1C),
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Top offense: ${stat.topCategoryName}   •   Streak: ${stat.streak}d",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
