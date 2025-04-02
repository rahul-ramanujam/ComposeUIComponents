package com.rahulrv.composebottle

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rahulrv.composebottle.extensions.toformattedString
import com.rahulrv.composebottle.util.Recurrence
import com.rahulrv.composebottle.util.TimesOfDay
import com.rahulrv.composebottle.util.getRecurrenceList
import java.text.DateFormatSymbols
import java.util.Calendar.*
import java.util.Date

/**
 * Created by  rahulramanujam On 3/30/25
 *
 */

@Composable
fun MedicationForm() {

    var medicationName by rememberSaveable { mutableStateOf("") }
    var numberOfDosage: String by rememberSaveable { mutableStateOf("1") }
    var recurrence by rememberSaveable { mutableStateOf(Recurrence.Daily.name) }
    var endDate by rememberSaveable { mutableLongStateOf(Date().time) }

    var isMorningSelected by rememberSaveable { mutableStateOf(false) }
    var isAfternoonSelected by rememberSaveable { mutableStateOf(false) }
    var isEveningSelected by rememberSaveable { mutableStateOf(false) }
    var isNightSelected by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
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

        Spacer(modifier = Modifier.padding(4.dp))

        EndDateTextField { endDate = it }

        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = stringResource(R.string.time_of_day),
            style = MaterialTheme.typography.bodyLarge
        )

        var selectionCount by rememberSaveable { mutableIntStateOf(0) }
        val context = LocalContext.current

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            FilterChip(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                selected = isMorningSelected,
                onClick = {
                    handleSelection(
                        isSelected = isMorningSelected,
                        selectionCount = selectionCount,
                        canSelectMoreTimesOfDay = canSelectedMoreTimesOfday(
                            selectionCount,
                            numberOfDosage.toIntOrNull() ?: 0
                        ),
                        onStateChange = { count, selected ->
                            isMorningSelected = selected
                            selectionCount = count
                        },
                        onShowMaxSelectionError = {
                                showMaxSeletionToast(numberOfDosage, context)
                        }
                    )
                },
                label = { Text(text = TimesOfDay.Morning.name) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Selected"
                    )
                }
            )


            FilterChip(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                selected = isAfternoonSelected,
                onClick = {
                    handleSelection(
                        isSelected = isAfternoonSelected,
                        selectionCount = selectionCount,
                        canSelectMoreTimesOfDay = canSelectedMoreTimesOfday(
                            selectionCount,
                            numberOfDosage.toIntOrNull() ?: 0
                        ),
                        onStateChange = { count, selected ->
                            isAfternoonSelected = selected
                            selectionCount = count
                        },
                        onShowMaxSelectionError = {
                            showMaxSeletionToast(numberOfDosage, context)
                        }
                    )
                },
                label = { Text(text = TimesOfDay.Afternoon.name) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Selected"
                    )
                }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            FilterChip(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                selected = isEveningSelected,
                onClick = {
                    handleSelection(
                        isSelected = isEveningSelected,
                        selectionCount = selectionCount,
                        canSelectMoreTimesOfDay = canSelectedMoreTimesOfday(
                            selectionCount,
                            numberOfDosage.toIntOrNull() ?: 0
                        ),
                        onStateChange = { count, selected ->
                            isEveningSelected = selected
                            selectionCount = count
                        },
                        onShowMaxSelectionError = {
                            showMaxSeletionToast(numberOfDosage, context)
                        }
                    )
                },
                label = { Text(text = TimesOfDay.Evening.name) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Selected"
                    )
                }
            )


            FilterChip(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                selected = isNightSelected,
                onClick = {
                    handleSelection(
                        isSelected = isNightSelected,
                        selectionCount = selectionCount,
                        canSelectMoreTimesOfDay = canSelectedMoreTimesOfday(
                            selectionCount,
                            numberOfDosage.toIntOrNull() ?: 0
                        ),
                        onStateChange = { count, selected ->
                            isNightSelected = selected
                            selectionCount = count
                        },
                        onShowMaxSelectionError = {
                            showMaxSeletionToast(numberOfDosage, context)
                        }
                    )
                },
                label = { Text(text = TimesOfDay.Night.name) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Selected"
                    )
                }
            )
        }

    }
}

private fun handleSelection(
    isSelected: Boolean,
    selectionCount: Int,
    canSelectMoreTimesOfDay: Boolean,
    onStateChange: (Int, Boolean) -> Unit,
    onShowMaxSelectionError: () -> Unit
) {
    if (isSelected) {
        onStateChange(selectionCount - 1, !isSelected)
    } else {
        if (canSelectMoreTimesOfDay) {
            onStateChange(selectionCount + 1, !isSelected)
        } else {
            onShowMaxSelectionError()
        }
    }
}

private fun canSelectedMoreTimesOfday(selectionCount: Int, numberOfDosage: Int): Boolean {
    return selectionCount < numberOfDosage
}

private fun showMaxSeletionToast(numberOfDosage: String, context: Context) {
    Toast.makeText(
        context,
        "You're selecting ${(numberOfDosage.toIntOrNull() ?: 0) + 1} times(s) of days which is more than the number of dosage.",
        Toast.LENGTH_LONG
    ).show()
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

@Composable
fun EndDateTextField(endDate: (Long) -> Unit) {

    Text(
        text = stringResource(R.string.end_date),
        style = MaterialTheme.typography.bodyLarge
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()

    val currentDate = Date().toformattedString()
    var selectedDate by rememberSaveable { mutableStateOf(currentDate) }

    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val year: Int = calendar.get(YEAR)
    val month: Int = calendar.get(MONTH)
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val datePickerDialog =
        DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, dayOfMonth)
            selectedDate = "${month.toMonthName()} $dayOfMonth $year"
            endDate(newDate.timeInMillis)
        }, year, month, day)

    TextField(
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        value = selectedDate,
        onValueChange = {},
        trailingIcon = {Icons.Default.DateRange},
        interactionSource = interactionSource
    )

    if(isPressed) {
        datePickerDialog.show()
    }
}

fun Int.toMonthName(): String {
    return DateFormatSymbols().months[this]
}