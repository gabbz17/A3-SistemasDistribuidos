package com.example.A3_Sistemas_Distribuidos.web.controller;

import com.example.A3_Sistemas_Distribuidos.entity.Funcionario;
import com.example.A3_Sistemas_Distribuidos.entity.Tarefa;
import com.example.A3_Sistemas_Distribuidos.entity.role.Status;
import com.example.A3_Sistemas_Distribuidos.service.TarefaService;
import com.example.A3_Sistemas_Distribuidos.web.dto.TarefaCreateDTO;
import com.example.A3_Sistemas_Distribuidos.web.dto.TarefaResponseDTO;
import com.example.A3_Sistemas_Distribuidos.web.dto.TarefaUpdateDTO;
import com.example.A3_Sistemas_Distribuidos.web.mapper.TarefaMapper;
import com.example.A3_Sistemas_Distribuidos.exception.ListNotFoundException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tarefa")
public class TarefaController {

    @Autowired
    TarefaService service;

    // Criar Tarefa
    // SUPERVISORES podem criar tarefas (e Gerentes também, por hierarquia se aplicável)
    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR')") // GERENTES E SUPERVISORES
    public ResponseEntity<TarefaResponseDTO> create(
            @Valid @RequestBody TarefaCreateDTO dto,
            @AuthenticationPrincipal Funcionario funcionarioLogado // Injete o Funcionário autenticado aqui
    ){
        // Verifique se o funcionário logado existe e tem um ID
        if (funcionarioLogado == null || funcionarioLogado.getId() == null) {
            // Isso geralmente não deveria acontecer com @PreAuthorize, mas é uma boa prática defensiva
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // --- LINHA CORRIGIDA AQUI ---
        // Agora, passamos o DTO e o ID do funcionário logado (que será o supervisor) para o serviço.
        Tarefa createdTarefa = service.create(dto, funcionarioLogado.getId());
        // --- FIM DA CORREÇÃO ---

        TarefaResponseDTO responseDto = TarefaMapper.toDTO(createdTarefa);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // Ver minhas tarefas (ATENDENTE)
    @GetMapping("/minhas-tarefas")
    @PreAuthorize("hasRole('ATENDENTE')")
    public ResponseEntity<List<TarefaResponseDTO>> getMinhasTarefas(@AuthenticationPrincipal Funcionario funcionarioLogado) {
        if (funcionarioLogado == null || funcionarioLogado.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Tarefa> minhasTarefas = service.findTarefasByAtendenteId(funcionarioLogado.getId());
        List<TarefaResponseDTO> responseList = TarefaMapper.toAllDto(minhasTarefas);
        return ResponseEntity.ok().body(responseList);
    }

    // Ver TODAS as tarefas
    // GERENTES E SUPERVISORES podem ver TODAS. Atendentes usam '/minhas-tarefas'.
    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR')") // SOMENTE GERENTES E SUPERVISORES
    public ResponseEntity<List<TarefaResponseDTO>> findAll(){
        List<Tarefa> tarefas = service.findAll();
        List<TarefaResponseDTO> responseList = TarefaMapper.toAllDto(tarefas);
        return ResponseEntity.ok().body(responseList);
    }

    // Buscar tarefa por UID
    // GERENTES e SUPERVISORES podem buscar qualquer tarefa. ATENDENTES só podem buscar as suas.
    @GetMapping("/uid/{uid}")
    // ATUALIZADO AQUI: use atendenteFuncionario.id
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR') or (hasRole('ATENDENTE') and @tarefaService.findByUid(#uid).atendenteFuncionario.id == principal.id)")
    public ResponseEntity<TarefaResponseDTO> findByUid(@PathVariable String uid, @AuthenticationPrincipal Funcionario principal){
        Tarefa tarefa = service.findByUid(uid);
        TarefaResponseDTO responseDto = TarefaMapper.toDTO(tarefa);
        return ResponseEntity.ok().body(responseDto);
    }

    // Buscar tarefas por Status
    // GERENTES e SUPERVISORES veem todas por status. ATENDENTES veem as suas por status.
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR', 'ATENDENTE')") // Todos podem buscar por status (com filtro interno)
    public ResponseEntity<List<TarefaResponseDTO>> findByStatus(@PathVariable String status, @AuthenticationPrincipal Funcionario principal){
        List<Tarefa> tarefas;
        if (principal.getCargo() == com.example.A3_Sistemas_Distribuidos.entity.role.Cargo.ATENDENTE) {
            tarefas = service.findTarefasByAtendenteId(principal.getId()).stream()
                                           .filter(t -> t.getStatus().name().equalsIgnoreCase(status))
                                           .collect(java.util.stream.Collectors.toList());
            if (tarefas.isEmpty()){
                throw new ListNotFoundException(String.format("Nenhuma tarefa com o status (%s) encontrada para você!", status));
            }
        } else { // Gerente ou Supervisor
            tarefas = service.findByStatus(status);
        }
        List<TarefaResponseDTO> responseList = TarefaMapper.toAllDto(tarefas);
        return ResponseEntity.ok().body(responseList);
    }

    // Buscar por Supervisor (String)
    // GERENTES e SUPERVISORES podem.
    @GetMapping("/supervisor/{supervisor}")
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR')")
    public ResponseEntity<List<TarefaResponseDTO>> findBySupervisor(@PathVariable String supervisor){
        List<Tarefa> tarefas = service.findBySupervisor(supervisor);
        List<TarefaResponseDTO> responseList = TarefaMapper.toAllDto(tarefas);
        return ResponseEntity.ok().body(responseList);
    }

    // Buscar por Atendente (String)
    // GERENTES e SUPERVISORES podem.
    @GetMapping("/atendente/{atendente}")
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR')")
    public ResponseEntity<List<TarefaResponseDTO>> findByAtendente(@PathVariable String atendente){
        List<Tarefa> tarefas = service.findByAtendente(atendente);
        List<TarefaResponseDTO> responseList = TarefaMapper.toAllDto(tarefas);
        return ResponseEntity.ok().body(responseList);
    }

    // Endpoint para atualizar APENAS o status da tarefa
    // ATENDENTES podem atualizar o status das SUAS tarefas. GERENTES NÃO. SUPERVISORES NÃO.
    @PatchMapping("/update/{uid}")
    // ATUALIZADO AQUI: use atendenteFuncionario.id
    @PreAuthorize("hasRole('ATENDENTE') and @tarefaService.findByUid(#uid).atendenteFuncionario.id == principal.id") // APENAS ATENDENTES e SOMENTE SUAS TAREFAS
    public ResponseEntity<TarefaResponseDTO> update(@PathVariable String uid, @Valid @RequestBody TarefaResponseDTO statusDto, @AuthenticationPrincipal Funcionario principal){
        Tarefa updatedTarefa = service.update(uid, statusDto.getStatus());
        TarefaResponseDTO responseDto = TarefaMapper.toDTO(updatedTarefa);
        return ResponseEntity.ok().body(responseDto);
    }

    // Endpoint para atualização completa da tarefa (título, descrição, atendente, supervisor)
    // SUPERVISORES podem editar (não status). GERENTES NÃO.
    @PatchMapping("/update-full/{uid}")
    @PreAuthorize("hasRole('SUPERVISOR')") // APENAS SUPERVISORES
    public ResponseEntity<TarefaResponseDTO> updateFull(@PathVariable String uid, @Valid @RequestBody TarefaUpdateDTO dto){
        Tarefa updatedTarefa = service.updateFull(uid, dto);
        TarefaResponseDTO responseDto = TarefaMapper.toDTO(updatedTarefa);
        return ResponseEntity.ok().body(responseDto);
    }

    // Deletar Tarefa
    // SUPERVISORES podem deletar. GERENTES NÃO.
    @DeleteMapping("/delete/{uid}")
    @PreAuthorize("hasRole('SUPERVISOR')") // APENAS SUPERVISORES
    public ResponseEntity<Void> delete(@PathVariable String uid){
        service.delete(uid);
        return ResponseEntity.noContent().build();
    }

    // NOVO ENDPOINT DE FILTRO COMBINADO
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERVISOR')") // Somente Gerentes e Supervisores podem usar este filtro.
    public ResponseEntity<List<TarefaResponseDTO>> filterTarefas(
            @RequestParam(required = false) Long assignedToId, // ID do funcionário (atendente)
            @RequestParam(required = false) String status) { // Status da tarefa
        
        List<Tarefa> tarefas = service.filterTarefas(assignedToId, status);
        
        if (tarefas.isEmpty()) {
            throw new ListNotFoundException("Nenhuma tarefa encontrada com os filtros fornecidos.");
        }
        
        List<TarefaResponseDTO> responseList = TarefaMapper.toAllDto(tarefas);
        return ResponseEntity.ok(responseList);
    }
}