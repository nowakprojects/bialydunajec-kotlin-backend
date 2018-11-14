package org.bialydunajec.email.readmodel

import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.bialydunajec.ddd.application.base.external.event.ExternalEvent
import org.bialydunajec.ddd.application.base.external.event.ExternalEventListener
import org.bialydunajec.ddd.domain.extensions.toStringOrNull
import org.bialydunajec.email.messages.event.EmailMessageExternalEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

const val EMAIL_STATUS_PENDING = "PENDING"
const val EMAIL_STATUS_SENT = "SENT"
const val EMAIL_STATUS_FAIL_TO_SEND = "FAIL_TO_SEND"

@Component
internal class EmailMessageLogEventsProjection(
        private val emailMessageRepository: EmailMessageRepository,
        private val emailMessageStatisticsRepository: EmailMessageStatisticsRepository
) : ExternalEventListener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val subject = PublishSubject.create<ExternalEvent<*>>().toSerialized()

    init {
        subject
                .observeOn(Schedulers.single())
                .doOnNext { log.info("Processing event with payload: {}", it.payload) }
                .subscribe { processExternalEventPayload(it) }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    override fun handleExternalEvent(externalEvent: ExternalEvent<*>) {
        subject.onNext(externalEvent)
    }

    fun processExternalEventPayload(externalEvent: ExternalEvent<*>) {
        val payload = externalEvent.payload
        when (payload) {
            is EmailMessageExternalEvent.EmailMessageCreated -> {
                with(payload) {
                    emailMessageRepository.findById(emailMessageLogId)
                            .orElseGet { EmailMessage(emailMessageLogId) }
                            .also {
                                it.recipient = recipientEmailAddress
                                it.subject = subject
                                it.content = content
                                if (it.status == null) {
                                    it.status = EMAIL_STATUS_PENDING
                                }
                            }.also {
                                emailMessageRepository.save(it)
                            }

                    emailMessageStatisticsRepository.findById(DEFAULT_EMAIL_MESSAGE_STATISTICS_ID)
                            .ifPresent {
                                it.messagesCount++
                                emailMessageStatisticsRepository.save(it)
                            }
                }
            }

            is EmailMessageExternalEvent.EmailMessageSentSuccess -> {
                with(payload) {
                    emailMessageRepository.findById(emailMessageLogId)
                            .orElseGet { EmailMessage(emailMessageLogId) }
                            .also {
                                it.status = EMAIL_STATUS_SENT
                                it.sentDate = sentDate.toStringOrNull()
                            }.also {
                                emailMessageRepository.save(it)
                            }.also { it ->
                                emailMessageStatisticsRepository.findById(DEFAULT_EMAIL_MESSAGE_STATISTICS_ID)
                                        .ifPresent { statistics ->
                                            statistics.sentSuccessCount++
                                            if (it.status == EMAIL_STATUS_FAIL_TO_SEND) {
                                                statistics.sentSuccessCount--
                                            }
                                            emailMessageStatisticsRepository.save(statistics)
                                        }
                            }
                }
            }

            is EmailMessageExternalEvent.EmailMessageSentFailure -> {
                with(payload) {
                    emailMessageRepository.findById(emailMessageLogId)
                            .orElseGet { EmailMessage(emailMessageLogId) }
                            .also {
                                it.status = EMAIL_STATUS_FAIL_TO_SEND
                                it.lastError = lastError
                            }.also {
                                emailMessageRepository.save(it)
                            }

                    emailMessageStatisticsRepository.findById(DEFAULT_EMAIL_MESSAGE_STATISTICS_ID)
                            .ifPresent {
                                it.sentFailureCount++
                                emailMessageStatisticsRepository.save(it)
                            }
                }
            }
        }

        log.info("Projected external event with payload: {}", externalEvent.payload.toString())
    }
}