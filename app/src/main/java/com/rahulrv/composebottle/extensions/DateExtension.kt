package com.rahulrv.composebottle.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by  rahulramanujam On 4/1/25
 *
 */
fun Date.toformattedString():String {
    val simpleDateFormat = SimpleDateFormat("LLLL dd, yyyy", Locale.getDefault())
    return simpleDateFormat.format(this)
}