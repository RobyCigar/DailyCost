package com.dcns.dailycost.foundation.common

import java.text.SimpleDateFormat
import java.util.Locale

object CommonDateFormatter {

    /**
     * Date format untuk format/parse tanggal dari local ke api atau sebaliknya
     */
    val api = SimpleDateFormat("dd MMMM yyyy hh:mm:ss", Locale.US)

    /**
     * Date format dengan format "EEEE, dd MMM yyyy"
     */
    val edmy = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.US)

}