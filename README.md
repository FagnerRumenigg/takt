# Takt

API Java/Spring Boot para autenticação, recuperação de senha e perfil de usuário.

## Stack
- Java 21
- Spring Boot
- Spring Security
- JPA/Hibernate
- PostgreSQL
- Swagger/OpenAPI

## Instalação

### 1) Instalar o Docker
- Baixe e instale o **Docker Desktop**.
- No Mac, use a versão correta para o seu chip:
  - `Apple Silicon` → `arm64`
  - `Intel` → `amd64`
- Abra o Docker Desktop e espere o status ficar como `Running`.
- No primeiro uso, aceite as permissões que o sistema pedir.

### 2) Verificar a instalação
Abra o terminal e rode:
```bash
docker --version
docker compose version
```

Se os dois comandos responderem com versão, está pronto.

### 3) Baixar o projeto
```bash
git clone <URL_DO_REPOSITORIO>
cd Takt
```

### 4) Configurar o `.env`
Crie o arquivo `.env` na raiz do projeto com este conteúdo:
```env
POSTGRES_DB=string
POSTGRES_USER=string
POSTGRES_PASSWORD=string
SPRING_DATASOURCE_URL=string
SPRING_DATASOURCE_USERNAME=string
SPRING_DATASOURCE_PASSWORD=string
JWT_SECRET=changeitchangethischangethischangeitchangeit
```

### 5) Subir tudo
```bash
docker compose up --build
```

Depois disso:
- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

### 6) Testar no Swagger
1. Abra o Swagger.
2. Faça `POST /api/auth/register`.
3. Veja o token de confirmação nos logs do container `takt-app`.
4. Faça `POST /api/auth/confirm-email?token=...`.
5. Faça `POST /api/auth/login`.
6. Clique em `Authorize` e cole `Bearer <accessToken>`.
7. Teste `GET /api/auth/info`.

### 7) Resetar tudo
Se precisar começar do zero:
```bash
docker compose down -v
docker compose up --build
```

### Observação para Mac
- O fluxo acima funciona igual em Mac.
- Se for `Apple Silicon`, o Docker usa `arm64` automaticamente nas imagens oficiais.
- Se algo falhar, confira se o Docker Desktop está aberto e ativo.

## Variáveis
- `POSTGRES_DB` - nome do banco
- `POSTGRES_USER` - usuário do banco
- `POSTGRES_PASSWORD` - senha do banco
- `SPRING_DATASOURCE_URL` - URL do datasource local
- `SPRING_DATASOURCE_USERNAME` - usuário do datasource local
- `SPRING_DATASOURCE_PASSWORD` - senha do datasource local
- `JWT_SECRET` - segredo do JWT

## Fluxo local recomendado
- `docker compose up --build`
- abrir Swagger
- usar `POST /api/auth/register`
- confirmar e-mail via log do container `takt-app`
- fazer login
- usar `Authorize` no Swagger com o access token

## Endpoints principais
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/info`
- `PUT /api/auth/user`
- `POST /api/auth/confirm-email`
- `POST /api/auth/resend-confirmation`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`

## Observações
- `birthDate` usa formato `dd/MM/yyyy`
- os links de e-mail são simulados em log por enquanto
- o acesso autenticado usa `Bearer <accessToken>`
- a imagem do app é construída via `Dockerfile` e funciona em `amd64` e `arm64` usando as imagens oficiais

## Build de release
Para gerar a imagem `arm64` localmente:
```bash
mvn -Pbuild-image clean package
```
Esse perfil executa o build da aplicação e gera a imagem Docker para `linux/arm64`.
