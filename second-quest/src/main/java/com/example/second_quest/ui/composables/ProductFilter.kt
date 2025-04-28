package com.example.second_quest.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.second_quest.domain.model.SortOrder
import com.example.second_quest.utils.convertToPriceDouble

@Composable
fun ProductFilter(
    minPrice: Double?,
    maxPrice: Double?,
    sortOrder: SortOrder,
    onPriceRangeChanged: (Double?, Double?) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Price") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Sort") })
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTab) {
            0 -> {
                CompactPriceRangeFilter(
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    onPriceRangeChanged = onPriceRangeChanged
                )
            }

            1 -> {
                CompactSortOptions(
                    currentSortOrder = sortOrder, onSortOrderChanged = onSortOrderChanged
                )
            }
        }
    }
}

@Composable
fun CompactPriceRangeFilter(
    minPrice: Double?, maxPrice: Double?, onPriceRangeChanged: (Double?, Double?) -> Unit
) {
    var minPriceText by remember(minPrice) { mutableStateOf(minPrice?.toString() ?: "") }
    var maxPriceText by remember(maxPrice) { mutableStateOf(maxPrice?.toString() ?: "") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = minPriceText,
            onValueChange = { value ->
                minPriceText = value
                onPriceRangeChanged(
                    value.convertToPriceDouble(), maxPriceText.convertToPriceDouble()
                )
            },
            label = { Text("Min Price") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = { Text("$", style = MaterialTheme.typography.bodyMedium) })

        OutlinedTextField(
            value = maxPriceText,
            onValueChange = { value ->
                maxPriceText = value
                onPriceRangeChanged(
                    minPriceText.convertToPriceDouble(), value.convertToPriceDouble()
                )
            },
            label = { Text("Max Price") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = { Text("$", style = MaterialTheme.typography.bodyMedium) })
    }
}

@Composable
fun CompactSortOptions(
    currentSortOrder: SortOrder, onSortOrderChanged: (SortOrder) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SortOptionChip(
                text = "Price ↑",
                selected = currentSortOrder == SortOrder.PRICE_ASC,
                onClick = { onSortOrderChanged(SortOrder.PRICE_ASC) })

            SortOptionChip(
                text = "Price ↓",
                selected = currentSortOrder == SortOrder.PRICE_DESC,
                onClick = { onSortOrderChanged(SortOrder.PRICE_DESC) })
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
        ) {
            SortOptionChip(
                text = "Name A-Z",
                selected = currentSortOrder == SortOrder.NAME_ASC,
                onClick = { onSortOrderChanged(SortOrder.NAME_ASC) })

            SortOptionChip(
                text = "Name Z-A",
                selected = currentSortOrder == SortOrder.NAME_DESC,
                onClick = { onSortOrderChanged(SortOrder.NAME_DESC) })
        }
    }
}

@Composable
fun SortOptionChip(
    text: String, selected: Boolean, onClick: () -> Unit
) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable { onClick() }
        .background(
            if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)
        ), contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

}