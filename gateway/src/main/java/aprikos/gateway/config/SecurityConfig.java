package aprikos.gateway.config;

import aprikos.gateway.config.properties.AzureActiveDirectoryProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

// based on:
// https://github.com/bogdanmarculescu/2025microservices/blob/main/gateway/src/main/java/org/cards/gateway/configurations/SecurityConfig.java
// https://gitlab.com/markozivkovic95/azure-ad-auth-demo/-/blob/main/src/main/java/com/demo/azure_ad_auth_demo/configurations/SecurityConfiguration.java?ref_type=heads

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final AzureActiveDirectoryProperties azureActiveDirectoryProperties;

    public SecurityConfig(final AzureActiveDirectoryProperties azureActiveDirectoryProperties) {
        this.azureActiveDirectoryProperties = azureActiveDirectoryProperties;
    }

    // Allow actuator/health without JWT (for Consul health checks).
    @Bean
    @Order(0)
    public SecurityWebFilterChain actuatorSecurity(ServerHttpSecurity http) {
        http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/actuator/**", "/health"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll());
        return http.build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity serverHttpSecurity
    ) {
        serverHttpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange ->
                        exchange
                                .pathMatchers("/health").permitAll()
                                .pathMatchers("/api/chat/**").permitAll()
                                .pathMatchers("/api/user/**").permitAll()
                                .pathMatchers("/api/canvas/**").permitAll()
                                .pathMatchers("/api/settings/**").permitAll()
                                .pathMatchers("/api/v1/**").permitAll()
                                .anyExchange().authenticated()
                ).oauth2ResourceServer( oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return serverHttpSecurity.build();
    }

    // Global CORS configuration applied to all endpoints.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://localhost:5173")); // your frontend origin(s)
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setExposedHeaders(List.of("Authorization"));
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(this.azureActiveDirectoryProperties.getIssuerUri()).build();
    }
}
