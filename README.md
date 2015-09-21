# compojure-project-template

A work in progress. 

This is a derived version of https://github.com/metosin/compojure-api-sample example project. This version also includes support for Postgres, Flyway migrations and a bit better support for configurations. 

## Components

- Compojure (https://github.com/weavejester/compojure)
- Ring (https://github.com/ring-clojure/ring)
- Swagger (https://github.com/swagger-api/swagger-core)
- ring-swagger-ui (https://github.com/metosin/ring-swagger-ui)
- Component (https://github.com/stuartsierra/component)
- Prismatic schema (https://github.com/Prismatic/schema)
- Schema tools (https://github.com/metosin/schema-tools)
- Flyway (http://flywaydb.org/)
- Midje (https://github.com/marick/Midje)

## Usage

### Run the application locally

`lein ring server`

### Run the tests

`lein midje`

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

