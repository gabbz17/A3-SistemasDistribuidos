package com.example.A3_Sistemas_Distribuidos;

import com.example.A3_Sistemas_Distribuidos.entity.Funcionario;
import com.example.A3_Sistemas_Distribuidos.entity.Tarefa;
import com.example.A3_Sistemas_Distribuidos.entity.role.Cargo;
import com.example.A3_Sistemas_Distribuidos.repository.FuncionarioRepository;
import com.example.A3_Sistemas_Distribuidos.repository.TarefaRepository;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (repository.count() == 0){

            Funcionario gerente = new Funcionario();
            gerente.setNome("Ana Melo");
            gerente.setCpf("55410792465");
            gerente.setEmail("anna@gmail.com");
            gerente.setNumero("71788474202");
            gerente.setCargo(Cargo.GERENTE);
            gerente.setSenha(passwordEncoder.encode("anna123"));
            gerente.setPrimeiroLogin(false);
            repository.save(gerente);

            Funcionario atendente1 = new Funcionario();
            atendente1.setNome("Carla Souza");
            atendente1.setCpf("09876543210");
            atendente1.setEmail("carla@gmail.com");
            atendente1.setNumero("71912345678");
            atendente1.setCargo(Cargo.ATENDENTE);
            atendente1.setSenha(passwordEncoder.encode("admin123"));
            repository.save(atendente1);
            
            Funcionario atendente2 = new Funcionario();
            atendente2.setNome("Junior Melo");
            atendente2.setCpf("99453782904");
            atendente2.setEmail("junu@gmail.com");
            atendente2.setNumero("71922153674");
            atendente2.setCargo(Cargo.ATENDENTE);
            atendente2.setSenha(passwordEncoder.encode("admin123"));
            repository.save(atendente2);

            Funcionario supervisor = new Funcionario();
            supervisor.setNome("Daniel Oliveira");
            supervisor.setCpf("11223344556");
            supervisor.setEmail("daniel@gmail.com");
            supervisor.setNumero("71998877665");
            supervisor.setCargo(Cargo.SUPERVISOR);
            supervisor.setSenha(passwordEncoder.encode("admin123"));
            repository.save(supervisor);
        }

        if (tarefaRepository.count() == 0) {
            
            Tarefa tarefa1 = new Tarefa();
             tarefa1.setTitulo("Consertar Gerador");
             tarefa1.setDescricao("Trocar gerador da câmara traseira. Alongando o pino da junta.");
             tarefa1.setAtendente("Carla Souza");
             tarefa1.setSupervisor("Daniel Oliveira");
             tarefa1.setAtendenteFuncionario(repository.findByNome("Carla Souza").get());
             tarefa1.setSupervisorFuncionario(repository.findByNome("Daniel Oliveira").get());

            Tarefa tarefa2 = new Tarefa();
             tarefa2.setTitulo("Ajustar Betoneira");
             tarefa2.setDescricao("Ajustar parafuso central do pino do coquilho traseiro da betoneira.");
             tarefa2.setAtendente("Junior Melo");
             tarefa2.setSupervisor("Daniel Oliveira");
             tarefa2.setAtendenteFuncionario(repository.findByNome("Junior Melo").get());
             tarefa2.setSupervisorFuncionario(repository.findByNome("Daniel Oliveira").get());
            
             Tarefa tarefa3 = new Tarefa();
             tarefa3.setTitulo("Pendurar Cerca");
             tarefa3.setDescricao("Ajustar muro de cerca da área central.");
             tarefa3.setAtendente("Junior Melo");
             tarefa3.setSupervisor("Daniel Oliveira");
             tarefa3.setAtendenteFuncionario(repository.findByNome("Junior Melo").get());
             tarefa3.setSupervisorFuncionario(repository.findByNome("Daniel Oliveira").get());


             tarefaRepository.saveAll(Arrays.asList(tarefa1, tarefa2));
        }
    }
}