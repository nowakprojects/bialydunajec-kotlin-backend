package org.bialydunajec.registrations.domain.campedition.entity

import org.bialydunajec.ddd.domain.base.persistence.IdentifiedEntity
import org.bialydunajec.ddd.domain.base.validation.ValidationResult
import org.bialydunajec.registrations.domain.campedition.CampEditionId
import org.bialydunajec.registrations.domain.campedition.valueobject.CampRegistrationsSnapshot
import org.bialydunajec.registrations.domain.campedition.valueobject.RegistrationsStatus
import org.bialydunajec.registrations.domain.campedition.valueobject.TimerSettings
import org.bialydunajec.registrations.domain.exception.CampRegistrationsDomainRule.*
import java.time.ZonedDateTime
import java.util.Objects.nonNull
import javax.persistence.*

@Entity
//@Table(schema = "camp_registrations")
internal class CampRegistrations constructor(
        campEditionId: CampEditionId,

        @Embedded
        private var timerSettings: TimerSettings = TimerSettings(),

        private var lastStartedAt: ZonedDateTime? = null,
        private var lastSuspendAt: ZonedDateTime? = null,
        private var lastUnsuspendAt: ZonedDateTime? = null,
        private var lastFinishedAt: ZonedDateTime? = null
) : IdentifiedEntity<CampRegistrationsId> {

    @Enumerated(EnumType.STRING)
    private var status: RegistrationsStatus = RegistrationsStatus.UNCONFIGURED_TIMER
    //private var startMethod: StatusChangeMethod = StatusChangeMethod.MANUAL
    //private var endMethod: StatusChangeMethod = StatusChangeMethod.MANUAL

    @EmbeddedId
    override val entityId: CampRegistrationsId = CampRegistrationsId(campEditionId)

    internal fun canupdateTimerSettings(timerSettings: TimerSettings, currentTime: ZonedDateTime): ValidationResult{
        val (startDate, endDate) = timerSettings
        return ValidationResult.buffer()
                .addViolatedRuleIf(
                        IN_PROGRESS_CAMP_REGISTRATIONS_CANNOT_BE_UPDATED,
                        status == RegistrationsStatus.IN_PROGRESS
                )
                .addViolatedRuleIf(
                        FINISHED_CAMP_REGISTRATIONS_CANNOT_BE_UPDATED,
                        status == RegistrationsStatus.FINISHED
                )
                .addViolatedRuleIf(
                        CAMP_REGISTERS_START_DATE_HAS_TO_BE_BEFORE_END_DATE,
                        startDate != null && !startDate.isBefore(endDate)
                )
                .addViolatedRuleIf(
                        CAMP_REGISTERS_END_DATE_HAS_TO_BE_AFTER_START_DATE,
                        endDate != null && !endDate.isAfter(endDate)
                )
                .addViolatedRuleIf(
                        CAMP_REGISTERS_START_DATE_HAS_TO_BE_IN_THE_FUTURE,
                        startDate != null && !startDate.isAfter(currentTime)
                )
                .addViolatedRuleIf(
                        CAMP_REGISTERS_END_DATE_HAS_TO_BE_IN_THE_FUTURE,
                        endDate != null && !endDate.isAfter(currentTime)
                )
                .toValidationResult()
    }

    internal fun updateTimerSettings(timerSettings: TimerSettings, currentTime: ZonedDateTime) {
        canupdateTimerSettings(timerSettings, currentTime)
                .ifInvalidThrowException()

        val isUpdate = this.timerSettings != timerSettings
        if (isUpdate) {
            this.timerSettings = timerSettings
            status = if (nonNull(this.timerSettings.startDate) && status != RegistrationsStatus.SUSPENDED) {
                RegistrationsStatus.CONFIGURED_TIMER
            } else {
                RegistrationsStatus.UNCONFIGURED_TIMER
            }
        }
    }

    internal fun canStartByTimer(currentTime: ZonedDateTime) =
            ValidationResult.buffer()
                    .addViolatedRuleIf(CAMP_REGISTERS_HAS_TO_HAVE_CONFIGURED_TIMER_TO_START_BY_TIMER, status == RegistrationsStatus.CONFIGURED_TIMER)
                    .toValidationResult()

    internal fun startByTimer(currentTime: ZonedDateTime) {
        canStartByTimer(currentTime)
                .ifInvalidThrowException()
        if (timerSettings.startDate != null && currentTime.isEqual(timerSettings.startDate) || currentTime.isAfter(timerSettings.startDate)) {
            status = RegistrationsStatus.IN_PROGRESS
            lastStartedAt = currentTime
        }
    }

    internal fun canFinishByTimer(currentTime: ZonedDateTime) =
            ValidationResult.buffer()
                    .addViolatedRuleIfNot(ONLY_IN_PROGRESS_CAMPERS_REGISTER_CAN_BE_FINISHED, status == RegistrationsStatus.IN_PROGRESS)
                    .toValidationResult()

    internal fun finishByTimer(currentTime: ZonedDateTime) {
        canFinishByTimer(currentTime)
                .ifInvalidThrowException()
        if (timerSettings.endDate != null && currentTime.isEqual(timerSettings.endDate) || currentTime.isAfter(timerSettings.endDate)) {
            status = RegistrationsStatus.FINISHED
            lastFinishedAt = currentTime
        }
    }

    internal fun canStartNow(currentTime: ZonedDateTime) =
            ValidationResult.buffer()
                    .addViolatedRuleIf(FINISHED_CAMP_REGISTRATIONS_CANNOT_START, status == RegistrationsStatus.FINISHED)
                    .addViolatedRuleIf(IN_PROGRESS_CAMP_REGISTRATIONS_CANNOT_START, status == RegistrationsStatus.IN_PROGRESS)
                    .toValidationResult()

    internal fun startNow(currentTime: ZonedDateTime) {
        canStartNow(currentTime)
                .ifInvalidThrowException()

        status = RegistrationsStatus.IN_PROGRESS
        timerSettings = timerSettings.copy(startDate = currentTime)
    }

    internal fun canFinishNow(currentTime: ZonedDateTime) =
            ValidationResult.buffer()
                    .addViolatedRuleIfNot(ONLY_IN_PROGRESS_CAMPERS_REGISTER_CAN_BE_FINISHED, status == RegistrationsStatus.IN_PROGRESS)
                    .toValidationResult()

    internal fun finishNow(currentTime: ZonedDateTime) {
        canFinishNow(currentTime)
                .ifInvalidThrowException()

        status = RegistrationsStatus.FINISHED
        timerSettings = timerSettings.copy(endDate = currentTime)
    }

    internal fun canSuspend() =
            ValidationResult.buffer()
                    .addViolatedRuleIfNot(ONLY_CONFIGURED_TIMER_OR_IN_PROGRESS_REGISTERS_CAN_BE_SUSPENDED, status == RegistrationsStatus.CONFIGURED_TIMER || status == RegistrationsStatus.IN_PROGRESS)
                    .toValidationResult()


    internal fun suspend(currentTime: ZonedDateTime) {
        canSuspend()
                .ifInvalidThrowException()

        status = RegistrationsStatus.SUSPENDED
        lastSuspendAt = currentTime
    }

    internal fun canUnsuspend() =
            ValidationResult.buffer()
                    .addViolatedRuleIfNot(ONLY_SUSPENDED_REGISTERS_CAN_BE_UNSUSPEND, status == RegistrationsStatus.SUSPENDED)
                    .toValidationResult()


    internal fun unsuspend(currentTime: ZonedDateTime) {
        canUnsuspend()
                .ifInvalidThrowException()

        lastUnsuspendAt = currentTime
        if (canStartByTimer(currentTime).isValid()) {
            startByTimer(currentTime)
        }
    }

    fun isInProgress() = status == RegistrationsStatus.IN_PROGRESS

    internal fun getTimerSettings() = timerSettings

    internal fun getSnapshot() = CampRegistrationsSnapshot(
            campRegistrationsId = entityId,
            timerSettings = timerSettings,
            lastStartedAt = lastStartedAt,
            lastSuspendAt = lastSuspendAt,
            lastUnsuspendAt = lastUnsuspendAt,
            lastFinishedAt = lastFinishedAt
    )

}