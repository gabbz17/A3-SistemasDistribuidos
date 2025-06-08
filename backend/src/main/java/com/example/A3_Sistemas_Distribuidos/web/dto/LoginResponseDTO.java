// Exemplo de LoginResponseDTO.java (se você não tiver, crie-o)
package com.example.A3_Sistemas_Distribuidos.web.dto;

public record LoginResponseDTO(
    String token,
    String message, // Mensagem de sucesso/erro
    String userId,
    String username,
    String profile,
    Boolean forcePasswordChange // NOVO CAMPO
) {
    // Construtor adicional para respostas de erro (sem token)
    public LoginResponseDTO(String token, String message) {
        this(token, message, null, null, null, null);
    }
}