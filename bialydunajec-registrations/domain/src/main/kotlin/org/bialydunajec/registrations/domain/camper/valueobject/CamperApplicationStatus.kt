package org.bialydunajec.registrations.domain.camper.valueobject

/*
Original application sent by camper - entity - @OneToOne with Camper
 */
class CamperApplication(

)

enum class CamperApplicationStatus {
    WAITING_FOR_CONFIRM,
    CONFIRMED_BY_CAMPER,
    CANCELLED_BY_CAMPER,
    CONFIRMED_BY_AUTHORIZED,
    CANCELLED_BY_AUTHORIZED,
    CANCELLED_BY_DEADLINE;
}