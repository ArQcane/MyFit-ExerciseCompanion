package com.example.myfit_exercisecompanion.models

import com.google.firebase.firestore.DocumentSnapshot

data class User constructor(
    val username: String,
    val email: String,
    val heightInMetres: Double,
    val weightInKilograms: Double,
    val profilePic: String? = null,
) {
    constructor(doc: DocumentSnapshot, path: String? = null) : this(
        doc.getString("username")!!,
        doc.id,
        doc.getDouble("heightInCentimetres")!! / 100.0,
        doc.getDouble("weightInKilograms")!!,
        path
    )
}