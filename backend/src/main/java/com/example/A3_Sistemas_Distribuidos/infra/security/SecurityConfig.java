package com.example.A3_Sistemas_Distribuidos.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import com.example.A3_Sistemas_Distribuidos.web.dto.LoginResponseDTO;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // --- ADICIONE O CORS AO HTTP SECURITY AQUI ---
        // Isso garante que o CorsFilter seja aplicado antes da autorização
        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/funcionario").permitAll() // PreAuthorize no controller
                        .requestMatchers(HttpMethod.GET, "/api/funcionario").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/funcionario/all").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/funcionario/id/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/funcionario/cargo/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/funcionario/nome/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/funcionario/update/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/funcionario/delete/*").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/tarefas").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tarefas").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tarefas/status/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tarefas/supervisor/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tarefas/atendente/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/tarefas/delete/*").authenticated()

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint((request, response, authException) -> {
                        System.out.println("DEBUG: [SecurityConfig] AuthenticationEntryPoint acionado. Motivo: " + authException.getMessage());
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        LoginResponseDTO errorResponse = new LoginResponseDTO(null, "Acesso não autorizado. Token inválido ou ausente.");
                        ObjectMapper mapper = new ObjectMapper();
                        response.getWriter().write(mapper.writeValueAsString(errorResponse));
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        System.out.println("DEBUG: [SecurityConfig] AccessDeniedHandler acionado. Motivo: " + accessDeniedException.getMessage());
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        LoginResponseDTO errorResponse = new LoginResponseDTO(null, "Acesso negado. Você não tem permissão para este recurso.");
                        ObjectMapper mapper = new ObjectMapper();
                        response.getWriter().write(mapper.writeValueAsString(errorResponse));
                    })
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // *** MÉTODO CORRIGIDO para configurar o CORS para o HttpSecurity ***
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // Origens permitidas: seu frontend Node.js
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // O CorsFilter como um Bean separado é geralmente adicionado automaticamente pelo Spring.
    // Mas se não funcionar, podemos tentar adicioná-lo manualmente à cadeia de filtros.
    // Por enquanto, mantenha o @Bean CorsFilter() comentado ou remova-o.
    /*
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
    */
}