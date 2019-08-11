package com.example.demo

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient

@RunWith(SpringRunner::class)
@SpringBootTest
class DemoApplicationTests {

    //hede
    private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()

    @Test
    @Ignore
    fun contextLoads() {
        client.get().uri("/api/gokhan").exchange().expectStatus().is2xxSuccessful
    }

}
