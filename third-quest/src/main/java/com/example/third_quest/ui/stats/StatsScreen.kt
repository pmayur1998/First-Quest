package com.example.third_quest.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.third_quest.utils.formatCurrency

@Composable
fun StatsScreen(
    onUserClick: (String) -> Unit, viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                StatsContent(uiState = uiState, onUserClick = onUserClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsContent(
    uiState: StatsUiState, onUserClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("E-Commerce Statistics") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total Revenue Section
            item {
                StatCard(
                    title = "Total Revenue", content = {
                        Text(
                            text = uiState.totalRevenue.formatCurrency(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    })
            }

            // Most Expensive Item Section
            item {
                uiState.mostExpensiveItem?.let { item ->
                    StatCard(
                        title = "Most Expensive Order Item", content = {
                            Column {
                                Text("Product ID: ${item.productId}")
                                Text("Quantity: ${item.quantity}")
                                Text("Price Per Unit: ${item.pricePerUnit.formatCurrency()}")
                                Text(
                                    text = "Total Cost: ${item.getTotalCost().formatCurrency()}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        })
                }
            }

            // Unique Product IDs Section
            item {
                StatCard(
                    title = "Unique Product IDs (${uiState.uniqueProductIds.size})", content = {
                        Text(uiState.uniqueProductIds.joinToString(", "))
                    })
            }

            // Product Sales Count Section
            item {
                StatCard(
                    title = "Product Sales Count - Top 5", content = {
                        Column {
                            uiState.productSalesCount.entries.sortedByDescending { it.value }
                                .take(5).forEach { (productId, count) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Product #$productId")
                                        Text("$count units", fontWeight = FontWeight.Bold)
                                    }
                                }
                        }
                    })
            }

            // User Spending Section
            item {
                StatCard(
                    title = "User Spending", content = {
                        Column {
                            uiState.userSpending.forEach { (userId, username, amount) ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    onClick = { onUserClick(userId) }) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(username)
                                        Text(
                                            amount.formatCurrency(), fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    })
            }
        }
    }
}

@Composable
fun StatCard(
    title: String, content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}