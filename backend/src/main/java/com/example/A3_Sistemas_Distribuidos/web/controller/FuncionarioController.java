package com.example.A3_Sistemas_Distribuidos.web.controller;

import com.example.A3_Sistemas_Distribuidos.documentation.FuncionarioDocs;
import com.example.A3_Sistemas_Distribuidos.entity.Funcionario;
import com.example.A3_Sistemas_Distribuidos.entity.role.Cargo; // Mantenha import
import com.example.A3_Sistemas_Distribuidos.service.FuncionarioService;
import com.example.A3_Sistemas_Distribuidos.web.dto.FuncionarioDTO;
import com.example.A3_Sistemas_Distribuidos.web.dto.FuncionarioResponseDTO;
import com.example.A3_Sistemas_Distribuidos.web.dto.FuncionarioCreateDTO;
import com.example.A3_Sistemas_Distribuidos.web.dto.TrocaSenhaDTO;
import com.example.A3_Sistemas_Distribuidos.web.mapper.FuncionarioMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/funcionario")
public class FuncionarioController implements FuncionarioDocs {

    @Autowired
    FuncionarioService service;

    // 1. POST /api/funcionario (Criação de Usuário)
    // GERENTE pode criar. Supervisor e Atendente NÃO.
    @Override
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')") // APENAS GERENTES
    public ResponseEntity<FuncionarioDTO> create(@Valid @RequestBody FuncionarioCreateDTO dto){
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());
        funcionario.setSenha(dto.getSenha());
        funcionario.setCargo(dto.getCargo());
        funcionario.setCpf(dto.getCpf());
        funcionario.setNumero(dto.getNumero());

        Funcionario createdFuncionario = service.create(funcionario);
        FuncionarioDTO responseDto = FuncionarioMapper.toDTO(createdFuncionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 2. GET /api/funcionario (Obter Detalhes do Usuário Logado)
    // Todos autenticados podem ver seus próprios detalhes.
    @GetMapping
    @PreAuthorize("isAuthenticated()") // Todos autenticados
    public ResponseEntity<FuncionarioDTO> getLoggedUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Funcionario funcionario = (Funcionario) userDetails;
        FuncionarioDTO dto = FuncionarioMapper.toDTO(funcionario);
        return ResponseEntity.ok(dto);
    }


    // 3. GET /api/funcionario/all (Listar Todos os Funcionários)
    // Gerentes e Supervisores podem ver todos.
    @Override
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR')") // GERENTES E SUPERVISORES
    public ResponseEntity<List<FuncionarioDTO>> findAll(){
        List<Funcionario> fun = service.findAll();
        List<FuncionarioDTO> list = FuncionarioMapper.toAllDto(fun);
        return ResponseEntity.ok().body(list);
    }

    // 4. GET /api/funcionario/id/{id} (Buscar por ID)
    // Gerentes e Supervisores podem ver qualquer um. Atendentes só podem ver a si mesmos.
    @Override
    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR') or (#id == principal.id and hasRole('ATENDENTE'))") // Ajustado para Atendente
    public ResponseEntity<FuncionarioDTO> findById(@PathVariable Long id, @AuthenticationPrincipal Funcionario principal){
        Funcionario fun = service.findById(id);
        FuncionarioDTO dto = FuncionarioMapper.toDTO(fun);
        return ResponseEntity.ok().body(dto);
    }

    // 5. GET /api/funcionario/cargo/{cargo} (Buscar por Cargo)
    // Gerentes e Supervisores podem buscar por cargo.
    @Override
    @GetMapping("/cargo/{cargo}")
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR')")
    public ResponseEntity<List<FuncionarioDTO>> findByCargo(@PathVariable String cargo){
        List<Funcionario> fun = service.findByCargo(cargo);
        List<FuncionarioDTO> list = FuncionarioMapper.toAllDto(fun);
        return ResponseEntity.ok().body(list);
    }

    // 6. GET /api/funcionario/nome/{nome} (Buscar por Nome)
    // Gerentes e Supervisores podem buscar por nome.
    @Override
    @GetMapping("/nome/{nome}")
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR')")
    public ResponseEntity<FuncionarioDTO> findByNome(@PathVariable String nome){
        Funcionario fun = service.findByNome(nome);
        FuncionarioDTO dto = FuncionarioMapper.toDTO(fun);
        return ResponseEntity.ok().body(dto);
    }

    // 7. PATCH /api/funcionario/update/{id} (Atualizar Cargo)
    // Apenas GERENTES podem alterar o cargo de outros. Um funcionário pode atualizar seu próprio cargo? Não parece o caso.
    // Se for para atualizar o próprio perfil do funcionário (não o cargo), o endpoint é diferente ou mais geral.
    // Assumo que este é para gerente mudar o cargo de alguém.
    @Override
    @PatchMapping("/update/{id}")
    @PreAuthorize("hasRole('GERENTE')") // APENAS GERENTES podem mudar o cargo de outros
    public ResponseEntity<FuncionarioDTO> update(@PathVariable Long id, @Valid @RequestBody FuncionarioResponseDTO cargo, @AuthenticationPrincipal Funcionario principal){
        Funcionario fun = service.update(id, cargo.getCargo());
        FuncionarioDTO dto = FuncionarioMapper.toDTO(fun);
        return ResponseEntity.ok().body(dto);
    }

    // 8. DELETE /api/funcionario/delete/{id} (Deletar Funcionário)
    // Apenas GERENTES podem deletar.
    @Override
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('GERENTE')") // APENAS GERENTES
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Trocar senha (já existente)
    @PatchMapping("/trocar-senha")
    @PreAuthorize("isAuthenticated()") // Todos autenticados podem trocar a senha
    public ResponseEntity<String> trocarSenha(
            @AuthenticationPrincipal Funcionario funcionarioLogado,
            @RequestBody @Valid TrocaSenhaDTO dto) {
        try {
            service.updatePassword(funcionarioLogado.getId(), dto.senhaAntiga(), dto.novaSenha());
            return ResponseEntity.ok("Senha alterada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao trocar a senha.");
        }
    }
}