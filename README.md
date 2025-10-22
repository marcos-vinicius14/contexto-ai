# **Contexto.ai**

Uma aplicação RAG (Retrieval-Augmented Generation) robusta e escalável que permite aos usuários "conversar" com seus documentos. Faça upload de PDFs e obtenha respostas baseadas exclusivamente no conteúdo fornecido, alimentado por Spring AI e PgVector.

## **🌟 Visão Geral**

Na era da informação, encontrar dados específicos em grandes volumes de documentos é um desafio. O **Contexto.ai** resolve esse problema permitindo que os usuários façam upload de seus documentos e, em vez de pesquisar manualmente, possam simplesmente *perguntar*.

A aplicação utiliza um fluxo RAG para garantir que as respostas da IA sejam fiéis ao conteúdo dos documentos, eliminando alucinações e fornecendo respostas confiáveis e contextualizadas.

## **✨ Features Principais**

* **Autenticação Segura**: Sistema completo de registro e login de usuários com Spring Security e JWT.  
* **Upload de Documentos**: Interface moderna (Vue.js) para upload de arquivos PDF.  
* **Processamento Assíncrono**: A ingestão de documentos (leitura, *splitting* e geração de *embeddings*) é feita em segundo plano usando RabbitMQ, garantindo que a aplicação permaneça responsiva.  
* **Chat em Tempo Real**: Interface de chat interativa construída com WebSockets (STOMP) para comunicação instantânea.  
* **IA Contextualizada (RAG)**: As respostas são geradas pela combinação da busca vetorial no PgVector com o poder de LLMs via Spring AI.  
* **Arquitetura Multi-Tenant**: Os documentos e chats são isolados por usuário.

## **🏗️ Arquitetura da Solução**

O projeto é construído como um **Monolito Modular** aderindo aos princípios de **DDD (Domain-Driven Design)** e **Clean Architecture**.

### **Fluxo de Ingestão (Assíncrono)**

1. **Frontend (Vue.js)**: Usuário faz upload do PDF.  
2. **Backend (API)**: Endpoint REST recebe o arquivo, valida o JWT e salva os metadados do documento (status: PENDING).  
3. **Broker (RabbitMQ)**: Uma mensagem com o documentId é publicada em uma fila.  
4. **Backend (Worker)**: Um *consumer* escuta a fila, lê o PDF, divide-o em *chunks*, gera os *embeddings* (via Spring AI) e os salva no **PgVector**.  
5. **Backend (DB)**: O status do documento é atualizado para READY.

### **Fluxo de Chat (Síncrono \- WebSocket)**

1. **Frontend (Vue.js)**: Usuário se conecta ao endpoint WebSocket (autenticado com JWT).  
2. **Frontend (Vue.js)**: Envia uma pergunta para o *broker* STOMP (ex: /app/chat.send).  
3. **Backend (API)**: O ChatController recebe a mensagem.  
4. Backend (RAG Service):  
   a. Gera o embedding da pergunta.  
   b. Busca os chunks de texto mais relevantes no PgVector (filtrando pelo userId).  
   c. Monta um prompt com o contexto (os chunks) e a pergunta.  
   d. Chama o LLM via ChatClient (Spring AI).  
5. **Backend (API)**: A resposta do LLM é enviada de volta ao usuário pelo WebSocket.

## **🚀 Stack de Tecnologias**

| Categoria | Tecnologia |
| :---- | :---- |
| **Backend** | Java 17+, Spring Boot 3, Spring AI, Spring Security (JWT) |
|  | Spring WebSockets (STOMP), Spring AMQP |
| **Frontend** | Vue.js 3, Vite, Pinia (Estado), Vue Router |
| **Banco de Dados** | PostgreSQL com extensão PgVector |
| **Mensageria** | RabbitMQ |
| **Infra & DevOps** | Docker, Docker Compose |
| **Arquitetura** | Monolito Modular, DDD, Clean Architecture |

## **🏁 Como Executar (Ambiente de Desenvolvimento)**

Siga os passos abaixo para configurar e executar o projeto localmente.

### **Pré-requisitos**

* JDK 17 ou superior  
* Maven 3.8+  
* Node.js 18+  
* Docker e Docker Compose

### **1\. Configuração**

1. **Clone o repositório:**  
   git clone \[https://github.com/seu-usuario/contexto-ai.git\](https://github.com/seu-usuario/contexto-ai.git)  
   cd contexto-ai

2. Configure as Variáveis de Ambiente:  
   No diretório raiz do backend, crie um arquivo .env (ou configure application.properties) com as seguintes chaves. Um arquivo .env.example é fornecido como modelo.  
   \# Credenciais do PostgreSQL
   ```bash
   SPRING\_DATASOURCE\_URL=jdbc:postgresql://localhost:5432/seu_db 
   SPRING\_DATASOURCE\_USERNAME=seu\_usuario  
   SPRING\_DATASOURCE\_PASSWORD=sua\_senha

   \# Credenciais do RabbitMQ  
   SPRING\_RABBITMQ\_HOST=localhost

   \# Chave da API (OpenAI ou outro provedor suportado pelo Spring AI)  
   SPRING\_AI\_OPENAI\_API\_KEY=sk-sua-chave-aqui

   \# Segredo do JWT  
   JWT\_SECRET=seu-segredo-super-secreto-para-jwt
   ```

### **2\. Subir a Infraestrutura (Banco de Dados e Fila)**

Utilizamos Docker Compose para gerenciar nossos serviços de infraestrutura (PostgreSQL/PgVector e RabbitMQ).

\# A partir da raiz do projeto  
docker-compose up \-d

### **3\. Executar o Backend (Spring Boot)**

\# Navegue até a pasta do backend  
cd backend/  
\# Compile e execute  
mvn spring-boot:run

A API estará disponível em http://localhost:8080.

### **4\. Executar o Frontend (Vue.js)**

\# Navegue até a pasta do frontend  
```bash
cd frontend/
```

\# Instale as dependências
```bash
npm install
```
  
\# Execute o servidor de desenvolvimento  
```bash
npm run dev
```

A aplicação estará acessível em http://localhost:5173.

## **📂 Estrutura do Projeto**

O backend segue uma abordagem de **Monolito Modular** para separar as responsabilidades:

/src/main/java/com/myapp/  
├── config/                 \# Configuração global (Security, WebSocket)  
├── identityaccess/         \# Módulo de Autenticação e Usuário  
├── documentingestion/      \# Módulo de Upload e Processamento (Filas, Workers)  
└── chatinteraction/        \# Módulo de Chat (WebSocket, Lógica RAG)

Cada módulo segue os princípios da **Clean Architecture**, separando domain, application (casos de uso) e infrastructure (adaptadores de banco, filas, etc.).

## **📄 Licença**

Este projeto está distribuído sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.
