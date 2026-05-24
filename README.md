# 🎮 AtivHub - Backend API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

O **AtivHub** é uma plataforma gamificada de gestão de aprendizagem (LMS) focada na criação, distribuição e correção de missões e atividades educacionais. O sistema transforma a entrega de tarefas em uma experiência imersiva para o estudante, recompensando o cumprimento de metas com Pontos de Experiência (XP), níveis dinâmicos e um Ranking Global competitivo.

Este repositório contém o **Core Backend** da aplicação, desenvolvido seguindo os padrões arquiteturais de uma API REST robusta, segura e escalável utilizando o ecossistema Java com Spring Boot.

---

## 🚀 Funcionalidades Principais

* **Autenticação e Autorização Seguras:** Controle de acesso baseado em cargos (**Roles**) gerenciado via Spring Security e Tokens JWT.
* **Visão do Professor:** Criação, edição, listagem e remoção de atividades (missões) educacionais, além de um sistema centralizado para dar notas e feedbacks às submissões dos alunos.
* **Visão do Aluno:** Painel dinâmico de atividades pendentes, envio de respostas (submissões), acompanhamento de notas/feedbacks recebidos e visualização do próprio progresso (XP e Nível).
* **Ranking Global Otimizado:** Placar atualizado que exibe o TOP 10 de alunos da plataforma, ordenando-os por maior quantidade de XP e maior número de atividades respondidas.
* **Evolução Dinâmica:** Sistema automático de níveis calculado diretamente no servidor (Ex: cada 100 XP equivale a um novo nível).
* **Versionamento de Banco de Dados:** Todas as tabelas e alterações estruturais do banco de dados PostgreSQL são gerenciadas de forma automatizada por migrações.

---

## 🛠️ Tecnologias e Bibliotecas Utilizadas

* **Java 21 (Temurin OpenJDK):** Versão LTS da linguagem, utilizando recursos modernos de performance e sintaxe.
* **Spring Boot 3.4.1:** Base do ecossistema do projeto para configuração simplificada.
* **Spring Web:** Criação de endpoints RESTful estruturados com `ResponseEntity` e controle semântico de verbos HTTP.
* **Spring Security & JWT (auth0):** Proteção criptografada de rotas e validação stateless de requisições por token bearer.
* **Spring Data JPA & Hibernate:** Mapeamento Objeto-Relacional (ORM) e abstração de consultas ao banco de dados com repositórios dinâmicos.
* **PostgreSQL:** Banco de dados relacional robusto utilizado para persistência dos dados de produção e desenvolvimento.
* **Flyway Migration:** Framework de migração para evolução contínua e controlada do esquema do banco de dados (`db/migration`).
* **Jakarta Bean Validation:** Validação declarativa de integridade de dados de entrada (`@Valid`, `@NotNull`, `@NotBlank`) nos DTOs.
* **Lombok:** Redução drástica de código boilerplate através de anotações como `@RequiredArgsConstructor`, `@Getter`, e `@Setter`.

---

## 📦 Arquitetura de Pacotes

O projeto segue uma estrutura limpa e dividida por responsabilidades claras (Clean Architecture / Domain-Driven Design simplificado):

```text
com.example.AtivHub.AtivHub
│
├── controller      # Camada de Entrada: Endpoints REST expostos ao Frontend
├── domain          # Modelos de Negócio: Entidades JPA, Enums e os DTOs (Data Transfer Objects)
├── infra           # Configurações de Infraestrutura: Filtros de Segurança, JWT, Tratamento Global de Erros
├── repository      # Camada de Acesso a Dados: Interfaces JPA que conversam com o PostgreSQL
└── service         # Camada de Serviço: Contém as Regras de Negócio e validações lógicas do sistema
```

---

## 🔗 Integração com o Frontend

Esta API foi construída de forma totalmente desacoplada e está preparada para alimentar um Front-end moderno (desenvolvido em **React / Next.js**). 

1.  **CORS Ativado:** A API permite conexões seguras originadas do cliente em ambiente local (`http://localhost:3000`).
2.  **Comunicação JSON:** Todas as requisições de entrada (`POST`/`PUT`) e respostas de saída seguem rigorosamente o padrão JSON estruturado.
3.  **Fluxo de Autenticação:**
    * O Frontend envia as credenciais para `/auth/login`.
    * A API valida e responde com um objeto contendo o token JWT (`TokenDTO`).
    * O Frontend armazena esse token no `localStorage` e o anexa no cabeçalho de todas as requisições seguintes como um `Authorization: Bearer <token>`.

---

## 🛠️ Como Executar o Projeto Localmente

Siga o passo a passo abaixo para rodar esta API na sua máquina:

### 1. Pré-requisitos Obrigatórios
* Ter o **Java 21** instalado configurado nas variáveis de ambiente (`JAVA_HOME`).
* Ter o **Maven** instalado (ou usar o wrapper `./mvnw` incluso).
* Ter o banco de dados **PostgreSQL** instalado e em execução.

### 2. Configuração do Banco de Dados
Abra o seu gerenciador do PostgreSQL (pgAdmin, DBeaver, terminal, etc.) e crie um banco de dados vazio com o nome exato do projeto:

```sql
CREATE DATABASE ativhub;
```

### 3. Ajustar as Credenciais
Abra o arquivo `src/main/resources/application.properties` e verifique se o usuário e a senha do banco batem com a sua instalação local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ativhub
spring.datasource.username=seu_usuario_postgres
spring.datasource.password=sua_senha_postgres
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```
*(Nota: O Flyway executará os scripts SQL contidos em `src/main/resources/db/migration` assim que o app ligar, criando todas as tabelas sozinhos!)*

### 4. Compilar e Rodar a Aplicação
Você pode rodar o projeto diretamente pela sua IDE (IntelliJ IDEA / Eclipse) executando a classe principal `AtivHubApplication.java`, ou utilizar o terminal de comandos na raiz do projeto:

```bash
# Compilar o projeto e baixar as dependências
mvn clean install

# Executar a aplicação Spring Boot
mvn spring-boot:run
```

O servidor iniciará com sucesso na porta padrão: **`http://localhost:8080`**.

---

## 🛣️ Principais Endpoints da API

Abaixo estão as rotas mapeadas no sistema. O controle de acesso é estrito, garantindo que alunos e professores acessem apenas os recursos pertinentes aos seus cargos.

### 🔐 Autenticação (`/auth`)
* `POST /auth/register` - Cadastro de novos usuários (informando nome, e-mail, senha e Cargo).
* `POST /auth/login` - Autenticação. Retorna o Token JWT de acesso.

### 👤 Usuários (`/users`)
* `GET /users/me` `[GERAL]` - Retorna os dados detalhados do perfil do usuário logado.
* `GET /users/ranking` `[GERAL]` - Retorna o Top 10 global de alunos ordenados por XP.

### 📚 Atividades (`/activities`)
* `POST /activities` `[PROFESSOR]` - Cria uma nova missão educacional.
* `GET /activities` `[GERAL]` - Lista todas as atividades gerais da plataforma.
* `GET /activities/professor` `[PROFESSOR]` - Lista apenas as missões criadas pelo professor logado.
* `GET /activities/{id}` `[GERAL]` - Busca os detalhes de uma atividade específica.
* `PUT /activities/{id}` `[PROFESSOR]` - Atualiza os dados de uma missão existente.
* `DELETE /activities/{id}` `[PROFESSOR]` - Remove uma missão do sistema.

### 📝 Submissões (Respostas)
* `POST /activities/{activityId}/submit` `[ALUNO]` - Envia uma resposta para uma atividade específica.
* `GET /activities/{activityId}/submissions` `[PROFESSOR]` - Lista todas as respostas enviadas para uma atividade.
* `GET /submissions/me` `[ALUNO]` - Lista todo o histórico de submissões do próprio aluno logado.
* `PUT /submissions/{submissionId}` `[ALUNO]` - Edita o conteúdo de uma resposta enviada (antes da correção).
* `DELETE /submissions/{submissionId}` `[ALUNO]` - Exclui uma submissão enviada.
* `PUT /submissions/{submissionId}/feedback` `[PROFESSOR]` - Atribui uma nota e comentário (feedback) à resposta de um aluno.

---

## 📝 Licença e Autoria
Desenvolvido com orgulho por **Lucas Viana da Silva**. Projeto focado em portfólio acadêmico e profissional para o ecossistema backend Java.

Se este projeto te ajudou a entender melhor a estrutura do Spring Boot com Security e Flyway, sinta-se à vontade para deixar uma ⭐ no repositório!
