package com.example.A3_Sistemas_Distribuidos.entity;

import com.example.A3_Sistemas_Distribuidos.entity.role.Cargo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Entity
public class Funcionario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres!")
    @Column(unique = true)
    private String cpf;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Column(unique = true)
    @Size(min = 10, max = 11, message = "O numero tem que ter de 10 a 11 dígitos!")
    private String numero;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Cargo cargo; // Este é o campo real que armazena o cargo

    @NotBlank
    private String senha;

    private boolean primeiroLogin = true;

    // ATUALIZADO: Mapeamento para tarefas onde este funcionário é o ATENDENTE
    @JsonIgnore // Para evitar recursão infinita na serialização JSON
    @OneToMany(mappedBy = "atendenteFuncionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefa> tarefasAtendidas; // Nome alterado para refletir o papel

    // NOVO: Mapeamento para tarefas onde este funcionário é o SUPERVISOR
    @JsonIgnore // Para evitar recursão infinita na serialização JSON
    @OneToMany(mappedBy = "supervisorFuncionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefa> tarefasSupervisionadas; // Nome alterado para refletir o papel

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.cargo == Cargo.GERENTE) return List.of(new SimpleGrantedAuthority("ROLE_GERENTE"),
                new SimpleGrantedAuthority("ROLE_SUPERVISOR"), new SimpleGrantedAuthority("ROLE_ATENDENTE"));
        else if (this.cargo == Cargo.SUPERVISOR) return List.of(new SimpleGrantedAuthority("ROLE_SUPERVISOR"), new SimpleGrantedAuthority("ROLE_ATENDENTE"));
        else return List.of(new SimpleGrantedAuthority("ROLE_ATENDENTE"));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}