package com.example.A3_Sistemas_Distribuidos.web.mapper;

import com.example.A3_Sistemas_Distribuidos.entity.Tarefa;
import com.example.A3_Sistemas_Distribuidos.entity.Funcionario; // <<< IMPORTANTE: Importar a entidade Funcionario
import com.example.A3_Sistemas_Distribuidos.web.dto.TarefaResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class TarefaMapper {

    public static TarefaResponseDTO toDTO(Tarefa tarefa) {
        if (tarefa == null) {
            return null;
        }

        return new TarefaResponseDTO(
                tarefa.getUid(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getCriado(),
                tarefa.getFinalizado(),
                tarefa.getAtendenteFuncionario(), // <<< Passando o objeto Funcionario direto
                tarefa.getSupervisorFuncionario() // <<< Passando o objeto Funcionario direto
        );
    }

    public static List<TarefaResponseDTO> toAllDto(List<Tarefa> tarefas) {
        return tarefas.stream()
                .map(TarefaMapper::toDTO)
                .collect(Collectors.toList());
    }
}