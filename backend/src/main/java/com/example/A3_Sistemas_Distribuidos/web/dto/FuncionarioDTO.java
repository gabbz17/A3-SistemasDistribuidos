package com.example.A3_Sistemas_Distribuidos.web.dto;

import com.example.A3_Sistemas_Distribuidos.entity.Funcionario; // Importe
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO { // Este DTO será usado para as respostas de GET Funcionario
    private String id;
    private String nome;
    private String email;
    private String perfil; // Campo para o cargo/perfil
    private String cpf; // Se quiser enviar
    private String numero; // Se quiser enviar

    // Construtor que recebe a entidade Funcionario para facilitar o mapeamento
    public FuncionarioDTO(Funcionario funcionario) {
        this.id = funcionario.getId().toString(); // Converter Long para String
        this.nome = funcionario.getNome();
        this.email = funcionario.getEmail();
        this.perfil = funcionario.getCargo().name(); // Converter Enum Cargo para String
        this.cpf = funcionario.getCpf();
        this.numero = funcionario.getNumero();
    }
}