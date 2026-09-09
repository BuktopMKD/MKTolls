package com.denofdevelopers.mktolls.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denofdevelopers.mktolls.R
import com.denofdevelopers.mktolls.model.Toll
import com.denofdevelopers.mktolls.ui.theme.MKTollsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    tolls: List<Toll>,
    category: String,
    onBackClick: () -> Unit,
    onShowMapClick: () -> Unit,
    totalDenars: Double,
    totalEuros: Double,
    isLoading: Boolean
) {
    MKTollsTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name), color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFE565))
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onShowMapClick,
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    text = { Text(stringResource(R.string.show_on_map)) },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFE565),
                                Color(0xFFFFA740)
                            )
                        )
                    )
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Total summary card
                    TotalSummaryCard(totalDenars, totalEuros)

                    // Tolls list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = padding.calculateBottomPadding() + 80.dp
                        )
                    ) {
                        items(tolls) { toll ->
                            TollItemCard(toll, category)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TotalSummaryCard(denars: Double, euros: Double) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Вкупен износ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${denars.toInt()}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " ден.",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            Text(
                text = "или ${String.format("%.2f", euros)} €",
                fontSize = 18.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun TollItemCard(toll: Toll, category: String) {
    val (priceDen, priceEur) = getTollPrice(toll, category)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toll.tollNameMk ?: toll.tollName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Патарина",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${priceDen.toInt()} ден.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "${String.format("%.2f", priceEur)} €",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun getTollPrice(toll: Toll, category: String): Pair<Double, Double> {
    val category1A = stringResource(R.string.category_1A)
    val category1B = stringResource(R.string.category_1B)
    val category2 = stringResource(R.string.category_2)
    val category3 = stringResource(R.string.category_3)
    val category4 = stringResource(R.string.category_4)

    return when (category) {
        category1A -> Pair(toll.categoryOneADen, toll.categoryOneAEuro)
        category1B -> Pair(toll.categoryOneDen, toll.categoryOneEuro)
        category2 -> Pair(toll.categoryTwoDen, toll.categoryTwoEuro)
        category3 -> Pair(toll.categoryThreeDen, toll.categoryThreeEuro)
        category4 -> Pair(toll.categoryFourDen, toll.categoryFourEuro)
        else -> Pair(0.0, 0.0)
    }
}
