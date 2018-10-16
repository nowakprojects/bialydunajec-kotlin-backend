package org.bialydunajec.registrations.presentation.rest.v1.admin

import org.bialydunajec.ddd.application.base.query.api.dto.toValueObject
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.internet.Url
import org.bialydunajec.registrations.application.command.api.CampRegistrationsCommand
import org.bialydunajec.registrations.application.command.api.CampRegistrationsCommandGateway
import org.bialydunajec.registrations.application.dto.CottageDto
import org.bialydunajec.registrations.application.query.api.*
import org.bialydunajec.registrations.application.dto.toValueObject
import org.bialydunajec.registrations.domain.cottage.CottageId
import org.bialydunajec.registrations.presentation.rest.v1.admin.request.UpdateCottageRequest
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/rest-api/v1/camp-registrations/camp-participant")
class CampRegistrationsCampParticipantController(
        private val commandGateway: CampRegistrationsCommandGateway,
        private val queryGateway: CampRegistrationsQueryGateway
) {

    //COMMAND----------------------------------------------------------------------------------------------------------
    @GetMapping("/count")
    fun countCampParticipantsByCottageId(@RequestParam cottageId: String) =
            queryGateway.process(CampParticipantQuery.CountByCottageId(cottageId))

    //QUERY------------------------------------------------------------------------------------------------------------

}