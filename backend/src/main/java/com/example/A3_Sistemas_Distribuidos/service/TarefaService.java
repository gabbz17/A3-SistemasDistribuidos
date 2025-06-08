// AQUI ESTÁ A CORREÇÃO NO SEU TarefaService.java
package com.example.A3_Sistemas_Distribuidos.service;

import com.example.A3_Sistemas_Distribuidos.entity.Funcionario;
import com.example.A3_Sistemas_Distribuidos.entity.Tarefa;
import com.example.A3_Sistemas_Distribuidos.entity.role.Status;
import com.example.A3_Sistemas_Distribuidos.exception.IdNotFoundException;
import com.example.A3_Sistemas_Distribuidos.exception.ListNotFoundException;
import com.example.A3_Sistemas_Distribuidos.exception.NameUniqueException;
import com.example.A3_Sistemas_Distribuidos.repository.TarefaRepository;
import com.example.A3_Sistemas_Distribuidos.web.dto.TarefaCreateDTO;
import com.example.A3_Sistemas_Distribuidos.web.dto.TarefaUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TarefaService {

    @Autowired
    TarefaRepository repository;

    @Autowired
    FuncionarioService funcionarioService; // Certifique-se que este serviço pode buscar Funcionario por ID

    // ATUALIZADO: Método create agora recebe o supervisorId diretamente
    public Tarefa create(TarefaCreateDTO dto, Long supervisorId) {
        Funcionario atendenteFuncionario = funcionarioService.findById(dto.getAtendenteId());
        // NOVO: Busca o funcionário supervisor usando o supervisorId recebido
        Funcionario supervisorFuncionario = funcionarioService.findById(supervisorId);

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setAtendente(atendenteFuncionario.getNome()); // Mantém o nome String
        tarefa.setSupervisor(supervisorFuncionario.getNome()); // Mantém o nome String
        tarefa.setStatus(Status.ATRIBUIDO);
        tarefa.setCriado(LocalDateTime.now());
        tarefa.setAtendenteFuncionario(atendenteFuncionario); // ATUALIZADO: Atribui o objeto Funcionario
        tarefa.setSupervisorFuncionario(supervisorFuncionario); // ATUALIZADO: Atribui o objeto Funcionario

        return repository.save(tarefa);
    }

    public List<Tarefa> findAll(){
        return repository.findAll();
    }

    public Tarefa findByUid(String uid){
        return repository.findByUid(uid).orElseThrow(() ->
                new IdNotFoundException(String.format("Tarefa com o uid (%s), não encontrada!", uid)));
    }

    public List<Tarefa> findByStatus(String status){
        Status st = Status.valueOf(status.toUpperCase());
        List<Tarefa> list = repository.findByStatus(st);

        if (list.isEmpty()){
            throw new ListNotFoundException(String.format("Tarefas com o status (%s), não encontrados!", status));
        }
        return list;
    }

    // Este método agora usará o ID do atendente diretamente do relacionamento
    public List<Tarefa> findTarefasByAtendenteId(Long atendenteId) {
        List<Tarefa> tarefas = repository.findByAtendenteFuncionarioId(atendenteId); // ATUALIZADO
        if (tarefas.isEmpty()){
            throw new ListNotFoundException(String.format("Nenhuma tarefa encontrada para o atendente com ID (%d)!", atendenteId));
        }
        return tarefas;
    }
    
    // Este método pode ser ajustado para usar findBySupervisorFuncionarioId
    public List<Tarefa> findBySupervisor(String supervisorNome){
        List<Tarefa> list = repository.findBySupervisorFuncionarioNome(supervisorNome); // ATUALIZADO para usar nome do objeto Funcionario
        if (list.isEmpty()){
            throw new ListNotFoundException(String.format("Tarefas supervisionadas por (%s), não encontradas!", supervisorNome));
        }
        return list;
    }

    // Este método pode ser ajustado para usar findByAtendenteFuncionarioNome
    public List<Tarefa> findByAtendente(String atendenteNome){
        List<Tarefa> list = repository.findByAtendenteFuncionarioNome(atendenteNome); // ATUALIZADO para usar nome do objeto Funcionario
        if (list.isEmpty()){
            throw new ListNotFoundException(String.format("Tarefas atribuídas para (%s), não encontradas!", atendenteNome));
        }
        return list;
    }

    public Tarefa update(String uid, Status status){
        Tarefa tarefa = findByUid(uid);

        if (status.equals(Status.ATRIBUIDO)){
            throw new NameUniqueException(String.format("Valor para status (%s), negado!", status));
        }

        if (status.equals(Status.CONCLUIDO)) {
            tarefa.setFinalizado(LocalDateTime.now());
        } else {
            tarefa.setFinalizado(null); // Limpa a data de finalização se o status não for CONCLUIDO
        }

        tarefa.setStatus(status);
        return repository.save(tarefa);
    }

    public Tarefa updateFull(String uid, TarefaUpdateDTO dto) {
        Tarefa tarefa = findByUid(uid);

        Funcionario novoAtendente = funcionarioService.findById(dto.getAtendenteId());
        Funcionario novoSupervisor = funcionarioService.findById(dto.getSupervisorId()); // O supervisor pode ser alterado no updateFull

        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setAtendente(novoAtendente.getNome());
        tarefa.setSupervisor(novoSupervisor.getNome());
        tarefa.setAtendenteFuncionario(novoAtendente); // ATUALIZADO
        tarefa.setSupervisorFuncionario(novoSupervisor); // ATUALIZADO

        return repository.save(tarefa);
    }

    public void delete(String uid){
        Tarefa tarefa = findByUid(uid);
        repository.delete(tarefa);
    }

    // MÉTODO DE FILTRO COMBINADO APRIMORADO
    public List<Tarefa> filterTarefas(Long assignedToId, String statusString) {
        Status statusEnum = null;
        if (statusString != null && !statusString.isEmpty()) {
            try {
                statusEnum = Status.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("DEBUG Service: Status de tarefa inválido recebido: " + statusString);
            }
        }

        List<Tarefa> tarefas;
        
        System.out.println("DEBUG Service filterTarefas: assignedToId=" + assignedToId + ", statusString=" + statusString + ", statusEnum=" + statusEnum);

        // Se ambos assignedToId e statusEnum são fornecidos
        if (assignedToId != null && statusEnum != null) {
            System.out.println("DEBUG Service: Cenário 1: Buscando por ID de atendente/supervisor (" + assignedToId + ") E status (" + statusEnum + ").");
            // CORREÇÃO: Mude para a chamada correta com 2 parâmetros se o seu repository foi alterado para isso,
            // OU use apenas 1 parâmetro se a query @Query no repository usa apenas um.
            // Pelo que você forneceu do repository, ele espera APENAS UM assignedId para essa query combinada.
            tarefas = repository.findByAtendenteFuncionarioIdOrSupervisorFuncionarioIdAndStatus(assignedToId, statusEnum);
        } 
        // Se apenas assignedToId é fornecido
        else if (assignedToId != null) {
            System.out.println("DEBUG Service: Cenário 2: Buscando por ID de atendente/supervisor (" + assignedToId + ").");
            // CORREÇÃO AQUI: Chame com APENAS UM assignedToId.
            tarefas = repository.findByAtendenteFuncionarioIdOrSupervisorFuncionarioId(assignedToId);
        }
        // Se apenas statusEnum é fornecido
        else if (statusEnum != null) {
            System.out.println("DEBUG Service: Cenário 3: Buscando por apenas status (" + statusEnum + ").");
            tarefas = repository.findByStatus(statusEnum);
        } 
        // Se nenhum filtro é fornecido
        else {
            System.out.println("DEBUG Service: Cenário 4: Nenhum filtro. Buscando todas as tarefas.");
            tarefas = repository.findAll();
        }
        
        return tarefas;
    } 
}