package org.bialydunajec.ddd.domain.base.valueobject

import java.io.Serializable

interface Identifier<ValueType> : ValueObject, Serializable {
    fun getIdentifierValue():ValueType
}