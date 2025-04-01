package com.rahulrv.composebottle.util

/**
 * Created by  rahulramanujam On 3/31/25
 *
 */
enum class Recurrence {
    Daily,
    Weekly,
    Monthly
}

fun getRecurrenceList(): List<Recurrence> {
    return Recurrence.entries
}