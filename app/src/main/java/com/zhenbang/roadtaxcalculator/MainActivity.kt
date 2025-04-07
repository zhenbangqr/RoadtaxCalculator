package com.zhenbang.roadtaxcalculator // Replace with your actual package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

// Main Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoadTaxCalculatorTheme { // Use your app's theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoadTaxCalculatorScreen()
                }
            }
        }
    }
}

// Data class to hold region information (optional but good practice)
data class Region(val name: String, val id: String)

val regions = listOf(
    Region("Peninsular Malaysia", "peninsular"),
    Region("Sabah and Sarawak", "sabah_sarawak")
)

// Composable Function for the main screen
@Composable
fun RoadTaxCalculatorScreen() {
    // State variables to hold user input and results
    var carRegNumber by remember { mutableStateOf("") }
    var engineCapacityStr by remember { mutableStateOf("") }
    var selectedRegionId by remember { mutableStateOf(regions[0].id) } // Default to Peninsular
    var calculatedTax by remember { mutableStateOf<Double?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Function to calculate road tax
    fun calculateRoadTax(capacity: Int, regionId: String): Double {
        return when (regionId) {
            "peninsular" -> {
                when {
                    capacity <= 1000 -> 20.00
                    capacity <= 1200 -> 55.00
                    capacity <= 1400 -> 70.00
                    capacity <= 1600 -> 90.00
                    else -> 200.00 // 1601 and above
                }
            }
            "sabah_sarawak" -> {
                when {
                    capacity <= 1000 -> 20.00
                    capacity <= 1200 -> 44.00
                    capacity <= 1400 -> 56.00
                    capacity <= 1600 -> 72.00
                    else -> 160.00 // 1601 and above
                }
            }
            else -> 0.0 // Should not happen with radio buttons
        }
    }

    // Function to handle button click
    fun onCalculateClick() {
        errorMessage = null // Clear previous error
        calculatedTax = null // Clear previous tax

        val capacity = engineCapacityStr.toIntOrNull()
        if (capacity == null || capacity <= 0) {
            errorMessage = "Please enter a valid engine capacity (cc)."
            return
        }

        calculatedTax = calculateRoadTax(capacity, selectedRegionId)
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp) // Spacing between elements
    ) {
        Text("Malaysia Road Tax Calculator", style = MaterialTheme.typography.headlineSmall)

        // Car Registration Number Input
        OutlinedTextField(
            value = carRegNumber,
            onValueChange = { carRegNumber = it.uppercase() }, // Often uppercase
            label = { Text("Car Registration Number") },
            modifier = Modifier.fillMaxWidth()
        )

        // Engine Capacity Input
        OutlinedTextField(
            value = engineCapacityStr,
            onValueChange = { newValue ->
                // Allow only digits
                if (newValue.all { it.isDigit() }) {
                    engineCapacityStr = newValue
                }
            },
            label = { Text("Engine Capacity (cc)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Region Selection (Radio Buttons)
        Text("Select Region:", style = MaterialTheme.typography.bodyLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            regions.forEach { region ->
                Row(
                    Modifier
                        .selectable(
                            selected = (region.id == selectedRegionId),
                            onClick = { selectedRegionId = region.id },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp), // Padding around each radio button + text
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (region.id == selectedRegionId),
                        onClick = null // null recommended for accessibility with Row onClick
                    )
                    Text(
                        text = region.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // Calculate Button
        Button(onClick = { onCalculateClick() }) {
            Text("Calculate Road Tax")
        }

        // Display Result or Error
        Spacer(modifier = Modifier.height(10.dp)) // Add some space before the result

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "", // Elvis operator for safety
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (calculatedTax != null) {
            val format = NumberFormat.getCurrencyInstance(Locale("ms", "MY")) // Malaysian Ringgit format
            Text(
                text = "Calculated Road Tax: ${format.format(calculatedTax)}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes the contact button to the bottom area

        // Contact JPJ Button (Placeholder)
        Button(
            onClick = {
                // TODO: Implement contact functionality (e.g., open Dialer or Email)
                // Example (needs Context, usually from LocalContext.current):
                // val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+60...")) // JPJ Hotline
                // val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:aduan@jpj.gov.my")) // JPJ Email
                // context.startActivity(phoneIntent or emailIntent)
                println("Contact JPJ button clicked - Implement Intent here")
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) // Different color
        ) {
            Text("Contact JPJ")
        }
    }
}

// --- Theme (Placeholder - Use your actual theme) ---
@Composable
fun RoadTaxCalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(), // Or darkColorScheme()
        typography = Typography(), // Define your typography
        content = content
    )
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RoadTaxCalculatorTheme {
        RoadTaxCalculatorScreen()
    }
}