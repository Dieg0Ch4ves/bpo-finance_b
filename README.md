# BPO Finance — Backend (Kotlin + Spring Boot)

Projeto backend demo para BPO financeiro — gerenciamento de **Contas a Pagar (Payables)** e **Contas a Receber (Receivables)**.  
Este README descreve como rodar o projeto (Gradle), profiles `dev`/`prod`, testes e como usar a collection do Postman.

---

## Índice

- [Stack / Tecnologias](#stack--tecnologias)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Arquivos de configuração (`.properties`)](#arquivos-de-configuração-properties)
- [Exemplo `.env` (`.env-example`)](#exemplo-env-env-example)
- [Rodando a aplicação](#rodando-a-aplicação)
    - [Modo DEV (H2)](#modo-dev-h2)
    - [Modo PROD (Postgres)](#modo-prod-postgres)
- [Docker / Postgres local](#docker--postgres-local)
- [Testes (unitários / integração)](#testes-unitários--integração)
- [Postman — collection](#postman--collection)
- [Dicas e troubleshooting rápido](#dicas-e-troubleshooting-rápido)
- [Boas práticas](#boas-práticas)
- [Autor](#autor)

---

## Stack / Tecnologias

- Kotlin (JVM 17+ / recomendado JDK 21)
- Spring Boot 3.x (Web, Validation, Data JPA, Flyway)
- H2 (dev) / PostgreSQL (prod)
- Gradle (wrapper) — **`./gradlew`**
- Testes: JUnit 5, MockK, springmockk (para `@WebMvcTest`)
- Postman / Newman (coleções de API)

---

## Estrutura do repositório (sugestão)

```
bpo-finance/
├── build.gradle.kts
├── settings.gradle.kts
├── docker-compose.yml
├── .env # local (NÃO comitar)
├── postman/
│ └── bpo-finance_collection.json
├── src/
│ ├── main/
│ │ ├── kotlin/com/diego/bpo/
│ │ │ ├── controller/
│ │ │ ├── domain/
│ │ │ ├── dto/
│ │ │ ├── repository/
│ │ │ ├── service/
│ │ │ └── exception/
│ │ └── resources/
│ │ ├── application.properties
│ │ ├── application-dev.properties
│ │ └── application-prod.properties
│ └── test/
│ └── kotlin/com/diego/bpo/
└── README.md
```

Coloque a collection do Postman em `postman/` para versionamento.

---

## Arquivos de configuração (`.properties`)

> **Importante:** NÃO coloque `spring.profiles.active` dentro de `application-dev.properties` ou `application-prod.properties`. O profile deve ser ativado via variável de ambiente, argumento JVM/Gradle ou Run Configuration.

### `src/main/resources/application.properties` (padrão)
```properties
spring.application.name=bpo-finance
server.port=8080
spring.flyway.locations=classpath:db/migration
spring.jackson.serialization.write-dates-as-timestamps=false
logging.level.org.springframework=INFO
```

### ` src/main/resources/application-dev.properties` (DEV — H2)
```properties
spring.datasource.url=jdbc:h2:mem:bpo_finance_dev;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.flyway.enabled=true

logging.level.com.diego.bpo=DEBUG
```


### `src/main/resources/application-prod.properties` (PROD — PostgreSQL)
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/bpodb}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:bpo}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:bpo}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=10

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

logging.level.org.springframework=INFO
logging.level.com.diego.bpo=INFO
```

## Exemplo .env (.env-example)

Crie um arquivo .env local (não comitar). Aqui está um exemplo a colocar em ./.env-example:

```env
SPRING_PROFILES_ACTIVE= # dev/prod

SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
```

> Obs: Se você está rodando em dev, comente/remova as variáveis SPRING_DATASOURCE_* no .env ou defina SPRING_PROFILES_ACTIVE=dev — caso contrário variáveis de ambiente sobrescreverão as propriedades do profile e podem causar conflito de driver/URL.

## Rodando a aplicação

### Pré-requisitos

- JDK 17+ (recomendo 21) instalado e JAVA_HOME configurado.
- Docker/docker-compose (opcional para Postgres).

### Comandos Gradle (via wrapper)
#### Build
```bash
./gradlew clean build
```

### Autor
Diego Chaves — desenvolvedor fullstack.
Contato / repositório: [GitHub](https://github.com/Dieg0Ch4ves)