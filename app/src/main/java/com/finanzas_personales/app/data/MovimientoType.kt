package com.finanzas_personales.app.data

enum class MovimientoType(val value: String) {
  INGRESO("Ingreso"),
  EGRESO("Egreso");

  companion object {
    fun fromString(value: String): MovimientoType? {
      return MovimientoType.entries.firstOrNull { it.value == value }
    }
  }
}