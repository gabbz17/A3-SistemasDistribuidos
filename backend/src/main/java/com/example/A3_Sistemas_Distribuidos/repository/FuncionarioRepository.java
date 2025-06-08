package com.example.A3_Sistemas_Distribuidos.repository;

import com.example.A3_Sistemas_Distribuidos.entity.Funcionario;
import com.example.A3_Sistemas_Distribuidos.entity.role.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Importe Optional

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    List<Funcionario> findByCargo(Cargo cargo);
    Optional<Funcionario> findByNome(String nome);

    // Este método é usado pelo Spring Security (AutorizacaoService).
    // Ele deve retornar UserDetails.
    UserDetails findByEmail(String email);

    // *** ADICIONE ESTE MÉTODO: findOptionalByEmail ***
    // Ele agora retorna Optional<Funcionario> quando buscar por email
    Optional<Funcionario> findOptionalByEmail(String email);
}