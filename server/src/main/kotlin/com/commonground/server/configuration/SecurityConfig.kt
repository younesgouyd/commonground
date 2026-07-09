package com.commonground.server.configuration

import com.commonground.server.JwtAuthFilter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            // Allow Compose Web development server origin
            allowedOrigins = listOf("http://localhost:8081")

            // Allow standard HTTP methods used by API
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")

            // Allow necessary headers for Content-Type and JWT Authorization tokens
            allowedHeaders = listOf("Authorization", "Content-Type", "Cache-Control")

            // Allow sending credentials (cookies, auth headers, etc.) if required
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // Apply to all API endpoints
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity, jwtFilter: JwtAuthFilter): SecurityFilterChain {
        http
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/v1/auth/**", "/images/**", "/api/v1/ws/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint { _, response, _ ->
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.contentType = "application/json"
                    response.writer.write("""{"error":"Authentication required"}""")
                }
            }
        return http.build()
    }
}

