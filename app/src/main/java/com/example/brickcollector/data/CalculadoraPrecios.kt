package com.example.brickcollector.data

object CalculadoraPrecios {

    fun calcularPrecio(piezas: Int): String {
        return when {
            piezas < 350 -> "26.99€"
            piezas < 800 -> "49.99€"
            piezas < 1300 -> "115.99€"
            piezas < 2000 -> "168.99€"
            piezas < 3000 -> "247.99€"
            else -> "449.99€"
        }
    }

    fun calcularPrecioDouble(piezas: Int): Double {
        return when {
            piezas < 350 -> 26.99
            piezas < 800 -> 49.99
            piezas < 1300 -> 115.99
            piezas < 2000 -> 168.99
            piezas < 3000 -> 247.99
            else -> 449.99
        }
    }
}
