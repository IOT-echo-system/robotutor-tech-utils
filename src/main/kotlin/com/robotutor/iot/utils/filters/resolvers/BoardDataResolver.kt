package com.robotutor.iot.utils.filters.resolvers

import com.robotutor.iot.exceptions.DataNotFoundException
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.createMonoError
import com.robotutor.iot.utils.gateway.BoardGateway
import com.robotutor.iot.utils.models.BoardData
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class BoardDataResolver(private val boardGateway: BoardGateway) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == BoardData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {

        return Mono.deferContextual { context ->
            val boardId = exchange.request.headers.getFirst("boardId") ?: "boardId"
            boardGateway.isValidBoard()
                .map {
                    BoardData(boardId = boardId, roleId = "")
                }
                .switchIfEmpty(
                    createMonoError(DataNotFoundException(IOTError.IOT0102))
                )
        }
    }
}
