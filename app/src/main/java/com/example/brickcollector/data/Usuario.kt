package com.example.brickcollector.data

import java.io.Serializable

data class Usuario(
    val nombre: String,
    val apellidos: String,
    val email: String
) : Serializable
