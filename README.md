# kafka-streams-dockerized

Dockerized Kafka Streams application using Docker Compose.

## How to run?

1. Run `sbt docker`
2. Open you favourite terminal in master branch of this project and run `docker compose up -d` (-d stands for **
   detached** mode).
3. Run Kafka Console Producer using this command:

   `docker-compose exec broker kafka-console-producer --topic input --bootstrap-server localhost:9092`

4. In master branch of this project in another terminal run Kafka Console Consumer using this command:

   `docker-compose exec broker kafka-console-consumer --topic count-topic --from-beginning --bootstrap-server localhost:9092`
    - Now produce message from Producer to Consumer and wait for the event in Consumer terminal

5. You can also check out the Confluent Control Center on [localhost:9021](http://localhost:9021/)

    - Navigate to: `Topics > output > Messages` and produce message from here. The output will be available on your
      Consumer terminal.

This project includes GitHub Actions exercise.