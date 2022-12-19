package com.society.myFinances.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Costs(
    var cost: Int? = null,
    var comment: String? = null,
    var uid: Int? = null,
    var date: String? = null
)