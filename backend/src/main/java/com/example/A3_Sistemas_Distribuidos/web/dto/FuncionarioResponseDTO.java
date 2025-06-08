package com.example.A3_Sistemas_Distribuidos.web.dto;

import com.example.A3_Sistemas_Distribuidos.entity.role.Cargo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FuncionarioResponseDTO {

    @NotNull
    @Enumerated(EnumType.STRING)
    private Cargo cargo;
}
