package org.bialydunajec.registrations.presentation.rest.v1.admin.request

import org.bialydunajec.ddd.dto.sharedkernel.PlaceDto
import org.bialydunajec.ddd.domain.base.validation.constraints.NullOrNotBlank
import org.bialydunajec.registrations.dto.BankTransferDetailsDto
import org.bialydunajec.registrations.dto.CampersLimitationsDto
import org.bialydunajec.registrations.dto.CottageBossDto
import org.bialydunajec.registrations.dto.CottageSpaceDto
import javax.validation.Valid
import javax.validation.constraints.NotBlank

data class UpdateCottageRequest(
        @field:NotBlank
        val name: String,
        @field:NullOrNotBlank
        val logoImageUrl: String?,
        @field:NullOrNotBlank
        val buildingPhotoUrl: String?,
        @field:Valid
        val place: PlaceDto?,
        @field:Valid
        val cottageSpace: CottageSpaceDto,
        @field:Valid
        val campersLimitations: CampersLimitationsDto?,
        @field:Valid
        val bankTransferDetails: BankTransferDetailsDto?,
        @field:Valid
        val cottageBoss: CottageBossDto?
)
