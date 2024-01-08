package com.redzuandika.musiqlo.guru

import com.google.gson.annotations.SerializedName

data class PostRespons (
    val id:Int,
    val title : String?,
    @SerializedName("body")
    val text : String?
        )