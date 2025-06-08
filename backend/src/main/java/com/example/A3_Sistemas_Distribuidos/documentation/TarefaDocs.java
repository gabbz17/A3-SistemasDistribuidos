package com.example.A3_Sistemas_Distribuidos.documentation;

import com.example.A3_Sistemas_Distribuidos.web.dto.TarefaResponseDTO;
import com.example.A3_Sistemas_Distribuidos.entity.Tarefa; // Mantenha Tarefa para outros métodos
import com.example.A3_Sistemas_Distribuidos.web.dto.TarefaCreateDTO; // <--- Importe TarefaCreateDTO
import com.example.A3_Sistemas_Distribuidos.web.dto.TarefaResponseDTO; // Importe TarefaDTO se for o DTO de resposta
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // Mantenha, se a documentação precisa
import org.springdoc.api.ErrorMessage;
import org.springframework.http.ResponseEntity;
// Remova @PathVariable e @RequestBody daqui se estiverem. Eles não são necessários em interfaces de doc.

import java.util.List;

@Tag(name = "Tarefa", description = "Documentação da entidade de Tarefa")
public interface TarefaDocs {

    @Operation(summary = "Cria uma nova Tarefa", description = "Http para criar uma nova Tarefa",
            responses = {@ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tarefa.class))), // Use Tarefa se o controller retornar Tarefa
                            @ApiResponse(responseCode = "400", description = "Campo(s) inválido(s)!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))), // Adicione 400
                            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    // *** CORREÇÃO AQUI: Use TarefaCreateDTO e remova @Valid @RequestBody (se houver) ***
    ResponseEntity<Tarefa> create(TarefaCreateDTO dto); // Assinatura para bater com o controller


    @Operation(summary = "Retorna todas as Tarefas", description = "Http para retornar todas as Tarefas",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao listar todas as Tarefas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tarefa.class))), // Use Tarefa
                            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<List<Tarefa>> findAll();


    @Operation(summary = "Retorna Tarefa pelo uid", description = "Http para retornar Tarefa pelo uid",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao buscar Tarefa pelo uid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tarefa.class))), // Use Tarefa
                            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<Tarefa> findByUid(String uid); // Sem @PathVariable aqui


    @Operation(summary = "Retorna Tarefas pelo status", description = "Http para retornar Tarefas pelo status",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao buscar Tarefas pelo status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tarefa.class))), // Use Tarefa
                            @ApiResponse(responseCode = "404", description = "Tarefas não encontradas!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<List<Tarefa>> findByStatus(String status); // Sem @PathVariable aqui


    @Operation(summary = "Retorna Tarefas por supervisor", description = "Http para retornar Tarefas por supervisor",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao buscar Tarefas por supervisor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tarefa.class))), // Use Tarefa
                            @ApiResponse(responseCode = "404", description = "Tarefas não encontradas!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<List<Tarefa>> findBySupervisor(String supervisor); // Sem @PathVariable aqui


    @Operation(summary = "Retorna Tarefas por atendente", description = "Http para retornar Tarefas por atendente",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao buscar Tarefas por atendente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tarefa.class))), // Use Tarefa
                            @ApiResponse(responseCode = "404", description = "Tarefas não encontradas!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<List<Tarefa>> findByAtendente(String atendente); // Sem @PathVariable aqui


    @Operation(summary = "Atualiza status da Tarefa", description = "Http para atualizar status da Tarefa",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao atualizar status da Tarefa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tarefa.class))), // Use Tarefa
                            @ApiResponse(responseCode = "400", description = "Campo(s) inválido(s)!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<Tarefa> update(String uid, TarefaResponseDTO status); // Sem @PathVariable ou @RequestBody aqui


    @Operation(summary = "Deleta Tarefa pelo uid", description = "Http para deletar Tarefa pelo uid",
            responses = {@ApiResponse(responseCode = "204", description = "Sucesso ao deletar Tarefa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<Void> delete(String uid); // Sem @PathVariable aqui
}