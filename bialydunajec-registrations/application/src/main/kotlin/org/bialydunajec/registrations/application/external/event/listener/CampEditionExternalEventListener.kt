package org.bialydunajec.registrations.application.external.event.listener

import org.bialydunajec.campedition.messages.event.CampEditionExternalEvent
import org.bialydunajec.ddd.application.base.external.event.ExternalEvent
import org.bialydunajec.ddd.application.base.external.event.ExternalEventListener
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.financial.Money
import org.bialydunajec.registrations.application.command.api.CampRegistrationsCommand
import org.bialydunajec.registrations.application.command.api.CampRegistrationsAdminCommandGateway
import org.bialydunajec.registrations.application.external.event.processor.CampEditionExternalEventProcessor
import org.bialydunajec.registrations.domain.campedition.CampRegistrationsEditionId
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class CampEditionExternalEventListener(
        private val campEditionExternalEventProcessor: CampEditionExternalEventProcessor,
        private val campRegistrationsCommandGateway: CampRegistrationsAdminCommandGateway
) : ExternalEventListener {

    //TODO: Change to ExternalEventProcessors instead of commands!!!
    @EventListener
    override fun handleExternalEvent(externalEvent: ExternalEvent<*>) {
        val payload = externalEvent.payload
        when (payload) {
            is CampEditionExternalEvent.CampEditionCreated -> {
                campEditionExternalEventProcessor.process(payload)
                campRegistrationsCommandGateway.process(
                        CampRegistrationsCommand.CreateCampRegistrationsEdition(
                                CampRegistrationsEditionId(payload.campEditionId),
                                payload.startDate,
                                payload.endDate,
                                Money(payload.totalPrice),
                                payload.downPaymentAmount?.let { Money(it) }
                        )
                )
            }
            is CampEditionExternalEvent.CampEditionDurationUpdated -> {
                campRegistrationsCommandGateway.process(
                        CampRegistrationsCommand.UpdateCampRegistrationsEditionDuration(
                                CampRegistrationsEditionId(payload.campEditionId),
                                payload.startDate,
                                payload.endDate
                        )
                )
            }
        }
    }
}