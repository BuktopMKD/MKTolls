package com.denofdevelopers.mktolls.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denofdevelopers.mktolls.R
import com.denofdevelopers.mktolls.ui.theme.MKTollsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityContent(
    fromLocation: String,
    toLocation: String,
    onFromLocationChange: (String) -> Unit,
    onToLocationChange: (String) -> Unit,
    isFromMyLocation: Boolean,
    isToMyLocation: Boolean,
    onFromMyLocationClick: (Boolean) -> Unit,
    onToMyLocationClick: (Boolean) -> Unit,
    categories: Array<String>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onCalculateClick: () -> Unit,
    isLoading: Boolean
) {
    MKTollsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.backgroundGradientStart),
                            colorResource(id = R.color.backgroundGradientEnd)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .safeDrawingPadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.img_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Start Location Card
                LocationInputCard(
                    title = stringResource(R.string.enter_start_location),
                    value = fromLocation,
                    onValueChange = onFromLocationChange,
                    isMyLocation = isFromMyLocation,
                    onMyLocationClick = onFromMyLocationClick,
                    enabled = !isFromMyLocation
                )

                Spacer(modifier = Modifier.height(16.dp))

                // End Location Card
                LocationInputCard(
                    title = stringResource(R.string.enter_end_location),
                    value = toLocation,
                    onValueChange = onToLocationChange,
                    isMyLocation = isToMyLocation,
                    onMyLocationClick = onToMyLocationClick,
                    enabled = !isToMyLocation
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Category Selection
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = categories[selectedCategoryIndex],
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категорија на возило") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White.copy(alpha = 0.8f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEachIndexed { index, category ->
                            DropdownMenuItem(
                                text = { Text(text = category) },
                                onClick = {
                                    onCategorySelected(index)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Calculate Button
                Button(
                    onClick = onCalculateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = stringResource(R.string.calculate).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationInputCard(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    isMyLocation: Boolean,
    onMyLocationClick: (Boolean) -> Unit,
    enabled: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(title) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                enabled = enabled,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Checkbox(
                    checked = isMyLocation,
                    onCheckedChange = onMyLocationClick,
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = stringResource(R.string.use_my_location),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
