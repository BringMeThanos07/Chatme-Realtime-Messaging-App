package com.example.chatme

import com.google.firebase.firestore.FieldValue

data class User(
    val name:String,
    val imageUrl: String,
    val thumbImage:String, //extension
    val uid: String,
    val deviceToken: String,
    val status:String,
    val onlineStatus: String


) {
    //for firebase we always need an empty constructor
    constructor() : this("","","","","","","")

    constructor(name: String,imageUrl: String,thumbImage: String,uid: String) : this(name,imageUrl,thumbImage,uid,"","Hey, there i am using Chatme ","")
}