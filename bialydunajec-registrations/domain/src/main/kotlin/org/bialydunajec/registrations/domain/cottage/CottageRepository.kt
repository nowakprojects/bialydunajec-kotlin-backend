package org.bialydunajec.registrations.domain.cottage

import org.bialydunajec.ddd.domain.base.persistence.DomainRepository
import org.bialydunajec.registrations.domain.campedition.CampRegistrationsEditionId

interface CottageRepository : DomainRepository<Cottage, CottageId> {
    fun findAllByCampRegistrationsEditionId(campRegistrationsEditionId: CampRegistrationsEditionId): Collection<Cottage>
    fun findByIdAndCampRegistrationsEditionId(cottageId: CottageId, campRegistrationsEditionId: CampRegistrationsEditionId): Cottage?
    fun countByCampRegistrationsEditionId(campRegistrationsEditionId: CampRegistrationsEditionId): Long
}