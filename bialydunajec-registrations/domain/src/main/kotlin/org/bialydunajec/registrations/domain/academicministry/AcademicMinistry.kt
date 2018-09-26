package org.bialydunajec.registrations.domain.academicministry

import org.bialydunajec.ddd.domain.base.aggregate.AggregateRoot
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.Url
import javax.persistence.Entity
import javax.validation.constraints.NotBlank

/**
 * Academic ministry in Camp Registrations Bounded Context
 */
@Entity
class AcademicMinistry(
        academicMinistryId: AcademicMinistryId,
        @NotBlank
        var shortName: String,
        var logoUrl: Url.InternalUrl? = null
) : AggregateRoot<AcademicMinistryId, AcademicMinistryEvent>(academicMinistryId)