package com.robotutor.iot.utils.filters

import com.robotutor.iot.exceptions.AccessDeniedException
import com.robotutor.iot.exceptions.UnAuthorizedException
import com.robotutor.iot.utils.config.PremisesConfig
import com.robotutor.iot.utils.createMono
import com.robotutor.iot.utils.createMonoError
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.gateway.PremisesGateway
import com.robotutor.iot.utils.gateway.views.PremisesRole
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
class PremisesApiFilter(private val premisesGateway: PremisesGateway, private val premisesConfig: PremisesConfig) :
    WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (premisesConfig.validatedPremisesPaths.any { path -> exchange.request.path.value().startsWith(path) }) {
            return premisesGateway.getPremises(exchange)
                .flatMap { premises ->
                    chain.filter(exchange)
                        .contextWrite { it.put(PremisesData::class.java, premises) }
                }
                .onErrorResume {
                    sendUnAuthorizedException(exchange)
                }
        }
        return chain.filter(exchange)
    }

    private fun sendUnAuthorizedException(exchange: ServerWebExchange): Mono<Void> {
        val unAuthorizedException = AccessDeniedException(IOTError.IOT0103)
        val response = exchange.response

        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON
        val content = response.bufferFactory()
            .wrap(serialize(unAuthorizedException.errorResponse()).toByteArray())
        return response.writeWith(createMono(content))
    }
}

fun getPremisesId(exchange: ServerWebExchange): String {
    return exchange.request.headers[PREMISES_ID_HEADER_KEY]?.first() ?: "missing-premises-id"
}

fun <T : Any> validatePremisesOwner(premisesData: PremisesData, executeIf: () -> Mono<T>): Mono<T> {
    return validatePremisesRole(premisesData, PremisesRole.OWNER, executeIf)
}

fun <T : Any> validatePremisesAdmin(premisesData: PremisesData, executeIf: () -> Mono<T>): Mono<T> {
    return validatePremisesRole(premisesData, PremisesRole.ADMIN, executeIf)
}

fun <T : Any> validatePremisesUser(premisesData: PremisesData, executeIf: () -> Mono<T>): Mono<T> {
    return validatePremisesRole(premisesData, PremisesRole.USER, executeIf)
}

fun <T : Any> validatePremisesBoard(premisesData: PremisesData, executeIf: () -> Mono<T>): Mono<T> {
    return validatePremisesRole(premisesData, PremisesRole.BOARD, executeIf)
}

fun <T : Any> validatePremisesVoice(premisesData: PremisesData, executeIf: () -> Mono<T>): Mono<T> {
    return validatePremisesRole(premisesData, PremisesRole.VOICE, executeIf)
}

fun <T : Any> validatePremisesRole(premisesData: PremisesData, role: PremisesRole, executeIf: () -> Mono<T>): Mono<T> {
    return createMono(isRoleAllowed(premisesData.agent.role, role))
        .flatMap {
            if (it) executeIf()
            else createMonoError(UnAuthorizedException(IOTError.IOT0106))
        }
}

private fun isRoleAllowed(sourceRole: PremisesRole, validateRole: PremisesRole): Boolean {
    return sourceRole.rank >= validateRole.rank
}
