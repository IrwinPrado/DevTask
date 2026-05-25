package com.devtask.app.data.local.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Room solo sabe guardar tipos simples (String, Int, Long, etc.)
// Esta clase le enseña a Room cómo convertir LocalDateTime a String y viceversa
// para poder guardarlo en la base de datos
class Converters {

    // Usamos el formato estándar ISO para las fechas (ej: "2024-11-15T08:30:00")
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // Convierte LocalDateTime → String para GUARDAR en la BD
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? =
        value?.format(formatter)

    // Convierte String → LocalDateTime para LEER de la BD
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? =
        value?.let { LocalDateTime.parse(it, formatter) }
}