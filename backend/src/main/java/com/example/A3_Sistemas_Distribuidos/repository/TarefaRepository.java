package com.example.A3_Sistemas_Distribuidos.repository;

import com.example.A3_Sistemas_Distribuidos.entity.Tarefa;
import com.example.A3_Sistemas_Distribuidos.entity.role.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, String> {
    List<Tarefa> findByStatus(Status st);

    // Queries que usam os nomes (String) - mantidas por compatibilidade se ainda usadas em outros lugares
    List<Tarefa> findBySupervisorFuncionarioNome(String supervisorNome);
    List<Tarefa> findByAtendenteFuncionarioNome(String atendenteNome);

    Optional<Tarefa> findByUid(String uid);

    // --- Métodos para buscar APENAS por ID do Atendente/Supervisor ---

    // ATUALIZADO: Usando @Query explícita para clareza
    @Query("SELECT t FROM Tarefa t WHERE t.atendenteFuncionario.id = :atendenteId")
    List<Tarefa> findByAtendenteFuncionarioId(@Param("atendenteId") Long atendenteId);

    // ATUALIZADO: Usando @Query explícita para clareza
    @Query("SELECT t FROM Tarefa t WHERE t.supervisorFuncionario.id = :supervisorId")
    List<Tarefa> findBySupervisorFuncionarioId(@Param("supervisorId") Long supervisorId);


    // --- Métodos para filtro COMBINADO ou flexível ---

    // NOVO: Filtrar por ID de Atendente OU ID de Supervisor (para o mesmo assignedToId vindo do frontend)
    // A query JPA está correta e utiliza o mesmo parâmetro para ambos os lados do OR.
    @Query("SELECT t FROM Tarefa t WHERE t.atendenteFuncionario.id = :assignedId OR t.supervisorFuncionario.id = :assignedId")
    List<Tarefa> findByAtendenteFuncionarioIdOrSupervisorFuncionarioId(@Param("assignedId") Long assignedId);

    // NOVO: Filtrar por ID de Atendente OU ID de Supervisor E Status
    // Esta é a query mais crítica para o filtro combinado.
    // Garante que a lógica OR esteja dentro de parênteses e o AND t.status esteja fora.
    @Query("SELECT t FROM Tarefa t WHERE (t.atendenteFuncionario.id = :assignedId OR t.supervisorFuncionario.id = :assignedId) AND t.status = :status")
    List<Tarefa> findByAtendenteFuncionarioIdOrSupervisorFuncionarioIdAndStatus(
        @Param("assignedId") Long assignedId, // O ID do funcionário (atendente ou supervisor)
        @Param("status") Status status);

}