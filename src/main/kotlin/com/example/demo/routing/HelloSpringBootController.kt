package com.example.demo.routing

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by Gokhan Ozgozen on 01-Jul-19.
 */
@RestController
class HelloSpringBootController {
    @RequestMapping(value = "/amsiz")
    fun helloSpringBoot() = "Hello SpringBoot"
}