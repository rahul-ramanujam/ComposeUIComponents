package com.rahulrv.composebottle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahulrv.composebottle.util.Recurrence
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import com.rahulrv.composebottle.util.getRecurrenceList
import kotlin.math.exp

/**
 * Created by  rahulramanujam On 3/30/25
 *
 */

@Composable
fun MedicationForm() {

    var medicationName by rememberSaveable { mutableStateOf("") }
    var numberOfDosage: String by rememberSaveable { mutableStateOf("1") }
    var recurrence by rememberSaveable { mutableStateOf(Recurrence.Daily.name) }

    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = stringResource(R.string.add_medication),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = stringResource(R.string.medication_name),
            style = MaterialTheme.typography.bodyLarge,
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = medicationName,
            onValueChange = {
                medicationName = it
            },
            placeholder = { Text(text = "e.g Hexamine") }
        )

        Spacer(modifier = Modifier.padding(4.dp))

        var isMaxDoseError by rememberSaveable { mutableStateOf(false) }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val maxDose = 3

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = stringResource(R.string.dosage),
                    style = MaterialTheme.typography.bodyLarge
                )

                TextField(
                    modifier = Modifier.width(128.dp),
                    value = numberOfDosage.toString(),
                    onValueChange = {
                        if(it.length < maxDose) {
                            isMaxDoseError = false
                            numberOfDosage = it
                        } else {
                            isMaxDoseError = true
                        }
                    },
                    trailingIcon = {
                        if(isMaxDoseError) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    placeholder = { Text(text = "e.g. 1")},
                    isError = isMaxDoseError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            RecurrenceDropDownMenu {recurrence = it}
        }

        if(isMaxDoseError) {
            Text(
                text = "You cannot have more than 99 dosage per day",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceDropDownMenu(recurrence:(String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy( 8.dp)
    ) {

        Text(
            text = stringResource(R.string.recurrence),
            style = MaterialTheme.typography.bodyLarge
        )

        val options = getRecurrenceList().map {it.name}
        var expanded by remember {mutableStateOf(false)}
        var selectionOptionText by remember { mutableStateOf(options[0]) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {expanded = !expanded}
        ) {

            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = selectionOptionText,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = true)},
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                options.forEach {selectedOption ->
                    DropdownMenuItem(
                        text = {Text(text = selectedOption)},
                        onClick = {
                            selectionOptionText = selectedOption
                            recurrence(selectedOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}