package dev.ryyk.claims.hello

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import kotlin.jvm.optionals.getOrElse

@Component
class GreetingHandler {
    fun hello(request: ServerRequest): Mono<ServerResponse> {

        val name = request.queryParam("name").getOrElse { "Spring" }
        val surname = request.queryParam("surname").getOrElse { "Boot" }

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Greeting("Hello, $name $surname!"))
    }
}