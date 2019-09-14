package com.cnx.pictionary.models
import com.google.gson.annotations.SerializedName


data class QuestionModel(
    @SerializedName("answer")
    var answer: String?,
    @SerializedName("difficulty")
    var difficulty: Int?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("imageUrl")
    var imageUrl: String?,

    var isAsked : Boolean = false
)



