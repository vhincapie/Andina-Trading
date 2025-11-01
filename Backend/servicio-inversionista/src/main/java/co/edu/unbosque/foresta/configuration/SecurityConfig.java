package co.edu.unbosque.foresta.configuration;

import co.edu.unbosque.foresta.security.InternalApiKeyFilter;
import co.edu.unbosque.foresta.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.*;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final InternalApiKeyFilter internalFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, InternalApiKeyFilter internalFilter) {
        this.jwtFilter = jwtFilter;
        this.internalFilter = internalFilter;
    }

    @Bean
    AuthenticationEntryPoint restEntry() {
        return (req,res,ex)->{
            res.setStatus(401);
            res.setContentType("application/json");
            try { res.getWriter().write("{\"message\":\"No autenticado\",\"status\":401}"); } catch (Exception ignore) {}
        };
    }

    @Bean
    AccessDeniedHandler restDenied() {
        return (req,res,ex)->{
            res.setStatus(403);
            res.setContentType("application/json");
            try { res.getWriter().write("{\"message\":\"Acceso denegado\",\"status\":403}"); } catch (Exception ignore) {}
        };
    }

    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm->sm.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex->ex.authenticationEntryPoint(restEntry()).accessDeniedHandler(restDenied()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/inversionistas/registrar").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/inversionistas/perfil").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/api/inversionistas/actualizar").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/inversionistas/mi/alpaca").hasRole("INVERSIONISTA")
                        .requestMatchers(HttpMethod.GET,"/api/inversionistas/listar").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/inversionistas/*").hasAnyRole("INTERNAL","INVERSIONISTA","COMISIONISTA","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/inversionistas/*/alpaca").hasAnyRole("INTERNAL","INVERSIONISTA","COMISIONISTA","ADMIN")
                        .anyRequest().authenticated()
                )


                .addFilterBefore(internalFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
