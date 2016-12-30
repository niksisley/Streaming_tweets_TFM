[![Build Status](https://drone.io/github.com/smallnest/kafka-example-in-scala/status.png)](https://drone.io/github.com/smallnest/kafka-example-in-scala/latest)

kafka producer and consumer example in scala and java


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
java -cp exercise-TFM-2.2.1-SNAPSHOT.jar com.colobu.kafka.TwitterKafkaProducer 10000 topic_master localhost:9092
```

### Run the Consumer
This example contains two consumers written in Java and in Scala.
You can run this for java:
``` sh
java -cp kafka_example-0.1.0-SNAPSHOT.jar com.colobu.kafka.ConsumerExample localhost:2181 group1 test_topic 10 0
```


### Elasticsearch

curl -XPUT 'http://localhost:9200/twitter/user/kimchy?pretty' -d '{ "name" : "Shay Banon" }'

curl -XPUT 'http://localhost:9200/twitter/tweet/1?pretty' -d '
{
    "user": "kimchy",
    "post_date": "2009-11-15T13:12:00",
    "message": "Trying out Elasticsearch, so far so good?"
}'

curl -XPUT 'http://localhost:9200/twitter/tweet/2?pretty' -d '
{
    "user": "kimchy",
    "post_date": "2009-11-15T14:12:12",
    "message": "Another tweet, will it be indexed?"
}'
Now, let’s see if the information was added by GETting it:

curl -XGET 'http://localhost:9200/twitter/user/kimchy?pretty=true'
curl -XGET 'http://localhost:9200/twitter/tweet/1?pretty=true'
curl -XGET 'http://localhost:9200/twitter/tweet/2?pretty=true'
Searching

Mmm search…, shouldn’t it be elastic?
Let’s find all the tweets that kimchy posted:

curl -XGET 'http://localhost:9200/twitter/tweet/_search?q=user:kimchy&pretty=true'
We can also use the JSON query language Elasticsearch provides instead of a query string:

curl -XGET 'http://localhost:9200/twitter/tweet/_search?pretty=true' -d '
{
    "query" : {
        "match" : { "user": "kimchy" }
    }
}'
Just for kicks, let’s get all the documents stored (we should see the user as well):

curl -XGET 'http://localhost:9200/twitter/_search?pretty=true' -d '
{
    "query" : {
        "match_all" : {}
    }
}'
We can also do range search (the postDate was automatically identified as date)

curl -XGET 'http://localhost:9200/twitter/_search?pretty=true' -d '
{
    "query" : {
        "range" : {
            "post_date" : { "from" : "2009-11-15T13:00:00", "to" : "2009-11-15T14:00:00" }
        }
    }
}'
There are many more options to perform search, after all, it’s a search product no? All the familiar Lucene queries are available through the JSON query language, or through the query parser.

Multi Tenant – Indices and Types

Man, that twitter index might get big (in this case, index size == valuation). Let’s see if we can structure our twitter system a bit differently in order to support such large amounts of data.

Elasticsearch supports multiple indices, as well as multiple types per index. In the previous example we used an index called twitter, with two types, user and tweet.

Another way to define our simple twitter system is to have a different index per user (note, though that each index has an overhead). Here is the indexing curl’s in this case:

curl -XPUT 'http://localhost:9200/kimchy/info/1?pretty' -d '{ "name" : "Shay Banon" }'

curl -XPUT 'http://localhost:9200/kimchy/tweet/1?pretty' -d '
{
    "user": "kimchy",
    "post_date": "2009-11-15T13:12:00",
    "message": "Trying out Elasticsearch, so far so good?"
}'

curl -XPUT 'http://localhost:9200/kimchy/tweet/2?pretty' -d '
{
    "user": "kimchy",
    "post_date": "2009-11-15T14:12:12",
    "message": "Another tweet, will it be indexed?"
}'
The above will index information into the kimchy index, with two types, info and tweet. Each user will get their own special index.

Complete control on the index level is allowed. As an example, in the above case, we would want to change from the default 5 shards with 1 replica per index, to only 1 shard with 1 replica per index (== per twitter user). Here is how this can be done (the configuration can be in yaml as well):

curl -XPUT http://localhost:9200/another_user?pretty -d '
{
    "index" : {
        "number_of_shards" : 1,
        "number_of_replicas" : 1
    }
}'
Search (and similar operations) are multi index aware. This means that we can easily search on more than one
index (twitter user), for example:

curl -XGET 'http://localhost:9200/kimchy,another_user/_search?pretty=true' -d '
{
    "query" : {
        "match_all" : {}
    }
}'
Or on all the indices:

curl -XGET 'http://localhost:9200/_search?pretty=true' -d '
{
    "query" : {
        "match_all" : {}
    }
}'
{One liner teaser}: And the cool part about that? You can easily search on multiple twitter users (indices), with different boost levels per user (index), making social search so much simpler (results from my friends rank higher than results from friends of my friends).

Distributed, Highly Available

Let’s face it, things will fail….

Elasticsearch is a highly available and distributed search engine. Each index is broken down into shards, and each shard can have one or more replica. By default, an index is created with 5 shards and 1 replica per shard (5/1). There are many topologies that can be used, including 1/10 (improve search performance), or 20/1 (improve indexing performance, with search executed in a map reduce fashion across shards).

In order to play with the distributed nature of Elasticsearch, simply bring more nodes up and shut down nodes. The system will continue to serve requests (make sure you use the correct http port) with the latest data indexed.

Where to go from here?

We have just covered a very small portion of what Elasticsearch is all about. For more information, please refer to the elastic.co website. General questions can be asked on the Elastic Discourse forum or on IRC on Freenode at #elasticsearch. The Elasticsearch GitHub repository is reserved for bug reports and feature requests only.

Building from Source

Elasticsearch uses Gradle for its build system. You’ll need to have version 2.13 of Gradle installed.

In order to create a distribution, simply run the gradle assemble command in the cloned directory.

The distribution for each project will be created under the build/distributions directory in that project.

See the TESTING file for more information about
running the Elasticsearch test suite.

Upgrading from Elasticsearch 1.x?

In order to ensure a smooth upgrade process from earlier versions of
Elasticsearch (1.x), it is required to perform a full cluster restart. Please
see the “setup reference”:
https://www.elastic.co/guide/en/elasticsearch/reference/current/setup-upgrade.html
for more details on the upgrade process.

