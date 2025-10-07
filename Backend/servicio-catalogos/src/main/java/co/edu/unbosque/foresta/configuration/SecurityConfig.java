package co.edu.unbosque.foresta.configuration;

import co.edu.unbosque.foresta.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean AuthenticationEntryPoint restEntryPoint() {
        return (req, res, ex) -> {
            res.setStatus(401); res.setContentType("application/json");
            res.getWriter().write("{\"message\":\"No autenticado\",\"status\":401}");
        };
    }
    @Bean AccessDeniedHandler restDeniedHandler() {
        return (req, res, ex) -> {
            res.setStatus(403); res.setContentType("application/json");
            res.getWriter().write("{\"message\":\"Acceso denegado\",\"status\":403}");
        };
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS
                ))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restEntryPoint())
                        .accessDeniedHandler(restDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/catalogos/paises/**",
                                "/api/catalogos/ciudades/**"
                        ).permitAll()


                        .requestMatchers(HttpMethod.POST,
                                "/api/catalogos/paises/**",
                                "/api/catalogos/ciudades/**",
                                "/api/catalogos/situaciones-economicas/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/catalogos/**").hasRole("ADMIN")


                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
