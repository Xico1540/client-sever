# Trabalho_SD

## 📜 Descrição

Este projeto é uma aplicação de **mensagens e canais** desenvolvida em Java, utilizando **JDBC** para integração com um banco de dados **PostgreSQL**. A aplicação oferece funcionalidades como:

- **Gestão de usuários**: criação, autenticação e atribuição de papéis.
- **Mensagens**: envio e recebimento em canais e chats.
- **Canais**: criação de grupos e chats privados.

---

## 📂 Estrutura do Projeto

- **`AuthService`**: Gerencia a autenticação e sessões de usuário.
- **`ChannelService`**: Lida com operações relacionadas a canais, incluindo criação e recuperação.
- **`MessageService`**: Gerencia o envio, recebimento e armazenamento de mensagens.
- **`UserService`**: Cuida de funcionalidades relacionadas a usuários, como registro e gerenciamento de papéis.

---

## ✉️ Funcionalidades e Endpoints

### **🔐 AuthHandler**

- **LOGIN**
    - **Parâmetros**: `username`, `password`
    - **Descrição**: Autentica o usuário com base nas credenciais fornecidas.

- **REGISTER**
    - **Parâmetros**: `username`, `password`, `email`, `role`
    - **Descrição**: Registra um novo usuário.

---

### **👥 UserHandler**

- **GET_USERS**
    - **Parâmetros**: Nenhum
    - **Descrição**: Lista todos os usuários cadastrados.

- **UPDATE_ROLE**
    - **Parâmetros**: `userId`, `role`
    - **Descrição**: Atualiza o papel de um usuário.

- **DELETE_USER**
    - **Parâmetros**: `userId`
    - **Descrição**: Exclui um usuário pelo ID.

---

### **📌 RequestHandler**

- **OPEN_EMERGENCY_SEND_VIEW**
    - **Parâmetros**: Nenhum
    - **Descrição**: Abre a interface para envio de emergências.

- **NEW_REQUEST**
    - **Parâmetros**: `requestDetails`
    - **Descrição**: Cria uma nova solicitação.

- **GET_REQUESTS**
    - **Parâmetros**: Nenhum
    - **Descrição**: Recupera todas as solicitações cadastradas.

- **ACCEPT_REQUEST**
    - **Parâmetros**: `requestId`
    - **Descrição**: Aceita uma solicitação pelo ID.

- **DECLINE_REQUEST**
    - **Parâmetros**: `requestId`
    - **Descrição**: Recusa uma solicitação pelo ID.

---

### **📡 ChannelsHandler**

- **GET_CHANNELS**
    - **Parâmetros**: `userId`
    - **Descrição**: Retorna os canais associados ao usuário.

- **DELETE_CHANNEL**
    - **Parâmetros**: `channelId`
    - **Descrição**: Remove um canal pelo ID.

- **ENTER_CHAT**
    - **Parâmetros**: `channelId`
    - **Descrição**: Acessa o chat correspondente ao canal.

- **CREATE_GROUP**
    - **Parâmetros**: `groupName`, `userIds`
    - **Descrição**: Cria um grupo com o nome e usuários especificados.

- **CREATE_CHAT**
    - **Parâmetros**: `userId1`, `userId2`
    - **Descrição**: Cria um chat privado entre dois usuários.

- **SEND_MESSAGE**
    - **Parâmetros**: `channelId`, `messageContent`
    - **Descrição**: Envia uma mensagem para um canal.

---

## 🛠️ Instalação

1. Clone o repositório:
   ```bash
   git clone https://gitlab.com/Xico125/trabalho_sd.git
   cd trabalho_sd
    ```

## 🛠️ Execução

1. **Inicie os serviços com Docker**:
  - Certifique-se de que o Docker e o Docker Compose estão instalados.
  - No diretório raiz do projeto, onde o arquivo `docker-compose.yml` está localizado, execute:
    ```bash
    docker-compose up -d
    ```
  - Este comando:
    - Inicializa o serviço PostgreSQL com as configurações fornecidas.
    - Restaura o banco de dados inicial usando o arquivo `alertops-db.sql` (se configurado).

2. **Verifique se os serviços estão funcionando**:
  - Certifique-se de que o banco de dados está pronto:
    ```bash
    docker logs <nome_do_serviço_db>
    ```
  - O serviço `alertops-database-restorer` executará um script de restauração, se configurado.

3. **Configure o projeto no IntelliJ IDEA**:
  - Abra o projeto no IntelliJ IDEA.
  - Verifique as configurações do SDK (Java 11 ou superior).
  - No arquivo `application.properties`, configure as credenciais do banco de dados:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/alertops_db
    spring.datasource.username=alertops_user
    spring.datasource.password=password123
    ```

4. **Inicie o servidor manualmente**:
  - No **IntelliJ IDEA**:
    1. Abra o projeto.
    2. Navegue até a classe `MultithreadedServer.java` no caminho:
       ```
       trabalho_sd/src/main/java/pt/estg/sd/alertops/server/MultithreadedServer.java
       ```
    3. Clique com o botão direito na classe e selecione **Run 'MultithreadedServer.main()'**.

5. **Iniciar pela linha de comando (opcional)**:
  - Se preferir, você pode iniciar o servidor manualmente com:
    ```bash
    java -cp build/libs/alertOps-1.0-SNAPSHOT.jar pt.estg.sd.alertops.server.MultithreadedServer
    ```
    
6. **Exportar o banco de dados (opcional)**:
  - Para exportar o banco de dados, ative o perfil `export` no `docker-compose.yml`:
    ```bash
    docker-compose --profile export up alertops-database-exporter
    ```
  - O arquivo será salvo no diretório `./backup`.

---

## 💡 Recursos Futuros

- Melhorias na interface do usuário.
- Integração com APIs externas para autenticação.
