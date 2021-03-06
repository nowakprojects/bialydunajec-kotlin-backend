package org.bialydunajec.academicministry.application.command.api

import org.bialydunajec.academicministry.domain.AcademicMinistryId
import org.bialydunajec.academicministry.domain.entity.AcademicPriestId
import org.bialydunajec.academicministry.domain.valueobject.AcademicPriestSnapshot
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.internet.SocialMedia
import org.bialydunajec.ddd.application.base.command.Command
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.contact.PhoneNumber
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.contact.email.EmailAddress
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.human.FirstName
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.human.LastName
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.human.PersonalTitle
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.internet.Url
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.location.Place
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.notes.ExtendedDescription

sealed class AcademicMinistryCommand : Command {
    data class CreateAcademicMinistry constructor(
            val academicMinistryId: AcademicMinistryId = AcademicMinistryId(),
            val officialName: String,
            val shortName: String?,
            val logoImageUrl: Url?,
            val place: Place?,
            val socialMedia: SocialMedia,
            val emailAddress: EmailAddress?,
            val photoUrl: Url?,
            val description: ExtendedDescription?
    ) : AcademicMinistryCommand()

    data class UpdateAcademicMinistry constructor(
            val academicMinistryId: AcademicMinistryId,
            val officialName: String,
            val shortName: String?,
            val logoImageUrl: Url?,
            val place: Place?,
            val socialMedia: SocialMedia,
            val emailAddress: EmailAddress?,
            val photoUrl: Url?,
            val description: ExtendedDescription?
    ) : AcademicMinistryCommand()

    data class DeactivateAcademicMinistry internal constructor(
            val academicMinistryId: AcademicMinistryId
    ) : AcademicMinistryCommand()

    data class CreateAcademicMinistryPriest(
            val academicPriestId: AcademicPriestId = AcademicPriestId(),
            val academicMinistryId: AcademicMinistryId,
            val firstName: FirstName,
            val lastName: LastName,
            val personalTitle: PersonalTitle?,
            val emailAddress: EmailAddress?,
            val phoneNumber: PhoneNumber?,
            val description: ExtendedDescription?,
            val photoUrl: Url?
    ) : AcademicMinistryCommand()

    data class UpdateAcademicMinistryPriest(
            val academicMinistryId: AcademicMinistryId,
            val priestSnapshot: AcademicPriestSnapshot
    ) : AcademicMinistryCommand()

    data class RemoveAcademicMinistryPriest(
            val academicMinistryId: AcademicMinistryId,
            val academicPriestId: AcademicPriestId
    ) : AcademicMinistryCommand()
}
