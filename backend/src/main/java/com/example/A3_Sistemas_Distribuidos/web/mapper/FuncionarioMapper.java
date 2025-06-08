package com.example.A3_Sistemas_Distribuidos.web.mapper;

import com.example.A3_Sistemas_Distribuidos.entity.Funcionario;
import com.example.A3_Sistemas_Distribuidos.web.dto.FuncionarioDTO;

import java.util.List;
import java.util.stream.Collectors;

public class FuncionarioMapper {

    public static FuncionarioDTO toDTO(Funcionario funcionario) {
        if (funcionario == null) {
            return null;
        }
        return new FuncionarioDTO(funcionario); // Usa o construtor do DTO
    }

    public static List<FuncionarioDTO> toAllDto(List<Funcionario> funcionarios) {
        if (funcionarios == null) {
            return null;
        }
        return funcionarios.stream()
                .map(FuncionarioMapper::toDTO)
                .collect(Collectors.toList());
    }
}