package org.bialydunajec.core

import org.bialydunajec.core.presentation.TestService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest-api/v1/test-service")
class TestServiceController(val testService: TestService) {

    @GetMapping
    fun test() = testService.helloWorld()

}