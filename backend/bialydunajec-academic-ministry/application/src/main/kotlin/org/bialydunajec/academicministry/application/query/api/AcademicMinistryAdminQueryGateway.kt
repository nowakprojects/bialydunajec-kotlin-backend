package org.bialydunajec.academicministry.application.query.api

import org.bialydunajec.academicministry.application.dto.AcademicMinistryDto
import org.bialydunajec.academicministry.application.dto.toDto
import org.bialydunajec.academicministry.application.query.readmodel.AcademicMinistryDomainModelReader
import org.bialydunajec.ddd.application.base.query.QueryProcessor
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
class AcademicMinistryAdminQueryGateway internal constructor(
        private val domainModelReader: AcademicMinistryDomainModelReader
) : QueryProcessor<AcademicMinistryQuery> {

    override fun process(query: AcademicMinistryQuery): Any? = when(query){
        is AcademicMinistryQuery.All -> process(query)
        AcademicMinistryQuery.NamesForAll -> throw IllegalArgumentException("Command ${query::class.simpleName} not supported!")
        is AcademicMinistryQuery.ById -> process(query)
        is AcademicMinistryQuery.AllPriestsByAcademicMinistryId -> process(query)
    }

    fun process(query: AcademicMinistryQuery.All) =
            domainModelReader.readFor(query)
                    .map { AcademicMinistryDto.from(it) }

    fun process(query: AcademicMinistryQuery.ById) =
            domainModelReader.readFor(query)
                    ?.let { AcademicMinistryDto.from(it) }

    fun process(query: AcademicMinistryQuery.AllPriestsByAcademicMinistryId) =
            domainModelReader.readFor(query)
                    .map { it.toDto() }

}
