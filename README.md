# Login Seguro

Sistema acadêmico de autenticação e autorização desenvolvido por João Pedro Pereira Simões, utilizando Java, Spring Boot, Thymeleaf e MongoDB Atlas.

O projeto possui estrutura modular para permitir adaptações futuras ao tema do Projeto Final de Curso.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Security
- Spring Data MongoDB
- Spring Session com MongoDB
- Thymeleaf
- Jakarta Validation
- Spring Mail
- Maven Wrapper

## Funcionalidades

- Cadastro com validação de nome, e-mail e senha.
- Senhas armazenadas como hash BCrypt com custo 12.
- Login e logout.
- Controle de acesso por três perfis.
- Consulta, edição, desativação e exclusão de usuários.
- Sessões persistidas no MongoDB Atlas.
- Encerramento das sessões após redefinição de senha ou alterações de acesso.
- Aceite obrigatório dos Termos de Uso e ciência da Política de Privacidade.
- Registro da data do aceite e das versões dos documentos.
- Recuperação de senha por e-mail.
- Tokens aleatórios de uso único, válidos por 15 minutos.
- Armazenamento somente do hash SHA-256 dos tokens.
- Limpeza automática dos tokens expirados por índice TTL.
- Temas visuais configuráveis.
- Navegação pelas páginas na mesma guia do navegador.

## Perfis de acesso

| Perfil | Permissões |
| --- | --- |
| USUARIO | Acessar o próprio painel e sair da conta. |
| MODERADOR | Acessar o painel e consultar a lista de usuários. |
| ADMINISTRADOR | Acessar o painel, consultar, editar, alterar perfis, ativar, desativar e excluir usuários. |

O cadastro público sempre cria contas com o perfil USUARIO.

O administrador não pode excluir, desativar ou rebaixar a própria conta, nem alterar o próprio e-mail pela administração.

## Requisitos para executar

- JDK 21 instalado.
- Git instalado.
- Conexão com a internet.
- Cluster MongoDB Atlas.
- Usuário de banco de dados autorizado no Atlas.
- Gmail com verificação em duas etapas e senha de app para envio dos e-mails.

O Maven é obtido pelo Maven Wrapper do projeto.

## Instalação

Clone o repositório:

```powershell
git clone https://github.com/916pereira/loginseguro.git
cd loginseguro
```

Confira o Java:

```powershell
java -version
javac -version
```

## Configuração local

Na raiz do projeto, copie o arquivo de exemplo:

```powershell
Copy-Item .\config\local.properties.example .\config\local.properties
```

No Linux ou macOS:

```bash
cp config/local.properties.example config/local.properties
```

Edite `config/local.properties` e substitua os exemplos:

```properties
spring.mongodb.uri=mongodb+srv://USUARIO:SENHA@SEU_CLUSTER.mongodb.net/?retryWrites=true&w=majority&tls=true
spring.mongodb.database=loginseguro

spring.mail.username=seuemail@gmail.com
spring.mail.password=SENHA_DE_APP_DO_GOOGLE

app.mail.from=seuemail@gmail.com
app.base-url=http://localhost:8080
app.theme=escuro
```

Utilize as credenciais do usuário de banco do Atlas na URI. Elas são diferentes das credenciais de acesso ao site do Atlas e da senha de app do Gmail.

Caso usuário ou senha contenham caracteres reservados de URL, aplique a codificação percentual nesses componentes da URI.

A senha de app do Gmail deve ser informada sem os espaços de apresentação. Utilize o mesmo Gmail em `spring.mail.username` e `app.mail.from`.

O arquivo local é carregado por `spring.config.import` e está protegido pelo `.gitignore`. Não publique esse arquivo, credenciais ou capturas de tela que contenham senhas.

Confirme que ele está ignorado:

```powershell
git check-ignore .\config\local.properties
```

Como alternativa ao arquivo local, configure as variáveis de ambiente `MONGODB_URI`, `MAIL_USERNAME` e `MAIL_PASSWORD`. Para personalização, estão disponíveis `APP_BASE_URL` e `APP_THEME`.

## Integração com MongoDB Atlas

1. Crie um cluster no MongoDB Atlas.
2. Em Database Access ou Database Users, crie um usuário de banco.
3. Conceda leitura e escrita no banco `loginseguro`.
4. Em Network Access, autorize o IP da máquina que executará o sistema.
5. Em Connect, selecione a conexão por driver Java e copie a URI.
6. Preencha a URI em `config/local.properties`, incluindo as credenciais corretas.
7. Execute a aplicação.

A conexão utiliza TLS. Os índices são criados automaticamente na inicialização.

### Coleções

| Coleção | Finalidade |
| --- | --- |
| usuarios | Dados cadastrais, hash da senha, perfil, estado da conta e registro de aceite. |
| sessoes | Sessões HTTP e informações necessárias à autenticação. |
| tokens_recuperacao | Hash dos tokens de recuperação, usuário associado e datas de criação e expiração. |

As sessões expiram após 30 minutos de inatividade. Sessões anônimas também podem existir, por exemplo, para manter o token de proteção CSRF dos formulários.

O índice `tokens_expiracao_ttl`, associado a `expiraEm`, possui `expireAfterSeconds: 0`. A remoção ocorre em segundo plano. O sistema verifica a expiração antes de aceitar um token, independentemente da limpeza do banco.

## Configuração do Gmail

1. Ative a verificação em duas etapas na conta Google.
2. Acesse https://myaccount.google.com/apppasswords.
3. Crie uma senha de app com o nome Login Seguro.
4. Configure o endereço Gmail e a senha gerada no arquivo local.

O envio utiliza SMTP na porta 587 com autenticação, STARTTLS obrigatório e verificação da identidade do servidor.

Algumas contas gerenciadas por organizações podem restringir a criação de senhas de app.

## Execução

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Acesse:

http://localhost:8080/login

Para encerrar a execução, pressione Ctrl + C.

Execute os comandos a partir da raiz do projeto para que o arquivo `config/local.properties` seja encontrado.

## Primeiro administrador

1. Execute o sistema e cadastre uma conta.
2. No Atlas, abra o banco `loginseguro` e a coleção `usuarios`.
3. Localize a conta pelo e-mail.
4. Altere o campo `role` para `ADMINISTRADOR`, mantendo `ativo` como `true`.
5. Caso a conta esteja logada, saia e entre novamente.

Essa operação é uma configuração inicial realizada por quem administra o banco. Depois, utilize a interface administrativa para gerenciar os demais perfis.

## Recuperação de senha

1. Clique em Esqueceu a senha? na tela de login.
2. Informe o e-mail de uma conta cadastrada e ativa.
3. Abra o link recebido no e-mail.
4. Informe e confirme uma nova senha.
5. Entre com a nova senha.

A resposta da solicitação é genérica para evitar revelar se um e-mail está cadastrado.

O sistema limita novas solicitações sequenciais para a mesma conta durante 60 segundos. Esse intervalo não substitui uma proteção distribuída contra abuso.

O link expira em 15 minutos e só pode ser utilizado uma vez. A senha antiga deixa de funcionar e as sessões existentes da conta são encerradas.

## Estrutura do projeto

| Caminho | Responsabilidade |
| --- | --- |
| src/main/java/br/com/joaopereira/loginseguro/config | Configurações de segurança, senhas e sessões. |
| src/main/java/br/com/joaopereira/loginseguro/controller | Rotas, formulários e atributos compartilhados das páginas. |
| src/main/java/br/com/joaopereira/loginseguro/dto | Dados de entrada e validações. |
| src/main/java/br/com/joaopereira/loginseguro/model | Entidades e perfis. |
| src/main/java/br/com/joaopereira/loginseguro/repository | Acesso aos dados no MongoDB. |
| src/main/java/br/com/joaopereira/loginseguro/service | Regras de negócio, recuperação, envio de e-mails e encerramento de sessões. |
| src/main/resources/templates | Páginas Thymeleaf. |
| src/main/resources/templates/fragments | Menu e estilos compartilhados. |
| src/main/resources/static/css | Estilos principais. |
| src/main/resources/static/css/themes | Temas personalizados. |
| config/local.properties.example | Modelo público de configuração. |

## Temas e futuras adaptações

O CSS principal define a estrutura da interface e as variáveis de cores, fontes e bordas.

Para usar o tema escuro:

```properties
app.theme=escuro
```

Para usar o tema claro:

```properties
app.theme=claro
```

Reinicie a aplicação após alterar a configuração. Se necessário, atualize o navegador com Ctrl + F5.

Para adicionar outro tema:

1. Crie um arquivo em `src/main/resources/static/css/themes`, por exemplo `personalizado.css`.
2. Redefina as variáveis CSS necessárias.
3. Configure `app.theme=personalizado`.
4. Reinicie o sistema.

Os nomes dos temas devem começar com letra minúscula e conter apenas letras minúsculas, números ou hífens, com até 40 caracteres.

O fragmento `estilos.html` aplica o tema às páginas. O fragmento `menu.html` centraliza a navegação.

Textos, identidade visual e páginas podem ser adaptados ao PFC mantendo os serviços de autenticação, usuários e sessões.

## Segurança

- Hash BCrypt das senhas.
- Validação de entrada no servidor.
- Índice único para e-mail.
- Proteção CSRF mantida nos formulários.
- Logout realizado por POST.
- Renovação do identificador da sessão no login.
- Autorização por rotas e métodos administrativos.
- Cookies de sessão HTTP-only e SameSite.
- TLS na conexão com o Atlas.
- STARTTLS no envio de e-mails.
- Tokens de recuperação armazenados como hash.
- Consumo atômico do token para impedir sua reutilização.
- Encerramento das sessões após alterações de acesso.
- Credenciais locais excluídas do versionamento.

A execução local utiliza HTTP. Para disponibilização pública, configure HTTPS, cookies Secure e `app.base-url` com o endereço HTTPS real. Revise também o armazenamento de segredos e a proteção contra tentativas excessivas de autenticação.

## Validação e empacotamento

Compilar:

```powershell
.\mvnw.cmd compile
```

Executar os testes existentes:

```powershell
.\mvnw.cmd test
```

Gerar o pacote:

```powershell
.\mvnw.cmd clean package
```

Os testes de contexto podem exigir acesso ao Atlas e a configuração local preenchida.

Após gerar o pacote:

```powershell
java -jar .\target\loginseguro-0.0.1-SNAPSHOT.jar
```

### Cenários verificados manualmente

- Cadastro, login e logout.
- Restrição de acesso por perfil.
- Recuperação por e-mail e login com a nova senha.
- Recusa da senha antiga.
- Recusa de reutilização do link.
- Encerramento de sessão após recuperação.
- Desativação com bloqueio de acesso.
- Mudança de perfil com encerramento da sessão.
- Exclusão com encerramento da sessão e bloqueio de login.
- Aplicação do tema claro.
- Inicialização e envio de e-mail pela configuração local.
- Existência do índice TTL no Atlas.

## Gitflow

O fluxo de desenvolvimento utiliza:

- `main`: versões entregues.
- `develop`: integração das alterações.
- `feature/loginseguro`: implementação das funcionalidades.
- `release/*`: preparação de uma versão para entrega.
- `hotfix/*`: correções urgentes de versões já entregues, quando necessárias.

As funcionalidades são integradas em `develop`. Uma branch de release reúne os ajustes finais e depois é integrada em `main` e `develop`, com uma tag para identificar a versão entregue.

## Autor

João Pedro Pereira Simões
Sistemas de Informação — Universidade de Mogi das Cruzes

## Referências técnicas

- Spring Boot: https://docs.spring.io/spring-boot/
- Spring Security: https://docs.spring.io/spring-security/reference/
- Spring Data MongoDB: https://docs.spring.io/spring-data/mongodb/reference/
- Spring Session: https://docs.spring.io/spring-session/reference/
- Thymeleaf: https://www.thymeleaf.org/documentation.html
- MongoDB Atlas: https://www.mongodb.com/docs/atlas/
- Senhas de app Google: https://support.google.com/accounts/answer/185833?hl=pt-BR