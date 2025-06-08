package com.example.A3_Sistemas_Distribuidos.entity;

import com.example.A3_Sistemas_Distribuidos.entity.role.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "uid")
@Entity
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uid;

    @NotBlank
    private String titulo;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String descricao;
    @NotBlank
    private String atendente;
    @NotBlank
    private String supervisor;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATRIBUIDO;

    // Relacionamento com o Funcionário Atendente
    @ManyToOne
    @JoinColumn(name = "atendente_funcionario_id") // Nome da coluna na tabela Tarefa
    private Funcionario atendenteFuncionario; // O objeto Funcionario que é o atendente

    // NOVO: Relacionamento com o Funcionário Supervisor
    @ManyToOne
    @JoinColumn(name = "supervisor_funcionario_id") // Nome da coluna na tabela Tarefa
    private Funcionario supervisorFuncionario; // O objeto Funcionario que é o supervisor

    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
    private LocalDateTime criado = LocalDateTime.now();
    
    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
    private LocalDateTime finalizado;
}