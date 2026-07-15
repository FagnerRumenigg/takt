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

### Passo a passo
1. Instale o Docker Desktop e confirme `docker --version` e `docker compose version`.
2. Clone o repositório e entre na pasta do projeto.
3. Crie o arquivo `.env` na raiz.
4. Faça login no Docker Hub com `docker login` se a imagem estiver privada.
5. Suba a aplicação com `docker compose up`.
6. Abra `http://localhost:8080/swagger-ui.html` para testar a API.

### `.env`
```env
POSTGRES_DB=takt
POSTGRES_USER=takt
POSTGRES_PASSWORD=takt
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/takt
SPRING_DATASOURCE_USERNAME=takt
SPRING_DATASOURCE_PASSWORD=takt
JWT_SECRET=dev-secret-change-me-dev-secret-change-me
```

### Reset
```bash
docker compose down -v
docker compose up
```

## Variáveis
- `POSTGRES_DB` - nome do banco
- `POSTGRES_USER` - usuário do banco
- `POSTGRES_PASSWORD` - senha do banco
- `SPRING_DATASOURCE_URL` - URL do datasource local
- `SPRING_DATASOURCE_USERNAME` - usuário do datasource local
- `SPRING_DATASOURCE_PASSWORD` - senha do datasource local
- `JWT_SECRET` - segredo do JWT

## Fluxo local recomendado
- `docker compose up`
- abrir Swagger
- usar `POST /takt/auth/register`
- confirmar e-mail via log do container `takt-app`
- fazer login
- usar `Authorize` no Swagger com o access token

## Endpoints principais
- `POST /takt/auth/register`
- `POST /takt/auth/login`
- `GET /takt/auth/info`
- `PUT /takt/auth/user`
- `POST /takt/auth/confirm-email`
- `POST /takt/auth/resend-confirmation`
- `POST /takt/auth/forgot-password`
- `POST /takt/auth/reset-password`
- `POST /takt/auth/refresh`
- `POST /takt/auth/logout`
- `GET /takt/categories`
- `POST /takt/categories`
- `PATCH /takt/categories/{id}`
- `DELETE /takt/categories/{id}`
- `GET /takt/productivity-levels`
- `PATCH /takt/productivity-levels`
- `GET /takt/time-entries`
- `GET /takt/time-entries/day`
- `POST /takt/time-entries`
- `PATCH /takt/time-entries/{id}`
- `DELETE /takt/time-entries/{id}`
- `GET /takt/calendar?startDate=2026-07-01&endDate=2026-07-31`

## Observações
- `birthDate` usa formato `dd/MM/yyyy`
- os links de e-mail são simulados em log por enquanto
- o acesso autenticado usa `Bearer <accessToken>`
- a imagem do app é publicada no Docker Hub como `fagnerrumenigg/takt:1.0.0`
- os valores do `.env` e do `application.yml` são apenas para desenvolvimento local
- `POST /takt/auth/register` cria automaticamente os 4 níveis padrão de produtividade do usuário
- blocos de tempo validam título obrigatório, nota opcional, limite de 500 caracteres, ordem temporal e conflito de horários
- `GET /takt/calendar` reúne blocos do dia, categorias e níveis de produtividade para o dashboard diário
- o seed inicial cria categorias globais padrão: Reunião, Programação, Estudo, Pausa, Almoço e Exercício

## Build de release
Para gerar e publicar a imagem `arm64`:
```bash
mvn clean install -Pbuild-image
```
Esse perfil faz push da imagem Docker para `linux/amd64` e `linux/arm64` no Docker Hub.
