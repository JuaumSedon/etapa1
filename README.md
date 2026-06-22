Entrega da etapa 3 da atividade da aula  de web 3 .

<img width="1386" height="428" alt="image" src="https://github.com/user-attachments/assets/a2df8caf-2a30-40f3-8b27-0a4a697d0b93" />

<img width="1394" height="647" alt="asdas" src="https://github.com/user-attachments/assets/4d861259-f5ae-4e9c-9ec0-7bb28b15c14c" />

Entrega da etapa 2 da atividade da aula  de web 3 .
<img width="1005" height="923" alt="image" src="https://github.com/user-attachments/assets/888669fc-1f6c-46d3-8e46-e5eb1b908cad" />

Entrega da etapa 1 da atividade da aula  de web 3 .
<img width="1683" height="440" alt="image" src="https://github.com/user-attachments/assets/770c5e82-4f96-4a31-ade7-18b6e90665c4" />


# Sistema de Autenticação Passwordless (Etapa 4) 🚀

Este projeto implementa uma arquitetura de microsserviços com autenticação via código enviado por e-mail (Passwordless) e proteção de rotas utilizando **Spring Security** e **Tokens JWT**.

## 🏗️ Arquitetura do Sistema
O projeto é composto por 3 componentes principais:
1. **Frontend (Node.js/Express):** Roda na porta `3000`. Atua como interface de utilizador e como API Gateway/Proxy, repassando o Token JWT nas requisições.
2. **User Service (Spring Boot):** Roda na porta `8081`. Gere o banco de dados MySQL, valida as regras do Spring Security e gera os Tokens JWT.
3. **Email Service (Spring Boot):** Roda na porta `8082`. Consome mensagens do RabbitMQ e simula o envio do código de verificação.

## 🛠️ Tecnologias Utilizadas
* **Java 17** + Spring Boot (Web, Security, Data JPA, AMQP)
* **Node.js** + Express + Axios + Cors
* **MySQL** (Banco de dados relacional)
* **RabbitMQ** (Mensageria e Filas)
* **JWT (JSON Web Tokens)** (Autenticação e Autorização)

## ⚙️ Pré-requisitos
Antes de executar, certifique-se de ter instalado e rodando em sua máquina:
- Java 17 e Node.js
- Banco de dados MySQL rodando (`localhost:3306`) com o schema `ms_user`
- RabbitMQ rodando (Local ou via CloudAMQP)

## 🚀 Como Executar o Projeto

**Modo Automático (Windows):**
Basta dar um duplo clique no arquivo `iniciar.bat` localizado na raiz do projeto. Ele abrirá os terminais e iniciará todos os 3 serviços automaticamente.

**Modo Manual:**
1. Abra um terminal na pasta `email-service` e rode: `mvnw spring-boot:run`
2. Abra um terminal na pasta `secrest` e rode: `mvnw spring-boot:run`
3. Abra um terminal na pasta `frontend` e rode: `node server.js`
4. Acesse no navegador: `http://localhost:3000`

## 🔒 Fluxo de Autenticação
1. O utilizador insere o e-mail no frontend.
2. O backend gera um código, envia para o RabbitMQ, e o *Email Service* simula o envio imprimindo no terminal.
3. O utilizador digita o código no frontend. O backend valida e gera um **Token JWT de 3 partes**.
4. O utilizador preenche o perfil, e o Token é guardado no `SessionStorage` do navegador.
5. Ao acessar o Dashboard, o Node.js atua como Proxy e usa o Token (formato `Bearer {token}`) para acessar a rota protegida `/users` no Java.
6. O Spring Security valida a assinatura HMAC256 e libera o acesso!