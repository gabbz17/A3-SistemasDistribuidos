package com.example.A3_Sistemas_Distribuidos.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TarefaUpdateDTO {
    @NotBlank
    private String titulo;
    @NotBlank
    private String descricao;
    @NotNull(message = "O ID do atendente é obrigatório.")
    private Long atendenteId;
    @NotNull(message = "O ID do supervisor é obrigatório.")
    private Long supervisorId;
    // O status não é incluído aqui, pois temos um endpoint específico para ele.
}