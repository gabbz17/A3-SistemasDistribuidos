package com.example.A3_Sistemas_Distribuidos.web.controller;

import com.example.A3_Sistemas_Distribuidos.infra.security.TokenService;
import com.example.A3_Sistemas_Distribuidos.web.dto.AutenticacaoDTO;
import com.example.A3_Sistemas_Distribuidos.web.dto.LoginResponseDTO;
import com.example.A3_Sistemas_Distribuidos.repository.FuncionarioRepository;
import com.example.A3_Sistemas_Distribuidos.entity.Funcionario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager atAuthenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AutenticacaoDTO dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
            var auth = this.atAuthenticationManager.authenticate(usernamePassword);

            Funcionario funcionario = (Funcionario) auth.getPrincipal();

            var token = tokenService.generateToken(funcionario);

            return ResponseEntity.ok(new LoginResponseDTO(
                token,
                "Login bem-sucedido.",
                funcionario.getId().toString(),
                funcionario.getNome(),
                funcionario.getCargo().name(),
                funcionario.isPrimeiroLogin() // <-- Esta linha está correta!
            ));

        } catch (AuthenticationException e) {
            // Ajuste a chamada aqui para usar o construtor AllArgsConstructor e passar null para os campos não aplicáveis
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(new LoginResponseDTO(
                                     null, // token
                                     "Credenciais inválidas. Tente novamente.", // message
                                     null, // userId
                                     null, // username
                                     null, // profile
                                     null  // forcePasswordChange (não aplicável em caso de falha de login)
                                 ));
        } catch (Exception e) {
            e.printStackTrace();
            // Ajuste a chamada aqui também
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new LoginResponseDTO(
                                     null, // token
                                     "Erro interno do servidor.", // message
                                     null, // userId
                                     null, // username
                                     null, // profile
                                     null  // forcePasswordChange
                                 ));
        }
    }
}