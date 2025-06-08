package com.example.A3_Sistemas_Distribuidos.config; // Verifique se este é o pacote correto

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica o CORS a todos os endpoints do seu backend
                        .allowedOrigins("http://localhost:3000") // <--- ESTA LINHA É A CHAVE. DEVE SER EXATAMENTE A URL DO SEU FRONTEND.
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP que seu frontend vai usar
                        .allowedHeaders("*") // Permite todos os cabeçalhos
                        .allowCredentials(true); // Permite o envio de cookies de sessão (necessário para o express-session que você usa)
            }
        };
    }
}