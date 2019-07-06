package org.bialydunajec.campedition.application.command.api

import org.bialydunajec.campedition.infrastructure.persistence.jpa.DbCampEditionId
import org.bialydunajec.ddd.application.base.command.Command
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.financial.Money
import java.time.LocalDate

sealed class CampEditionCommand : Command {
    data class CreateCampEdition internal constructor(
            val campEditionId: DbCampEditionId,
            val campEditionStartDate: LocalDate,
            val campEditionEndDate: LocalDate,
            val campEditionPrice: Money,
            val campEditionDownPaymentAmount: Money?
    ) : CampEditionCommand() {
        companion object {
            fun from(
                    campEditionNumber: Int,
                    campEditionStartDate: LocalDate,
                    campEditionEndDate: LocalDate,
                    campEditionPrice: Double,
                    campEditionDownPaymentAmount: Double?
            ) =
                    CreateCampEdition(
                            DbCampEditionId(campEditionNumber),
                            campEditionStartDate,
                            campEditionEndDate,
                            Money(campEditionPrice),
                            campEditionDownPaymentAmount?.let { Money(it) }
                    )
        }
    }

    data class UpdateCampEditionDuration(
            val campEditionId: DbCampEditionId,
            val campEditionStartDate: LocalDate,
            val campEditionEndDate: LocalDate
    ) : CampEditionCommand() {
        companion object {
            fun from(campEditionNumber: Int, campEditionStartDate: LocalDate, campEditionEndDate: LocalDate) =
                    UpdateCampEditionDuration(DbCampEditionId(campEditionNumber), campEditionStartDate, campEditionEndDate)
        }
    }
}
