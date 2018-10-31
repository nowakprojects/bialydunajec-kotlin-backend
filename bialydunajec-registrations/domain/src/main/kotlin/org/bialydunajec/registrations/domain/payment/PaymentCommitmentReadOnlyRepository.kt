package org.bialydunajec.registrations.domain.payment

import org.bialydunajec.ddd.domain.base.persistence.ReadOnlyDomainRepository
import org.bialydunajec.registrations.domain.campedition.CampRegistrationsEditionId
import org.bialydunajec.registrations.domain.camper.campparticipant.CampParticipantId

interface PaymentCommitmentReadOnlyRepository
    : ReadOnlyDomainRepository<PaymentCommitment, PaymentCommitmentId> {
}