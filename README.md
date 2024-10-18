# PROJECT 3 - Chatop Rental Backend

## Install Dependencies

- **Maven** `Apache Maven 3.6.3`
- **JDK** `21 LTS`

## Compilation

1. If you need env vars using dotven file look at [Env section](#using-env-file)
2. Using maven run `mvn package -Dmaven.test.skip` to compile
3. You can run the app using `java -jar target/rental-backend-1.0.0-SNAPSHOT.jar` and your `.env` file or env variables

## Prepare needed services

Choose between **MariaDB** or **PostgresSql**

1. Migration can be launched using ***Flyway*** (Postgres only), to do this use env vars `APP_DB_MIGRATE_ON_START` and `APP_DB_MIGRATION_SCHEMA` -> see `.env.example`
2. Else look at `resources/sql/migration_1.0.0.mariadb.sql` or `resources/sql/migration_1.0.0.postgres.sql` and run one of these
3. Optionally seed data using `resources/sql/seed_1.0.0.mariadb.sql` or `resources/sql/seed_1.0.0.postgres.sql`

## Env variables

### Using `.env` file

- If you want to use .env file the uncomment `spring.config.import=optional:file:.env[.properties]` at first line in `src/main/resources/application.properties`
  Then you main run the app form project using `mvn spring-boot:run`

### Using Eclipse

- Use **Eclipse** with [Spring tool suite](https://marketplace.eclipse.org/content/spring-tools-4-aka-spring-tool-suite-4)
  Then set env variables

### Minimal env variables

You have to set at leat these env variables :

- **APP_DB_URL** (ex: `jdbc:postgresql://localhost:5432/postgres`)
- **APP_DB_USER** (ex: `postgres`)
- **APP_DB_PWD** (ex: `My_P4assW0rd.S3cur3.`)
- **APP_JWT_SECRET** (regex description: `[0-9a-f]{256}`)

*Please refer to `.env.example`*

## Run the application

- Use **Maven** to start the application `mvn spring-boot:run`
- OR use **Spring tool suite** to run the app.

## Testing

- You can use **Postman** with the resource `resources/postman/rental.postman_collection.json`.
- Or you can download and run **Angular** app from [Github P3-Full-Stack-portail-locataire](https://github.com/OpenClassrooms-Student-Center/P3-Full-Stack-portail-locataire).

## Open Api / Swagger

1. You may set `SPDOC_ENABLE` and `SPDOC_SWAGGER_ENABLE` env vars to **true**
2. Run the app (Assumming `SERVER_PORT` is 3001)
3. You can access Open Api specs at <http://localhost:3001/api-docs>
4. You can access the Swagger interface at <http://localhost:3001/swagger-ui>
