# Flor & Folha Floricultura

Sistema web de controle de estoque para uma floricultura, desenvolvido como trabalho final da disciplina de Tópicos Especiais em Desenvolvimento de Software III (Análise e Desenvolvimento de Sistemas).

Autora: Fernanda

## Tecnologias

- Java 21 e Maven
- Jakarta EE 10: Jakarta Faces (JSF), EJB, CDI e JPA (EclipseLink)
- PrimeFaces 13 e Template Manhattan 7
- PostgreSQL 17
- GlassFish 7

## Funcionalidades

- **Login com filtro administrativo:** todas as telas exigem login, e a pasta `/admin/` é exclusiva para administradores. As senhas são guardadas com hash e salt (PBKDF2).
- **Perfis de usuário:** `ADMIN` (Administrador) e `FUNCIONARIO` (Funcionário). Só o administrador exclui registros e gerencia usuários.
- **CRUDs básicos** (criar, editar, excluir, listar e buscar por nome): Categorias, Locais e Plantas (com foto).
- **Controle de Estoque (funcionalidade principal):** entradas e saídas por planta, com histórico, bloqueio de saída maior que o estoque disponível e alerta de estoque baixo (5 unidades ou menos).
- **Usuários:** cadastro de usuários, exclusivo do administrador.
- **Página inicial:** vitrine com banner e cartões das plantas, com foto, preço e situação do estoque.

## Requisitos do enunciado

| Requisito | Onde está |
|---|---|
| Arquitetura JSF + Postgres + Template Manhattan | Projeto inteiro |
| Login com filtro administrativo | `AutenticacaoFilter`, `LoginBean` e pasta `webapp/admin` |
| Pelo menos um Enum | `Perfil` e `TipoMovimentacao` |
| Três CRUDs básicos | Categoria, Local e Planta |
| Funcionalidade principal | Controle de Estoque (`MovimentacaoEstoque`) |
| Relacionamento one-to-many | `Planta` para `MovimentacaoEstoque` |
| Persistência no Postgres | Unidade `floriculturaPU` e pool `jdbc/floricultura` |

## Modelo de dados

- `Usuario` (perfil: enum `Perfil`)
- `Categoria` 1 — N `Planta`
- `Local` 1 — N `Planta`
- `Planta` 1 — N `MovimentacaoEstoque` (tipo: enum `TipoMovimentacao`)

As tabelas são criadas automaticamente pelo EclipseLink na primeira execução.

## Como rodar

Pré-requisitos: JDK 21, Maven 3.9, GlassFish 7 e PostgreSQL com um servidor em execução.

Nos comandos abaixo, `asadmin` fica em `<pasta-do-glassfish>/bin/`. Troque os trechos em maiúsculas pelos seus valores.

**1. Banco de dados.** Crie um banco vazio chamado `floricultura`.

**2. Driver do PostgreSQL.** Copie o `postgresql-42.7.3.jar` para a pasta `glassfish/lib` do GlassFish. Depois inicie (ou reinicie) o servidor:

```
asadmin start-domain
```

**3. Conexão com o banco (pool e recurso JNDI).** A senha do banco fica no servidor, e não no código:

```
asadmin create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --restype javax.sql.DataSource --property "serverName=localhost:portNumber=5432:databaseName=floricultura:user=postgres:password=SENHA_DO_POSTGRES" floriculturaPool
asadmin create-jdbc-resource --connectionpoolid floriculturaPool jdbc/floricultura
```

**4. Senha do administrador inicial.** No primeiro início com o banco vazio, o sistema cria o usuário `admin@floricultura.com` usando esta propriedade (use letras e números, com pelo menos 6 caracteres):

```
asadmin create-jvm-options "-Dfloricultura.admin.senha=SENHA_DO_ADMIN"
asadmin restart-domain
```

**5. Compilar e instalar:**

```
mvn clean package
asadmin deploy --force=true target/floricultura.war
```

**6. Acessar:** http://localhost:8080/floricultura/ e entrar com `admin@floricultura.com` e a senha definida no passo 4.

## Observações

- O tema Manhattan está na pasta `repo/`, um repositório Maven local dentro do projeto, e o `pom.xml` já aponta para ele. Por ser um tema licenciado, mantenha este repositório privado.
- As fotos das plantas são salvas na pasta `floricultura-uploads`, dentro da pasta do usuário que executa o GlassFish. Elas não fazem parte do repositório, então, em outra máquina, os cartões mostram uma folhinha até que as fotos sejam enviadas pelo cadastro de plantas.
- Nenhuma senha fica no código: a do banco está no pool do GlassFish e a do administrador, numa propriedade do servidor.