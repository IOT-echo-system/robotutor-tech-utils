package com.robotutor.iot.utils.filters

import com.robotutor.iot.exceptions.AccessDeniedException
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.gateway.PremisesGateway
import com.robotutor.iot.utils.models.PremisesData
import com.robotutor.loggingstarter.serializer.DefaultSerializer.serialize
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(3)
class PremisesApiFilter(private val premisesGateway: PremisesGateway) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestPath = exchange.request.path.pathWithinApplication()
        val regex = Regex("^/premises/\\d+/", RegexOption.IGNORE_CASE)
        if (regex.containsMatchIn(requestPath.value())) {
            val premisesId = requestPath.value().split("/")[2]
            return premisesGateway.getPremises(premisesId)
                .flatMap { premises ->
                    chain.filter(exchange)
                        .contextWrite {
                            it.put(PremisesData::class.java, premises)
                        }
                }
                .onErrorResume {
                    val unAuthorizedException = AccessDeniedException(IOTError.IOT0104)
                    val response = exchange.response

                    response.statusCode = HttpStatus.UNAUTHORIZED
                    response.headers.contentType = MediaType.APPLICATION_JSON
                    response.writeWith(
                        Mono.just(
                            response.bufferFactory()
                                .wrap(serialize(unAuthorizedException.errorResponse()).toByteArray())
                        )
                    )
                    response.setComplete()
                }
        }
        return chain.filter(exchange)
    }
}
