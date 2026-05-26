package com.commonground.server

import com.commonground.server.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.clearContext()
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            return
        }
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("No token provided")
            return
        }
        val token = authHeader.removePrefix("Bearer ")
        if (!jwtService.validateAccessToken(token)) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Token expired or invalid")
            return
        }
        val userId = jwtService.getUserIdFromAccessToken(token)
        if (userId == null) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            return
        }
        val authToken = UsernamePasswordAuthenticationToken(userId, null, emptyList())
        SecurityContextHolder.getContext().authentication = authToken

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.requestURI.startsWith("/api/v1/auth/")
    }
}