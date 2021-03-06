package org.bialydunajec.ddd.domain.base.persistence

import org.bialydunajec.ddd.domain.base.aggregate.AggregateRoot
import org.bialydunajec.ddd.domain.base.specification.Specification
import org.bialydunajec.ddd.domain.sharedkernel.valueobject.Identifier

interface ReadOnlyDomainRepository<AggregateType : AggregateRoot<*, *>, AggregateIdType : Identifier<*>> {
    fun findAll(): Collection<AggregateType>
    fun findById(aggregateId: AggregateIdType): AggregateType?
    fun findByIdAndSpecification(aggregateId: AggregateIdType, specification: Specification<AggregateType>): AggregateType?
    fun findFirstBySpecification(specification: Specification<AggregateType>): AggregateType?
    fun findAllBySpecification(specification: Specification<AggregateType>): Collection<AggregateType>
    fun existsById(aggregateId: AggregateIdType):Boolean
    fun existsByIdAndSpecification(aggregateId: AggregateIdType, specification: Specification<AggregateType>):Boolean
    fun count():Long
    fun isEmpty():Boolean
}
