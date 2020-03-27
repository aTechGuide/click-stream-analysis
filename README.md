# Click Stream Analysis

Analyzing the Click stream data from web page


## Tech Stack
- Spark Streaming
- Akka HTTP Server
- Kafka
- Scala
- sbt 



## Set Up
- Page URL to capture Click Stream -> `http://localhost:9998/`
- Start Docker Containers -> `docker-compose up`
- Connect to container -> `docker exec -it atechguide-kafka bash`
  - `cd /opt/kafka_2.12-2.4.1/bin/`
- Create a Topic -> `kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --replication-factor 1 --topic atechguide_click_stream`
- Connect to Kafka Consumer `kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic atechguide_click_stream`


# References
- This project is build as part of [rockthejvm.com  spark-streaming](https://rockthejvm.com/p/spark-streaming) Course
  
  