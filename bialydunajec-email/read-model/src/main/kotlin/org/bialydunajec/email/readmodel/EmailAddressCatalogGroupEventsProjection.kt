package org.bialydunajec.email.readmodel

import org.bialydunajec.ddd.application.base.external.event.ExternalEvent
import org.bialydunajec.ddd.application.base.external.event.ExternalEventSubscriber
import org.bialydunajec.ddd.application.base.external.event.SerializedExternalEventListener
import org.bialydunajec.email.messages.event.EmailAddressExternalEvent
import org.bialydunajec.email.messages.event.EmailGroupExternalEvent
import org.springframework.stereotype.Component

@Component
internal class EmailAddressCatalogGroupEventsProjection(
        private val emailAddressCatalogGroupMongoRepository: EmailAddressCatalogGroupMongoRepository,
        private val emailAddressCatalogGroupStatisticsMongoRepository: EmailAddressCatalogGroupStatisticsMongoRepository,
        private val emailAddressCatalogGroupEventStream: EmailAddressCatalogGroupEventStream,
        eventSubscriber: ExternalEventSubscriber
) : SerializedExternalEventListener() {

    init {
        eventSubscriber.subscribe(EmailGroupExternalEvent.EmailGroupCreated::class) {
            processingQueue.process(it)
        }

        eventSubscriber.subscribe(EmailAddressExternalEvent.EmailAddressCatalogizedToEmailGroup::class) {
            processingQueue.process(it)
        }

        eventSubscriber.subscribe(EmailAddressExternalEvent.EmailAddressDeactivated::class) {
            processingQueue.process(it)
        }

        eventSubscriber.subscribe(EmailAddressExternalEvent.EmailAddressBelongingToGroupUpdated::class) {
            processingQueue.process(it)
        }
    }

    override fun processExternalEvent(externalEvent: ExternalEvent<*>) {
        val payload = externalEvent.payload
        when (payload) {
            is EmailGroupExternalEvent.EmailGroupCreated -> {
                with(payload) {
                    emailAddressCatalogGroupMongoRepository.findById(aggregateId).orElseGet { EmailGroup(aggregateId) }
                            .also {
                                it.groupName = name
                            }.also {
                                emailAddressCatalogGroupMongoRepository.save(it)
                            }

                    emailAddressCatalogGroupStatisticsMongoRepository.findById(DEFAULT_EMAIL_ADDRESS_CATALOG_GROUP_STATISTICS_ID)
                            .ifPresent {
                                it.groupsCount++
                                emailAddressCatalogGroupStatisticsMongoRepository.save(it)
                            }
                }.also {
                    emailAddressCatalogGroupEventStream.updateStreamWith(externalEvent)
                }
            }

            is EmailAddressExternalEvent.EmailAddressCatalogizedToEmailGroup -> {
                with(payload) {
                    emailAddressCatalogGroupMongoRepository.findById(emailGroupId).orElseGet { EmailGroup(emailGroupId) }
                            .also {
                                it.groupName = emailGroupName
                                it.emailAddresses?.add(emailAddress)
                            }.also {
                                emailAddressCatalogGroupMongoRepository.save(it)
                            }
                }.also {
                    emailAddressCatalogGroupEventStream.updateStreamWith(externalEvent)
                }
            }

            is EmailAddressExternalEvent.EmailAddressDeactivated -> {
                with(payload) {
                    emailGroupId?.let {
                        emailAddressCatalogGroupMongoRepository.findById(it).get()
                                .also {
                                    it.emailAddresses?.remove(emailAddress)
                                }.also {
                                    emailAddressCatalogGroupMongoRepository.save(it)
                                }
                    }
                }.also {
                    emailAddressCatalogGroupEventStream.updateStreamWith(externalEvent)
                }
            }

            is EmailAddressExternalEvent.EmailAddressBelongingToGroupUpdated -> {
                with(payload) {
                    emailAddressCatalogGroupMongoRepository.findById(emailGroupId).orElseGet { EmailGroup(emailGroupId) }
                            .also {
                                it.groupName = emailGroupName
                                it.emailAddresses?.add(newEmailAddress)
                            }.also {
                                emailAddressCatalogGroupMongoRepository.save(it)
                            }
                }.also {
                    emailAddressCatalogGroupEventStream.updateStreamWith(externalEvent)
                }
            }
        }
    }

}
