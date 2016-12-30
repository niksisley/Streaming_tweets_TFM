[![Build Status](https://drone.io/github.com/smallnest/kafka-example-in-scala/status.png)](https://drone.io/github.com/smallnest/kafka-example-in-scala/latest)

kafka producer and consumer example in java


### start zookeeper
if you have installed zookeeper, start it, or
run the command:
``` sh
bin/zookeeper-server-start.sh config/zookeeper.properties
```

### start kafka with default configuration
``` sh
bin/kafka-server-start.sh config/server.properties
```

### create a topico
``` sh
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 10 --topic topic_master
```
Lista topics

bin/kafka-topics.sh --zookeeper localhost:2181 --list

Producir mensajes

bin/kafka-console-producer.sh --broker-list localhost:9095 --topic topic_master

Consumir mensajes

bin/kafka-console-consumer.sh --zookeeper localhost:2181 --from-beginning --topic topic_master

### package this example
``` sh
mvn clean package
```

it will package compiled classes and its dependencies into a jar.

### Run the Producer
This example also contains two producers written in Java and in scala.
you can run this for java:
``` sh
java -cp exercise-TFM-2.2.1-SNAPSHOT.jar com.nico.kafka.ProducerKafkaTwitter
```

### Run the Consumer
This example contains two consumers written in Java and in Scala.
You can run this for java:
``` sh
java -cp exercise-TFM-2.2.1-SNAPSHOT.jar com.nico.kafka.KafkaConsumer

```