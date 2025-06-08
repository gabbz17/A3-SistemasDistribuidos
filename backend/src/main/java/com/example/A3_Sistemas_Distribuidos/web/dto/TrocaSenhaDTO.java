package com.example.A3_Sistemas_Distribuidos.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrocaSenhaDTO(
    @NotBlank(message = "A senha antiga é obrigatória.")
    String senhaAntiga,
    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.") // Exemplo de validação de tamanho
    String novaSenha
) {}