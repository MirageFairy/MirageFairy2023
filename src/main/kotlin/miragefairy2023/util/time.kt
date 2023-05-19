@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun Instant.toLocalDateTime(zoneOffset: ZoneOffset): LocalDateTime = LocalDateTime.ofInstant(this, zoneOffset)
