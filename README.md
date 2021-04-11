# JetBrains intro task: message service

[![Main CI](https://github.com/kuzznya/message-service/actions/workflows/main.yml/badge.svg)](https://github.com/kuzznya/message-service/actions/workflows/main.yml)
[![codecov](https://codecov.io/gh/kuzznya/message-service/branch/master/graph/badge.svg?token=lwRONJ44R4)](https://codecov.io/gh/kuzznya/message-service)

[Heroku deployment - link to OpenAPI docs](https://jb-message-service.herokuapp.com/swagger-ui.html)

## Features

- Create messages from templates using provided variables
- Validate variables by defining types in templates
- Schedule message sending using cron (e.g. */5 * * * * *) or fixed interval (e.g. 00/500ms, 1s, 2m, 30h)
- Send a created message to multiple recipients, either HTTP servers (defined by URL) or emails 
  (defined as mailto:\<email address>)
- Schedule message sending & cancel scheduled tasks

## Technological stack

- Java 11
- Gradle
- Spring Boot 2.4
- Spring Data JPA
- Project Lombok
- H2DB
- OpenAPI Documentation
