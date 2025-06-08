package com.example.A3_Sistemas_Distribuidos.service;

import com.example.A3_Sistemas_Distribuidos.entity.Funcionario;
import com.example.A3_Sistemas_Distribuidos.entity.role.Cargo;
import com.example.A3_Sistemas_Distribuidos.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Funcionario create(Funcionario funcionario) {
        String encodedPassword = passwordEncoder.encode(funcionario.getSenha());
        funcionario.setSenha(encodedPassword);
        // NOVO: Definir primeiroLogin como true para novos usuários
        funcionario.setPrimeiroLogin(true);
        return funcionarioRepository.save(funcionario);
    }

    public List<Funcionario> findAll() { return funcionarioRepository.findAll(); }
    public Funcionario findById(Long id) {
        return funcionarioRepository.findById(id)
                   .orElseThrow(() -> new UsernameNotFoundException("Funcionário não encontrado com ID: " + id));
    }
    public List<Funcionario> findByCargo(String cargoName) {
        try {
            Cargo cargo = Cargo.valueOf(cargoName.toUpperCase());
            return funcionarioRepository.findByCargo(cargo);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cargo inválido: " + cargoName);
        }
    }
    public Funcionario findByNome(String nome) {
        return funcionarioRepository.findByNome(nome)
                   .orElseThrow(() -> new UsernameNotFoundException("Funcionário não encontrado com nome: " + nome));
    }
    public Funcionario update(Long id, Cargo newCargo) { // Assumindo que este é um update de cargo
        Funcionario funcionario = findById(id); // Reusa findById para buscar o funcionário
        funcionario.setCargo(newCargo);
        return funcionarioRepository.save(funcionario);
    }
    public void delete(Long id) {
        funcionarioRepository.deleteById(id);
    }

    public Funcionario findByEmail(String email) {
        return funcionarioRepository.findOptionalByEmail(email)
                   .orElseThrow(() -> new UsernameNotFoundException("Funcionário não encontrado por email: " + email));
    }

    // NOVO MÉTODO: Troca de Senha
    public Funcionario updatePassword(Long funcionarioId, String oldPassword, String newPassword) {
        Funcionario funcionario = findById(funcionarioId);

        // Verifica se a senha antiga está correta
        if (!passwordEncoder.matches(oldPassword, funcionario.getSenha())) {
            throw new IllegalArgumentException("Senha antiga incorreta.");
        }

        // Codifica a nova senha
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        funcionario.setSenha(encodedNewPassword);
        // NOVO: Define primeiroLogin como false após a troca de senha bem-sucedida
        funcionario.setPrimeiroLogin(false);
        return funcionarioRepository.save(funcionario);
    }
}