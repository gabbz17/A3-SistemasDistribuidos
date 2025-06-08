package com.example.A3_Sistemas_Distribuidos.infra.security; // Ou seu pacote de segurança

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.A3_Sistemas_Distribuidos.entity.Funcionario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}") // Define a secret key no application.properties
    private String secret;

    public String generateToken(Funcionario funcionario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-api") // Emissor do token
                    .withSubject(funcionario.getEmail()) // Assunto do token (geralmente o username/email)
                    .withExpiresAt(genExpirationDate()) // Data de expiração
                    // NOVOS CLAIMS: Adicionar id, nome, cargo e *primeiroLogin*
                    .withClaim("userId", funcionario.getId())
                    .withClaim("userName", funcionario.getNome())
                    .withClaim("userProfile", funcionario.getCargo().name())
                    .withClaim("forcePasswordChange", funcionario.isPrimeiroLogin()) // Adiciona o flag aqui!
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject(); // Retorna o 'subject' (email) do token validado
        } catch (JWTVerificationException exception) {
            return ""; // Token inválido
        }
    }

    private Instant genExpirationDate() {
        // Token expira em 2 horas (ou defina sua própria duração)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // Ajuste o offset conforme necessário
    }
}