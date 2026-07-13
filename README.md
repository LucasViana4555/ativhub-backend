# 🎮 AtivHub - Backend API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

O **AtivHub** é uma plataforma gamificada de gestão de aprendizagem (LMS) focada na criação, distribuição e correção de missões e atividades educacionais.

Este repositório contém o **Backend do AtivHub**, que foi migrado para uma arquitetura distribuída de **Microsserviços** escaláveis e desacoplados, utilizando Java com Spring Boot, mensageria assíncrona com Apache Kafka e containers com Docker.

---

## 🚀 Funcionalidades Principais

*   **Arquitetura de Microsserviços:** Divisão do sistema em serviços independentes:
    *   **Main Service (Core):** Gerencia autenticação, controle de salas de aula, missões e submissões.
    *   **Gamification Service:** Gerencia o cálculo de ganho de XP dos alunos, regras de níveis e ranking global.
*   **Comunicação Assíncrona via Kafka:** Processamento desacoplado de eventos do sistema (como novas submissões avaliadas que geram atualização de XP) usando tópicos de mensageria no Apache Kafka.
*   **Autenticação e Autorização Seguras:** Controle de acesso baseado em cargos (**Roles**) gerenciado via Spring Security e Tokens JWT no Core Service.
*   **Verificação de E-mail:** Fluxo de cadastro integrado com envio de código numérico de 6 dígitos para validação de conta.
*   **Evolução Dinâmica:** Sistema automático de níveis calculado diretamente no microsserviço de gamificação de forma distribuída.
*   **Isolamento de Banco de Dados:** Cada microsserviço possui seu próprio banco de dados PostgreSQL independente (`ativhub_core` e `ativhub_gamification`) para total isolamento dos dados de negócio.
*   **Versionamento Automático:** Banco de dados versionado dinamicamente com Flyway migrations dentro de cada microsserviço.

---

## 🛠️ Tecnologias e Bibliotecas Utilizadas

*   **Java 21 (Temurin OpenJDK):** Recursos de performance e sintaxe modernos.
*   **Spring Boot 3.4.1:** Base de configuração rápida e estável para microsserviços.
*   **Apache Kafka (KRaft Mode):** Broker de mensageria de alta performance para comunicação assíncrona entre os serviços.
*   **Docker & Docker Compose:** Containerização e orquestração de toda a stack local (PostgreSQL, Kafka, Serviços).
*   **Spring Data JPA & Flyway:** Persistência relacional robusta com versionamento de banco independente por serviço.
*   **Spring Security & JWT (auth0):** Proteção criptografada de rotas de forma stateless.
*   **Lombok & Jakarta Validation:** Código conciso sem boilerplate e validações de DTO na entrada.

---

## 📦 Estrutura do Projeto

O projeto é organizado como um repositório multi-módulos conceitual contendo serviços isolados e arquivos de orquestração:

```text
AtivHub/
│
├── gamification-service/   # Microsserviço de Gamificação (XP, Níveis, Ranking) - Porta 8082
│   ├── src/
│   └── pom.xml
│
├── main-service/           # Microsserviço Core (Auth, Classes, Atividades) - Porta 8080
│   ├── src/
│   └── pom.xml
│
├── docker-compose.yml      # Orquestrador local da stack (Postgres, Kafka, Serviços)
└── init-db.sql             # Script SQL de inicialização dos múltiplos bancos de dados
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

Há duas maneiras principais de executar a stack de microsserviços do AtivHub localmente:

### Método 1: Usando Docker Compose (Recomendado)

Este método sobe o PostgreSQL, o Apache Kafka e ambos os microsserviços automaticamente em containers, pré-configurados para se comunicarem.

1. **Pré-requisitos:** Ter o **Docker** e o **Docker Compose** instalados.
2. **Executar a Stack:** Na raiz do projeto backend (onde está o arquivo `docker-compose.yml`), execute o comando:
   ```bash
   docker compose up --build -d
   ```
   *(Nota: O script `init-db.sql` criará os bancos de dados `ativhub_core` e `ativhub_gamification` automaticamente. As migrações do Flyway serão executadas por cada serviço no momento da inicialização).*
3. **Serviços Ativos:**
   * **Main Service:** `http://localhost:8080`
   * **Gamification Service:** `http://localhost:8082`
   * **Apache Kafka Broker:** `localhost:9092`
   * **PostgreSQL Database:** `localhost:5432`

---

### Método 2: Executando Manualmente (Desenvolvimento)

Se você preferir rodar os serviços via terminal ou através de sua IDE (como IntelliJ IDEA ou Eclipse):

1. **Pré-requisitos:**
   * **Java 21** instalado e configurado (`JAVA_HOME`).
   * **Maven** instalado (ou use o wrapper `./mvnw` em cada pasta).
   * **PostgreSQL** e **Apache Kafka** rodando localmente nas portas padrões (`5432` e `9092`).
2. **Configuração de Banco:** Crie os dois bancos necessários no PostgreSQL:
   ```sql
   CREATE DATABASE ativhub_core;
   CREATE DATABASE ativhub_gamification;
   ```
3. **Iniciar os Microsserviços:**
   Abra dois terminais separados e rode cada serviço:
   * **Para o Main Service:**
     ```bash
     cd main-service
     mvn spring-boot:run
     ```
   * **Para o Gamification Service:**
     ```bash
     cd gamification-service
     mvn spring-boot:run
     ```

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
