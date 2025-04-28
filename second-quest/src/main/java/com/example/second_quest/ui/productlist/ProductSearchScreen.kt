package com.example.second_quest.ui.productlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.second_quest.domain.model.Product
import com.example.second_quest.domain.model.SortOrder
import com.example.second_quest.utils.convertToPriceDouble
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSearchScreen(
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var filtersExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error, actionLabel = "Dismiss"
                )
                viewModel.handleEvent(SearchEvent.ErrorDismissed)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        TopAppBar(
            title = {
                Text(
                    "Product Search", style = MaterialTheme.typography.titleLarge
                )
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    SearchBar(
                        query = uiState.searchQuery, onQueryChange = { query ->
                            viewModel.handleEvent(SearchEvent.QueryChanged(query))
                        })
                }

                IconButton(
                    onClick = { filtersExpanded = !filtersExpanded },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = if (filtersExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle filters",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(
                visible = filtersExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CompactFiltersSection(
                    minPrice = uiState.minPrice,
                    maxPrice = uiState.maxPrice,
                    sortOrder = uiState.sortOrder,
                    onPriceRangeChanged = { min, max ->
                        viewModel.handleEvent(SearchEvent.PriceRangeChanged(min, max))
                    },
                    onSortOrderChanged = { sortOrder ->
                        viewModel.handleEvent(SearchEvent.SortOrderChanged(sortOrder))
                    })
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${uiState.products.size} products found",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = { viewModel.handleEvent(SearchEvent.RefreshRequested) },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Refresh", style = MaterialTheme.typography.bodySmall)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    }

                    uiState.products.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(bottom = 12.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "No products found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Try different search criteria",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }

                    else -> {
                        ProductsList(products = uiState.products)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String, onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search products...") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear, contentDescription = "Clear search"
                    )
                }
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun CompactFiltersSection(
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

@Composable
fun ProductsList(products: List<Product>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items = products, key = { it.id }) { product ->
            CompactProductItem(product = product)
        }
    }
}

@Composable
fun CompactProductItem(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(product.thumbnail)
                    .crossfade(true).build(),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    if (product.discountPercentage > 0) {
                        Text(
                            text = " (-${product.discountPercentage}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = product.rating.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = "${product.stock} in stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (product.stock > 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

object Icons {
    object Default {
        val Search = androidx.compose.material.icons.Icons.Default.Search
        val Clear = androidx.compose.material.icons.Icons.Default.Clear
        val Refresh = androidx.compose.material.icons.Icons.Default.Refresh
        val ExpandMore = androidx.compose.material.icons.Icons.Default.KeyboardArrowDown
        val ExpandLess = androidx.compose.material.icons.Icons.Default.KeyboardArrowUp
        val Star = androidx.compose.material.icons.Icons.Default.Star
    }
}