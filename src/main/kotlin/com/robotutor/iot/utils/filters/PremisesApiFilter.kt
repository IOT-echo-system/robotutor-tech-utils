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

const val PREMISES_ID_HEADER_KEY = "x-premises-id"

@Component
@Order(3)
class PremisesApiFilter(private val premisesGateway: PremisesGateway) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val premisesId = getPremisesId(exchange)
        if (premisesId != null) {
            return premisesGateway.getPremises(premisesId, getTraceId(exchange))
                .flatMap { premises ->
                    chain.filter(exchange)
                        .contextWrite { it.put(PremisesData::class.java, premises) }
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
                }
        }
        return chain.filter(exchange)
    }

    private fun getPremisesId(exchange: ServerWebExchange): String? {
        return exchange.request.headers[PREMISES_ID_HEADER_KEY]?.first()
    }
}
