package org.bialydunajec.registrations.readmodel.statistics

import org.bialydunajec.ddd.base.dto.GenderDto
import org.bialydunajec.registrations.messages.event.CampParticipantExternalEvent
import org.bialydunajec.registrations.messages.event.CottageExternalEvent
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
internal class CampRegistrationsEditionStatistics(
        @Id
        val campRegistrationsEditionId: String,
        var cottagesStats: MutableList<CottageStats> = mutableListOf()
) {

    internal class CottageStats(
            val cottageId: String,
            var cottageName: String,
            var academicMinistryId: String?,
            var maleCampParticipantsAmount: Int = 0,
            var femaleCampParticipantsAmount: Int = 0,
            var highSchoolRecentGraduatesAmount: Int = 0
    ) {
        fun getCampParticipantsAmount() = maleCampParticipantsAmount + femaleCampParticipantsAmount
    }

    fun calculateWith(eventPayload: CottageExternalEvent.CottageCreated) {
        with(eventPayload.snapshot) {
            cottagesStats.add(CottageStats(cottageId, name, academicMinistryId))
        }
    }

    fun calculateWith(eventPayload: CampParticipantExternalEvent.CampParticipantRegistered) {
        with(eventPayload.snapshot.currentCamperData) {
            cottagesStats.find { it.cottageId == cottage.cottageId }
                    ?.apply {
                        when (personalData.gender) {
                            GenderDto.FEMALE -> femaleCampParticipantsAmount++
                            GenderDto.MALE -> maleCampParticipantsAmount++
                        }
                        if (camperEducation.isHighSchoolRecentGraduate) {
                            highSchoolRecentGraduatesAmount++
                        }
                    }
        }
    }

}