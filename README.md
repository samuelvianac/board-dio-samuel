# Board DIO Samuel

Projeto desenvolvido como parte do desafio prático da DIO.

## Objetivo
Construir um sistema de board em Java com persistência em MySQL usando JDBC e versionamento do banco com Liquibase.

## Tecnologias
- Java 17
- Gradle Kotlin DSL
- MySQL
- JDBC
- Liquibase

## Funcionalidades
- Criar board
- Listar boards
- Exibir board com colunas e cards
- Criar card
- Mover card entre colunas
- Bloquear card
- Desbloquear card

## Estrutura
- `src/main/java`: código-fonte
- `src/main/resources`: configurações e migrations

## Como configurar
Edite o arquivo `src/main/resources/db.properties` com:
- URL do banco
- usuário
- senha

## Banco de dados
Crie o banco antes de rodar a aplicação:

```sql
CREATE DATABASE board_dio;
