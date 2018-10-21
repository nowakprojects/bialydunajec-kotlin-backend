package org.bialydunajec.registrations.domain.shirt.entity

import org.bialydunajec.ddd.domain.base.persistence.IdentifiedEntity
import org.bialydunajec.registrations.domain.camper.campparticipant.CampParticipantId
import org.bialydunajec.registrations.domain.shirt.valueobject.Color
import org.bialydunajec.registrations.domain.shirt.valueobject.ShirtOrderSnapshot
import org.bialydunajec.registrations.domain.shirt.valueobject.ShirtSize
import org.bialydunajec.registrations.domain.shirt.valueobject.ShirtType
import javax.persistence.*

@Entity
@Table(schema = "camp_registrations")
class ShirtOrder(
        @Embedded
        private val campParticipantId: CampParticipantId,

        @OneToOne
        private val colorOption: ShirtColorOption,

        @OneToOne
        private val sizeOption: ShirtSizeOption,

        @Enumerated(EnumType.STRING)
        private val orderedType: ShirtType
) : IdentifiedEntity<ShirtOrderId> {

    @EmbeddedId
    override val entityId: ShirtOrderId = ShirtOrderId()

    fun getSnapshot() =
            ShirtOrderSnapshot(campParticipantId, colorOption.getColor(), sizeOption.getSize())

    // Snapshot tego jakie były wartości np. kolorów w momencie zamawiania
    @Embedded
    @AttributeOverrides(
            AttributeOverride(name = "name", column = Column(name = "colorName"))
    )
    private val orderedColor: Color = colorOption.getColor()

    @Embedded
    @AttributeOverrides(
            AttributeOverride(name = "name", column = Column(name = "sizeName"))
    )
    private val orderedSize: ShirtSize = sizeOption.getSize()


}