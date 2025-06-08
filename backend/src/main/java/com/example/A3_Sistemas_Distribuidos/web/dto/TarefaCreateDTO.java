package com.example.A3_Sistemas_Distribuidos.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TarefaCreateDTO {
    @NotBlank
    private String titulo;
    @NotBlank
    private String descricao;

    @NotNull // Mantém este, pois o ID do atendente é enviado pelo frontend
    private Long atendenteId;

    // ESTE CAMPO DEVE SER REMOVIDO DO DTO DE CRIAÇÃO!
    // private Long supervisorId;
    // private Long supervisorFuncionarioId; // Se você usava um campo direto
    // private Funcionario supervisorFuncionario; // Se usava o objeto completo aqui
}