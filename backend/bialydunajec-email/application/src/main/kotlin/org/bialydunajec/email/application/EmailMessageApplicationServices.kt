package org.bialydunajec.email.application

import org.bialydunajec.ddd.application.base.ApplicationService
import org.bialydunajec.ddd.application.base.email.SimpleEmailMessage
import org.bialydunajec.ddd.application.base.time.Clock
import org.bialydunajec.ddd.domain.sharedkernel.exception.DomainRuleViolationException
import org.bialydunajec.email.application.api.EmailMessageCommand
import org.bialydunajec.email.domain.EmailMessageDomainRule
import org.bialydunajec.email.domain.EmailMessageLog
import org.bialydunajec.email.domain.EmailMessageLogId
import org.bialydunajec.email.domain.EmailMessageLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class SendEmailMessageApplicationService(
        private val emailMessageSender: EmailMessageSender,
        private val emailMessageLogRepository: EmailMessageLogRepository,
        private val clock: Clock
) : ApplicationService<EmailMessageCommand.SendEmailCommand> {

    override fun execute(command: EmailMessageCommand.SendEmailCommand): EmailMessageLogId {
        val emailMessage = with(command.emailMessage) {
            SimpleEmailMessage(
                    recipient,
                    subject,
                    content
            )
        }
        val emailSendingResult = emailMessageSender.sendEmailMessage(emailMessage)

        val emailMessageLog = with(command.emailMessage) {
            EmailMessageLog(
                    recipient,
                    subject,
                    content,
                    emailMessageLogId ?: EmailMessageLogId()
            )
        }

        when (emailSendingResult) {
            is EmailSendingResult.Success -> {
                emailMessageLog.logSendingSuccess(clock.currentDateTime())
            }
            is EmailSendingResult.Failure -> {
                emailMessageLog.logSendingFailure(emailSendingResult.errorMessage)
            }
        }

        return emailMessageLogRepository.save(emailMessageLog).getAggregateId()
    }
}

@Service
@Transactional
internal class ResendEmailMessageApplicationService(
        private val emailMessageSender: EmailMessageSender,
        private val emailMessageLogRepository: EmailMessageLogRepository,
        private val clock: Clock
) : ApplicationService<EmailMessageCommand.ResendEmailCommand> {

    override fun execute(command: EmailMessageCommand.ResendEmailCommand) {
        val emailMessage = emailMessageLogRepository.findById(command.emailMessageLogId)
                ?: throw DomainRuleViolationException.of(EmailMessageDomainRule.EMAIL_MESSAGE_TO_RESEND_MUST_EXISTS)

        if (emailMessage.isSent()) {
            throw DomainRuleViolationException.of(EmailMessageDomainRule.EMAIL_MESSAGE_WAS_ALREADY_SUCCESSFULLY_SENT)
        }

        with(emailMessage) {
            val emailSendingResult = emailMessageSender.sendEmailMessage(
                    SimpleEmailMessage(getRecipient(), getSubject(), getContent())
            )

            when (emailSendingResult) {
                is EmailSendingResult.Success -> {
                    emailMessage.logSendingSuccess(clock.currentDateTime())
                }
                is EmailSendingResult.Failure -> {
                    emailMessage.logSendingFailure(emailSendingResult.errorMessage)
                }
            }

            emailMessageLogRepository.save(emailMessage)
        }
    }
}

@Service
@Transactional
internal class ForwardEmailMessageApplicationService(
        private val emailMessageSender: EmailMessageSender,
        private val emailMessageLogRepository: EmailMessageLogRepository,
        private val clock: Clock
) : ApplicationService<EmailMessageCommand.ForwardEmailCommand> {

    override fun execute(command: EmailMessageCommand.ForwardEmailCommand) {
        val messageToForward = emailMessageLogRepository.findById(command.emailMessageLogId)
                ?: throw DomainRuleViolationException.of(EmailMessageDomainRule.EMAIL_MESSAGE_TO_RESEND_MUST_EXISTS)

        with(messageToForward) {
            command.recipients.forEach { recipient ->
                val emailMessage = SimpleEmailMessage(recipient, getSubject(), getContent())
                val emailSendingResult = emailMessageSender.sendEmailMessage(emailMessage)

                val emailMessageLog = with(emailMessage) {
                    EmailMessageLog(
                            recipient,
                            subject,
                            content,
                            EmailMessageLogId()
                    )
                }

                when (emailSendingResult) {
                    is EmailSendingResult.Success -> {
                        emailMessageLog.logSendingSuccess(clock.currentDateTime())
                    }
                    is EmailSendingResult.Failure -> {
                        emailMessageLog.logSendingFailure(emailSendingResult.errorMessage)
                    }
                }

                emailMessageLogRepository.save(emailMessageLog)
            }
        }
    }
}


