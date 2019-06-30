package com.example.demo.routing

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router


@Configuration
class RoutingConfig {
    private val usernamePathVariable = "username"

    @Bean
    fun routes() = router {
        "/api".nest {
            GET("/gokhan") {
                ServerResponse.ok().body(fromObject(arrayOf(1, 2, 3)))
            }
        }

    }
//    @Bean
//    fun routes(): RouterFunction<ServerResponse> = router {
//        "/api".nest {
//            GET("/gokhan"){
//                ServerResponse.ok().body(fromObject(arrayOf(1, 2, 3)))
//            }
//        }
//
//    }
}

/**
 * Created by Gokhan Ozgozen on 28-Jun-19.
 */
//@Configuration
//class SimpleRoute {
//
//    @Bean
//    fun route() = router{
//        GET("/route")
////        run { { _ -> ServerResponse.ok().body(fromObject(arrayOf(1, 2, 3))) } }
//        ok().body(Flux.range(1,5))
//    }
//
//
//}
