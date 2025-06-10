# API Sistemas Distribuídos - *Por: Gabriel Coutinho*
* **Esta API gerencia 2 tipos de entidades, Funcionário e Tarefas, onde em uma será possível cadastrar seus funcionários e atribuir seus cargos, e em tarefas serão alocadas funções a esses funcionários.**
* **A entidade Funcionário consiste em 3 níveis: Gerente, onde nesse o usuário terá total liberdade de manipulação de dados de ambas as entidades; Supervisor, aqui o usuário só podera manipular dados da entidade Tarefa; Atendente, nesse o funcionário só visualizará suas atividades através de uma busca pelo próprio uid, além disso, ele também poderá alterar o status da tarefa baseado no uid da mesma.**
* **A API usa um sistema de autenticação, onde o funcionário fará login com email e senha e receberá um token. A partir deste token, ele terá acesso aos seus endpoints baseado no seu nível de acesso.**

# Requisitos
* **Há 3 tipos diferentes de clientes: o funcionário, o supervisor e o gerente;**

* **O funcionário acompanha as tarefas alocadas para ele informa sempre que uma dessas tarefas foi concluída;**

* **O supervisor cadastra tarefas para os funcionários e acompanha a execução das mesmas;**

* **O gerente emite relatórios em tempo real sobre o andamento do trabalho na empresa;**

* **Os dados devem ser armazenados em um banco de dados relacional (MySQL, SQLite, etc);**

* **Este serviço deve ser RESTFul.**


# Tecnologias utilizadas
* Linguagem de programação -> JAVA, JavaScript
* Framework -> SpringBoot, Node.js, Express
* Liguagem de marcação e estilização -> HTML e CSS
* IDE -> Intellij e VsCode
* Banco de Dados -> MySQL
* Ambiente para versionamento de código -> Git e GitHub
* Ambiente para Contêinerização -> Docker
* Método de autenticação -> JWT e OAuth

# Documento de configuração do ambiente para rodar a API
[A3 - Sistemas - Configuracao de ambiente.pdf](https://github.com/user-attachments/files/20662847/A3.-.Sistemas.-.Configuracao.de.ambiente.pdf)


# Link para protótipo no Figma
https://www.figma.com/design/dyCTMfXqlYbMFRVIsWL1jR/A3---Sistemas?node-id=0-1&m=dev&t=hr1OI3qedemc3ktl-1

# Relatório geral do projeto
[Relatório Geral - A3 Sistemas Distribuídos.pdf](https://github.com/user-attachments/files/20647022/Relatorio.Geral.-.A3.Sistemas.Distribuidos.pdf)

# Link do vídeo no YouTube


