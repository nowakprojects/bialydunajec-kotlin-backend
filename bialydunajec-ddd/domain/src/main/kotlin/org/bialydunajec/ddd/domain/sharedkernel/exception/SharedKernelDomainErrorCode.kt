package org.bialydunajec.ddd.domain.sharedkernel.exception

import org.bialydunajec.ddd.domain.base.exception.DomainErrorCode

enum class SharedKernelDomainErrorCode : DomainErrorCode {
    MONEY_VALUE_CANNOT_BE_NEGATIVE_NUMBER,
    CURRENCY_MISMATCH,
    PESEL_NUMBER_IS_NOT_VALID;

    override fun getName() = name;
}