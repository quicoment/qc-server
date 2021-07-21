# QUICOMENT
<img width="683" alt="quicoment" src="https://user-images.githubusercontent.com/40485341/126188123-6e29c9f2-cfb7-4393-848c-9735a533aac4.png">

## real-time comment project
QUICOMENT is a very simple implementation of real-time comment services. Especially this `qc-server` repository is responsible for post(board) CRUD API and routes requests related to comments of particular post to the message queue(RabbitMQ).


## Enviroment
- Kotlin
- Spring Boot
- Gradle
- JPA
- JUnit
- RabbitMQ
- Redis

## How to Start

### 1. set Rabbit MQ in operation
  - LOCAL  
  write down a `docker-compose.yml` file in anywhere, and run this `docker-compose up` command in where the file is. (remeber the docker daemon must be run in advance)  
  
  Here is a exmaple `docker-compose.yml` file.
```yml
version: "3.9"

services:
  rabbimq:
    image: rabbitmq:management
    container_name: message-broker
    ports:
      - "5672:5672"  # for sender and consumer connections (remember the port for spring boot)
      - "15672:15672"  # for rabbit mq management GUI
```
  - OR you can use any cloud service.

### 2. write rabbit-mq configuration file in `~/src/main/resources/rabbit-mq.yml`
The `config/RabbitConfiguration.kt` and `service/PostService.kt` need this property file.

Here is a example `rabbit-mq.yml`
```yml
rabbitmq.host: localhost
rabbitmq.port: 5672  (same as docker-compose.yml)
rabbitmq.username: guest
rabbitmq.password: guest
rabbitmq.queue-name-domain: q.exmaple
rabbitmq.exchange-name-domain: e.exmaple
```

### 3. run Spring Boot application
```bash 
./gradlew bootRun
```
