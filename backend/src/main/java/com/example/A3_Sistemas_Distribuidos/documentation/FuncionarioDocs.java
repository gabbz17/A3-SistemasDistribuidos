package com.example.A3_Sistemas_Distribuidos.documentation;

import com.example.A3_Sistemas_Distribuidos.entity.Funcionario; // Importe
import com.example.A3_Sistemas_Distribuidos.web.dto.FuncionarioDTO;
import com.example.A3_Sistemas_Distribuidos.web.dto.FuncionarioCreateDTO;
import com.example.A3_Sistemas_Distribuidos.web.dto.FuncionarioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.ResponseEntity;
// Removendo @PathVariable e @RequestBody daqui, eles não pertencem à interface de documentação pura
// e são a causa do problema quando você usa `implements`.
// Se sua ferramenta de documentação (SpringDoc/Swagger) precisa deles na interface,
// isso pode ser uma configuração específica, mas a regra geral é que eles são do Controller.
// O erro `must implement` acontece por causa da DISCREPÂNCIA na ASSINATURA, não na ANOTAÇÃO.

import java.util.List;

@Tag(name = "Funcionario", description = "Documentação da entidade de Funcionario")
public interface FuncionarioDocs {

    @Operation(summary = "Cria um novo Funcionário", description = "Http para criar um novo Funcionário",
            responses = {@ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioDTO.class))),
                            @ApiResponse(responseCode = "409", description = "Erro ao criar Funcionário!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    // Removi @Valid @RequestBody da interface, mantenha-os no controller
    ResponseEntity<FuncionarioDTO> create(FuncionarioCreateDTO dto);


    @Operation(summary = "Retorna todos os Funcionários", description = "Http para retornar todos os Funcionários",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao listar todos os Funcionários", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioDTO.class))),
                            @ApiResponse(responseCode = "404", description = "Erro ao retornar todos os Funcionários", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    // Mantenha findAll() sem parâmetros, pois o controller tem @GetMapping("/all")
    ResponseEntity<List<FuncionarioDTO>> findAll();


    // *** CORREÇÃO AQUI para findById: Adicione o segundo parâmetro `Funcionario principal` ***
    @Operation(summary = "Retorna Funcionário pelo id", description = "Http para retornar Funcionário pelo id",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao buscar Funcionário pelo id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioDTO.class))),
                            @ApiResponse(responseCode = "404", description = "Erro ao buscar Funcionário pelo id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    // A assinatura AGORA BATE com o controller: Long id E Funcionario principal
    ResponseEntity<FuncionarioDTO> findById(Long id, Funcionario principal); // <--- A MUDANÇA MAIS IMPORTANTE AQUI


    @Operation(summary = "Retorna Funcionário pelo cargo", description = "Http para retornar Funcionário pelo cargo",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao buscar Funcionário pelo cargo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioDTO.class))),
                            @ApiResponse(responseCode = "404", description = "Erro ao buscar Funcionário pelo cargo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<List<FuncionarioDTO>> findByCargo(String cargo); // Sem @PathVariable aqui


    @Operation(summary = "Retorna Funcionário pelo nome", description = "Http para retornar Funcionário pelo nome",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao buscar Funcionário pelo nome", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioDTO.class))),
                            @ApiResponse(responseCode = "404", description = "Erro ao buscar Funcionário pelo nome", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<FuncionarioDTO> findByNome(String nome); // Sem @PathVariable aqui


    // *** CORREÇÃO AQUI para update: Adicione o terceiro parâmetro `Funcionario principal` ***
    @Operation(summary = "Altera cargo do Funcionário pelo id", description = "Http para alterar cargo do Funcionário pelo id",
            responses = {@ApiResponse(responseCode = "200", description = "Sucesso ao alterar cargo do Funcionário", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioDTO.class))),
                            @ApiResponse(responseCode = "404", description = "Erro ao alterar cargo do Funcionário", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    // A assinatura AGORA BATE com o controller: Long id, FuncionarioResponseDTO cargo E Funcionario principal
    ResponseEntity<FuncionarioDTO> update(Long id, FuncionarioResponseDTO cargo, Funcionario principal); // <--- A MUDANÇA MAIS IMPORTANTE AQUI


    @Operation(summary = "Deleta Funcionário pelo id", description = "Http para deletar Funcionário pelo id",
            responses = {@ApiResponse(responseCode = "204", description = "Sucesso ao deletar Funcionário", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                            @ApiResponse(responseCode = "404", description = "Erro ao deletar Funcionário", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))})
    ResponseEntity<Void> delete(Long id); // Sem @PathVariable aqui
}