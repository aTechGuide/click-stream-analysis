# Click Stream Analysis

Analyzing the Click stream data from web page.  

This App is divided into two main components

### Server App
- It starts the Akka Web Server and exposes the endpoints
- Captures the clicks from web page and sends it to kafka

### ClickStream App
- It fetches the data from Kafka server
- Aggregates the ROLLING average response time over the past N clicks

## Tech Stack
- Spark Structured Streaming
- Akka HTTP Server
- Kafka
- Scala
- sbt 

## How to Run the App
- Start Docker Containers from the project root -> `docker-compose up`
- Start the WebServer -> Run `src/main/scala/guide/atech/ServerApp.scala`
- Start the Spark Aggregation -> Run `src/main/scala/guide/atech/ClickStreamApp.scala`


## Useful Commands
- Page URL to capture Click Stream -> `http://localhost:9998/`
- Connect to container -> `docker exec -it atechguide-kafka bash`
  - `cd /opt/kafka_2.12-2.4.1/bin/`
- Create a Topic -> `kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic atechguide_click_stream`
- Connect to Kafka Consumer `kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic atechguide_click_stream`


# References
- This project is build as part of [rockthejvm.com  Spark Streaming with Scala](https://rockthejvm.com/p/spark-streaming) Course
  
  
