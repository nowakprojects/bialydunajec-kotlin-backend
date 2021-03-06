package org.bialydunajec.registrations.dto

data class ShirtColorOptionDto (
        val shirtColorOptionId: String,
        val color: ColorDto,
        val available: Boolean = true
)