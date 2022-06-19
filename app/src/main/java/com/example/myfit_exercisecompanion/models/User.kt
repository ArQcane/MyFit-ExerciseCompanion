package com.example.myfit_exercisecompanion.models

import com.google.firebase.firestore.DocumentSnapshot

data class User(
    val username: String,
    val heightInMeters: Double,
    val weightInKG: Double,
) {
   constructor(map: DocumentSnapshot) : this(
       map.getString("username")!!,
       map.getDouble("heightInCM")!! / 100.0,
       map.getDouble("weightInKG")!!
   )
}