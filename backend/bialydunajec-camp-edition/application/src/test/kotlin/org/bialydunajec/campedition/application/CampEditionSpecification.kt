package org.bialydunajec.campedition.application

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import org.bialydunajec.campedition.application.command.api.CampEditionCommand
import org.bialydunajec.campedition.application.command.api.CampEditionCommandGateway
import org.bialydunajec.campedition.application.query.api.CampEditionQuery
import org.bialydunajec.campedition.application.query.api.CampEditionQueryGateway
import org.bialydunajec.campedition.application.query.api.dto.CampEditionDto
import org.bialydunajec.campedition.domain.campedition.CampEdition
import org.bialydunajec.campedition.domain.campedition.CampEditionEvent
import org.bialydunajec.campedition.domain.campedition.CampEditionId
import org.bialydunajec.campedition.domain.campedition.CampEditionRepository
import org.bialydunajec.campedition.domain.exception.CampEditionDomainRule
import org.bialydunajec.ddd.domain.base.event.DomainEventBus
import org.bialydunajec.ddd.domain.base.event.InMemoryDomainEventsRecorder
import org.bialydunajec.ddd.domain.base.persistence.InMemoryDomainRepository
import org.bialydunajec.ddd.domain.base.validation.exception.DomainRule
import org.bialydunajec.ddd.domain.base.validation.exception.DomainRuleViolationException
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.financial.Money
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class CampEditionSpecification {

    private val campEditionGivens = mapOf(
            35 to CampEditionGiven(
                    campEditionNumber = 35,
                    campEditionStartDate = LocalDate.of(2018, 9, 2),
                    campEditionEndDate = LocalDate.of(2018, 9, 16),
                    campEditionPrice = 399.0,
                    campEditionDownPaymentAmount = 99.0
            ),
            36 to CampEditionGiven(
                    campEditionNumber = 36,
                    campEditionStartDate = LocalDate.of(2019, 9, 2),
                    campEditionEndDate = LocalDate.of(2019, 9, 16),
                    campEditionPrice = 419.0,
                    campEditionDownPaymentAmount = 99.0
            )
    )


    @Test
    fun `Given none camp edition exists | When create camp edition | Then camp edition should be created`() {
        val campEdition35 = campEditionGivens.getValue(35)

        campEditions {
            whenExecute(campEdition35.create) thenExpect (campEdition35.created)

            thenResultOf { process(CampEditionQuery.All) }.containsOnly(campEdition35.dto)
            thenResultOf { process(CampEditionQuery.ById(35)) }.isEqualTo(campEdition35.dto)
        }

    }

    @Test
    fun `Given camp edition exists | When create camp edition, which dates do not overlap with the first one | Then second camp edition should be created`() {
        val campEdition35 = campEditionGivens.getValue(35)
        val campEdition36 = campEditionGivens.getValue(36)

        campEditions {
            givenCampEditionExists(campEdition35)

            whenExecute(campEdition36.create) thenExpect (campEdition36.created)

            thenResultOf { process(CampEditionQuery.All) }.containsOnly(campEdition35.dto, campEdition36.dto)
            thenResultOf { process(CampEditionQuery.ById(35)) }.isEqualTo(campEdition35.dto)
            thenResultOf { process(CampEditionQuery.ById(36)) }.isEqualTo(campEdition36.dto)
        }

    }

    @Test
    fun `Given camp edition exists | When update the camp edition duration | Then the camp edition duration should be updated`() {
        val campEdition35 = campEditionGivens.getValue(35)
        val newStartDate = LocalDate.of(2017, 9, 5)
        val newEndDate = LocalDate.of(2017, 9, 10)

        campEditions {
            givenCampEditionExists(campEdition35)

            whenExecute(campEdition35.updateDuration(newStartDate, newEndDate))

            thenPublishedLastly(campEdition35.updated(newStartDate, newEndDate))
            val updated35campEdition = campEdition35.updatedDto(newStartDate, newEndDate)
            thenResultOf { process(CampEditionQuery.All) }.containsOnly(updated35campEdition)
            thenResultOf { process(CampEditionQuery.ById(35)) }.isEqualTo(updated35campEdition)
        }

    }

    @Test
    fun `Given camp edition doesn't exists | When try to update not existing camp edition | Then update should fail`() {
        val campEdition35 = campEditionGivens.getValue(35)
        val newStartDate = LocalDate.of(2017, 9, 5)
        val newEndDate = LocalDate.of(2017, 9, 10)

        campEditions {
            whenExecute(campEdition35.updateDuration(newStartDate, newEndDate))

            thenRuleBroken(CampEditionDomainRule.CAMP_EDITION_TO_UPDATE_MUST_EXISTS)
            thenNothingPublished()
        }

    }

}

class CampEditionGiven(
        val campEditionNumber: Int,
        val campEditionStartDate: LocalDate,
        val campEditionEndDate: LocalDate,
        val campEditionPrice: Double,
        val campEditionDownPaymentAmount: Double
) {

    val create = CampEditionCommand.CreateCampEdition.from(
            campEditionNumber = campEditionNumber,
            campEditionStartDate = campEditionStartDate,
            campEditionEndDate = campEditionEndDate,
            campEditionPrice = campEditionPrice,
            campEditionDownPaymentAmount = campEditionDownPaymentAmount
    )
    val created = CampEditionEvent.CampEditionCreated(
            campEditionId = CampEditionId(campEditionNumber),
            startDate = campEditionStartDate,
            endDate = campEditionEndDate,
            totalPrice = Money(campEditionPrice),
            downPaymentAmount = Money(campEditionDownPaymentAmount)
    )
    val dto = CampEditionDto(
            campEditionId = campEditionNumber.toString(),
            campEditionStartDate = campEditionStartDate,
            campEditionEndDate = campEditionEndDate,
            campEditionYear = campEditionStartDate.year,
            campEditionPrice = campEditionPrice,
            campEditionDownPaymentAmount = campEditionDownPaymentAmount
    )

    fun updateDuration(start: LocalDate, end: LocalDate): CampEditionCommand.UpdateCampEditionDuration =
            CampEditionCommand.UpdateCampEditionDuration(CampEditionId(campEditionNumber), start, end)

    fun updated(start: LocalDate, end: LocalDate): CampEditionEvent.CampEditionDurationUpdated =
            CampEditionEvent.CampEditionDurationUpdated(CampEditionId(campEditionNumber), start, end)

    fun updatedDto(start: LocalDate, end: LocalDate): CampEditionDto =
            CampEditionDto(
                    campEditionId = campEditionNumber.toString(),
                    campEditionStartDate = start,
                    campEditionEndDate = end,
                    campEditionYear = start.year,
                    campEditionPrice = campEditionPrice,
                    campEditionDownPaymentAmount = campEditionDownPaymentAmount
            )
}

class CampEditionTestFixtureScope(
        val commandGateway: CampEditionCommandGateway,
        val queryGateway: CampEditionQueryGateway,
        val domainEvents: InMemoryDomainEventsRecorder,
) {

    private val brokenRules = mutableListOf<DomainRuleViolationException>()

    operator fun invoke(block: (CampEditionTestFixtureScope.() -> Unit)): CampEditionTestFixtureScope = apply { block(this) }

    infix fun givenCampEditionExists(given: CampEditionGiven): CampEditionTestFixtureScope = apply {
        commandGateway.process(given.create)
    }

    private fun <R : Any> collectDomainException(block: () -> R): R? {
        try {
            return block()
        } catch (domainException: DomainRuleViolationException) {
            brokenRules.add(domainException)
        }
        return null
    }

    fun givenCampEditionsExists(vararg givens: CampEditionGiven): CampEditionTestFixtureScope = apply {
        givens.map { commandGateway.process(it.create) }
    }

    infix fun whenExecute(command: () -> CampEditionCommand): CampEditionTestFixtureExpect {
        collectDomainException { commandGateway.process(command()) }
        return CampEditionTestFixtureExpect(queryGateway, domainEvents)
    }

    infix fun whenExecute(command: CampEditionCommand): CampEditionTestFixtureExpect {
        collectDomainException { commandGateway.process(command) }
        return CampEditionTestFixtureExpect(queryGateway, domainEvents)
    }

    fun whenCommands(vararg commands: CampEditionCommand): CampEditionTestFixtureExpect {
        commands.map { collectDomainException { commandGateway.process(it) } }
        return CampEditionTestFixtureExpect(queryGateway, domainEvents)
    }

    infix fun whenCommands(command: CampEditionCommandGateway.() -> Any): CampEditionTestFixtureExpect {
        collectDomainException { command(commandGateway) }
        return CampEditionTestFixtureExpect(queryGateway, domainEvents)
    }

    inline infix fun <reified T : CampEditionEvent> thenPublishedLastly(event: T): CampEditionTestFixtureScope = apply {
        assertThat(domainEvents).publishedLastly<T>().equalsToDomainEvent(event)
    }

    fun thenNothingPublished(): CampEditionTestFixtureScope = apply {
        assertThat(domainEvents).publishedNone()
    }

    inline infix fun <reified R> thenResultOf(query: CampEditionQueryGateway.() -> R) = assertThat(query(queryGateway))

    infix fun thenRuleBroken(domainRule: DomainRule)  = apply {
        assertThat(brokenRules).isNotEmpty()
        assertThat(brokenRules.last().violatedRules).contains(domainRule)
    }
}

class CampEditionTestFixtureExpect(
        queryGateway: CampEditionQueryGateway,
        domainEvents: InMemoryDomainEventsRecorder,
): TestFixtureExpect<CampEditionQuery, CampEditionQueryGateway>(queryGateway, domainEvents)


private fun campEditions(block: (CampEditionTestFixtureScope.() -> Unit)? = null): CampEditionTestFixtureScope {
    val externalEvents = anExternalEventPublisher()
    val domainEvents = anDomainEventBus()
    val repository = InMemoryCampEditionRepository(domainEvents)
    val configuration = CampEditionConfiguration(repository, externalEvents)
    val commandGateway = configuration.campEditionCommandGateway()
    val queryGateway = configuration.campEditionQueryGateway()
    val fixture = CampEditionTestFixtureScope(commandGateway, queryGateway, domainEvents)
    block?.invoke(fixture)
    return fixture
}


class InMemoryCampEditionRepository(domainEventBus: DomainEventBus)
    : InMemoryDomainRepository<CampEditionId, CampEdition>(domainEventBus = domainEventBus), CampEditionRepository


