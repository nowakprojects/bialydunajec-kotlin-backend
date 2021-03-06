package org.bialydunajec.campbus.domain

import org.bialydunajec.eventsourcing.domain.givenAggregate
import org.bialydunajec.eventsourcing.domain.toFixed
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.Clock

//TODO: Create DSL for spek, with will use DSL with assertk + descriptions
object SeatSpecification : Spek({

    describe("Feature: Reserve seat in Camp Bus for a passenger") {

        val fixedClock = Clock.systemUTC().toFixed()
        val campBusCourseId = BusCourseId("example-buscourse-id")
        val seatId = SeatId("example-seat-id")
        val passengerId = PassengerId("example-passenger-id")
        val seat: Seat by memoized { Seat.newInstance { fixedClock.instant() } }

        context("Given seat for reservation is added for course") {

            val seatAddedForCourse = SeatEvent.SeatAddedForCourse(seatId, fixedClock.instant(), campBusCourseId)

            describe("When reserve the seat for passenger") {

                val reserveSeat = SeatCommand.ReserveSeat(seatId, passengerId)

                it("Then the seat should be reserved for the passenger") {

                    val seatReservedForPassenger = SeatEvent.SeatReservedForPassenger(seatId, fixedClock.instant(), campBusCourseId, passengerId)

                    givenAggregate { seat }
                            .withPriorEvent { seatAddedForCourse }
                            .whenCommand { reserveSeat }
                            .thenExpectEvent { seatReservedForPassenger }
                }

            }

            context("And the seat already reserved") {

                val seatReserved = SeatEvent.SeatReservedForPassenger(seatId, fixedClock.instant(), campBusCourseId, passengerId)

                describe("When reserve the seat for passenger") {

                    val reserveSeat = SeatCommand.ReserveSeat(seatId, passengerId)

                    it("Then the seat reservation should fail") {

                        val seatReservationFailed = SeatEvent.SeatReservationFailed(
                                seatId,
                                fixedClock.instant(),
                                campBusCourseId,
                                reserveSeat.passengerId,
                                SeatDomainRule.SeatForReservationCannotBeAlreadyReserved)

                        givenAggregate { seat }
                                .withPriorEvents(seatAddedForCourse, seatReserved)
                                .whenCommand { reserveSeat }
                                .thenExpectEvent { seatReservationFailed }

                    }

                }

                context("And the reservation is confirmed") {

                    val reservationConfirmed = SeatEvent.SeatReservationConfirmed(seatId, fixedClock.instant(), campBusCourseId, passengerId)

                    describe("When reserve the seat for passenger") {

                        val reserveSeat = SeatCommand.ReserveSeat(seatId, passengerId)

                        it("Then the seat reservation should fail") {

                            val seatReservationFailed = SeatEvent.SeatReservationFailed(
                                    seatId,
                                    fixedClock.instant(),
                                    campBusCourseId,
                                    reserveSeat.passengerId,
                                    SeatDomainRule.SeatForReservationCannotBeOccupiedByAnotherPassenger)

                            givenAggregate { seat }
                                    .withPriorEvents(seatAddedForCourse, seatReserved, reservationConfirmed)
                                    .whenCommand { reserveSeat }
                                    .thenExpectEvent { seatReservationFailed }

                        }

                    }

                }

            }

            context("And the seat is already removed") {

                val seatRemoved = SeatEvent.SeatRemovedFromCourse(seatId, fixedClock.instant(), campBusCourseId, passengerId)

                describe("When reserve the seat for passenger") {

                    val reserveSeat = SeatCommand.ReserveSeat(seatId, passengerId)

                    it("Then try should fail") {

                        val seatReservationFailed = SeatEvent.SeatReservationFailed(
                                seatId,
                                fixedClock.instant(),
                                campBusCourseId,
                                reserveSeat.passengerId,
                                SeatDomainRule.SeatForReservationCannotBeRemoved)


                        givenAggregate { seat }
                                .withPriorEvents(seatAddedForCourse, seatRemoved)
                                .whenCommand { reserveSeat }
                                .thenExpectEvent { seatReservationFailed }

                    }

                }


            }

        }


    }

})

