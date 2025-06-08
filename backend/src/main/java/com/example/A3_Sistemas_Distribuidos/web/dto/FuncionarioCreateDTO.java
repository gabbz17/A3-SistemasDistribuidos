// src/main/java/com/example/A3_Sistemas_Distribuidos/web/dto/FuncionarioCreateDTO.java

package com.example.A3_Sistemas_Distribuidos.web.dto;

import com.example.A3_Sistemas_Distribuidos.entity.role.Cargo; // Importe o Enum Cargo
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioCreateDTO { // DTO para a requisição POST
    @NotBlank
    private String nome;
    @NotBlank
    @Size(min = 11, max = 11)
    private String cpf;
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 10, max = 11, message = "O numero tem que ter de 10 a 11 dígitos!")
    private String numero;
    @NotNull
    private Cargo cargo; // Receberá o valor do Enum (e.g., "GERENTE")
    @NotBlank
    private String senha;
}