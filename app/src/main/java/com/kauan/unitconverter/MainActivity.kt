package com.kauan.unitconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.kauan.unitconverter.ui.theme.UnitConverterTheme
import java.math.RoundingMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnitConverterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UnitConverter()
                }
            }
        }
    }
}

@Composable
fun UnitConverter(){

    val inputValue = remember { mutableStateOf("") }

    val outputValue = remember { mutableStateOf("") }

    val inputUnit = remember { mutableStateOf(UnitType.CENTIMETERS) }
    val outputUnit = remember { mutableStateOf(UnitType.METERS) }

    val iExpanded = remember { mutableStateOf(false) }
    val oExpanded = remember { mutableStateOf(false) }

    val headerStyleText = TextStyle(
        fontSize = 32.sp,
        fontFamily = FontFamily.SansSerif,
        color = Color.Red,
        shadow = Shadow(color = Color.Green, blurRadius = 30F)
    )

    fun realizeConversion(){
        val isNumber = inputValue.value.isNotEmpty() && inputValue.value.isDigitsOnly();
        if(isNumber){
            val result = inputUnit.value.conversionValueTo(inputValue.value.toDouble(), outputUnit.value)
                .toBigDecimal().setScale(4, RoundingMode.HALF_UP);
            outputValue.value = result.toString() + " ${outputUnit.value.text}";
        }else{
            outputValue.value = ""
            inputValue.value = ""
        }
    }

    @Composable
    fun GenerateDropdownMenuItem(expanded: MutableState<Boolean>, valueUnit: MutableState<UnitType>, unitType: UnitType){
        DropdownMenuItem(
            text = { Text(unitType.text) },
            onClick = {
                valueUnit.value = unitType
                expanded.value = false
                realizeConversion()
            })
    }

    @Composable
    fun GenerateDropDownMenuUnits(expanded: MutableState<Boolean>, valueUnit: MutableState<UnitType>){

        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            GenerateDropdownMenuItem(expanded, valueUnit, UnitType.CENTIMETERS)
            GenerateDropdownMenuItem(expanded, valueUnit, UnitType.METERS)
            GenerateDropdownMenuItem(expanded, valueUnit, UnitType.FEET)
            GenerateDropdownMenuItem(expanded, valueUnit, UnitType.MILLIMETERS)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text("Unit Converter", style = headerStyleText)

        Spacer(modifier = Modifier.height(Dp(16F)))

        OutlinedTextField(
            value = inputValue.value,
            onValueChange = {
                inputValue.value = it
                realizeConversion()
            },
            label = {Text("Enter value")},
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(Dp(16F)))

        Row {
            Box{
                Button(onClick = { iExpanded.value = true }) {
                    Text("${inputUnit.value}")
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Arrow Down"
                    )
                }
                GenerateDropDownMenuUnits(iExpanded, inputUnit)
            }

            Spacer(modifier = Modifier.width(Dp(16F)))

            Box{
                Button(onClick = { oExpanded.value = true }) {
                    Text("${outputUnit.value}")
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Arrow Down"
                    )
                }

                GenerateDropDownMenuUnits(oExpanded, outputUnit)
            }
        }

        Spacer(modifier = Modifier.height(Dp(16F)))

        Text("Result: ${outputValue.value}", style = MaterialTheme.typography.headlineSmall)
    }
}



@Preview(showBackground = true)
@Composable
fun UnitConverterPreview(){
    UnitConverterTheme {
        UnitConverter()
    }
}

enum class UnitType(val text: String, val conversionFactor: Double){
    CENTIMETERS("Centimeters", 0.01),
    METERS("Meters", 1.0),
    FEET("Feet", 0.3048),
    MILLIMETERS("Millimeters", 0.001);

    fun conversionValueTo(value: Double, to: UnitType): Double{
       return (value * this.conversionFactor) / to.conversionFactor;
    }
}