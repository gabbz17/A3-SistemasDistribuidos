package com.example.A3_Sistemas_Distribuidos.web.dto;

import com.example.A3_Sistemas_Distribuidos.entity.Funcionario; // <<< IMPORTANTE: Importar a entidade Funcionario
import com.example.A3_Sistemas_Distribuidos.entity.role.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Removeremos @AllArgsConstructor para construtor manual

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
// @AllArgsConstructor // Removido para usar construtor manual ou setters e ter controle total.
                    // Se Lombok gerar um construtor com os novos campos Funcionario, pode tentar manter.
public class TarefaResponseDTO {
    private String uid;
    private String titulo;
    private String descricao;
    private Status status;
    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
    private LocalDateTime criado;
    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
    private LocalDateTime finalizado;

    // --- CAMPOS CORRIGIDOS/ADICIONADOS AQUI ---
    // Removi os campos 'String atendente' e 'String supervisor' pois são redundantes
    // e o frontend já espera os objetos Funcionario completos.
    private Funcionario atendenteFuncionario; // Frontend espera este objeto
    private Funcionario supervisorFuncionario; // Frontend espera este objeto
    // --- FIM DOS CAMPOS CORRIGIDOS/ADICIONADOS ---

    // Construtor manual para o mapper.
    // Se você usa @AllArgsConstructor e ele gerar um construtor que inclui os novos campos Funcionario,
    // pode usar essa versão, mas o construtor explícito dá mais clareza.
    public TarefaResponseDTO(String uid, String titulo, String descricao, Status status,
                             LocalDateTime criado, LocalDateTime finalizado,
                             Funcionario atendenteFuncionario, Funcionario supervisorFuncionario) {
        this.uid = uid;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.criado = criado;
        this.finalizado = finalizado;
        this.atendenteFuncionario = atendenteFuncionario;
        this.supervisorFuncionario = supervisorFuncionario;
    }
}