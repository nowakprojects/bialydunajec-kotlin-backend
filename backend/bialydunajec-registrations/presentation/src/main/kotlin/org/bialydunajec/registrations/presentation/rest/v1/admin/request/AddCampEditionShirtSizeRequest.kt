package org.bialydunajec.registrations.presentation.rest.v1.admin.request

import org.bialydunajec.registrations.dto.ShirtSizeDto

data class AddCampEditionShirtSizeRequest(
        val size: ShirtSizeDto,
        val available: Boolean
)