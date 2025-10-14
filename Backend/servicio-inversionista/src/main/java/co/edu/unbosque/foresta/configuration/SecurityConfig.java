package co.edu.unbosque.foresta.configuration;

import co.edu.unbosque.foresta.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.*;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) { this.jwtFilter = jwtFilter; }

    @Bean
    AuthenticationEntryPoint restEntry() {
        return (req,res,ex)->{ res.setStatus(401); res.setContentType("application/json"); res.getWriter().write("{\"message\":\"No autenticado\",\"status\":401}"); };
    }

    @Bean
    AccessDeniedHandler restDenied() {
        return (req,res,ex)->{ res.setStatus(403); res.setContentType("application/json"); res.getWriter().write("{\"message\":\"Acceso denegado\",\"status\":403}"); };
    }

    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm->sm.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex->ex.authenticationEntryPoint(restEntry()).accessDeniedHandler(restDenied()))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/inversionistas/registrar").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/inversionistas/perfil").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/api/inversionistas/actualizar").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/inversionistas/{id}").hasAnyRole("INVERSIONISTA","COMISIONISTA","ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}