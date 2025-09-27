# mcp-api-demo

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

This simple demo shows how to expose a service (doing CRUD ops in a database) both as REST endpoint and as MCP Server.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

Navigate to the Dev UI, which is available at <http://localhost:8080/q/dev/>. 
From there you can access the SwaggerUI and the Tools UI, play around ! 

### Interacting with the MCP Server

The MCP server uses AI to interpret natural language and execute the appropriate tool. For example, you can ask it to filter tickets by a specific category, even though the category is computed dynamically from the ticket's content.

**Example Query:**

A user can provide a prompt like this:

> "Show me all tickets related to security vulnerabilities."

**How it Works:**

1.  **Intent Recognition**: The AI understands the user wants to "list tickets" with a "security" filter.
2.  **Tool Selection**: It identifies the `getTickets` tool, which has the description: *"Get a list of tickets. You can optionally filter by ticket type."*
3.  **Parameter Matching**: The AI matches the phrase "security vulnerabilities" to the `SECURITY` value in the `TicketType` enum.
4.  **Execution**: It calls the `getTickets` tool with the `SECURITY` type. The service then filters the tickets in memory based on keywords in their title and description.
5.  **Response**: The AI formats the filtered list of tickets and presents it to the user.

This allows for powerful, flexible queries using natural language, without needing to change the underlying database schema.

---

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/mcp-api-demo-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Running via Docker Compose

```shell script
docker-compose up -d
```

```shell script
docker-compose down -v  
```

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)

[Related Hibernate with Panache section...](https://quarkus.io/guides/hibernate-orm-panache)


### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
