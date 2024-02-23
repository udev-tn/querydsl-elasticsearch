## Querydsl Elasticsearch

______

The Elasticsearch module provides integration with the `spring-data-elasticsearch` library.

### Maven integration

Add the following dependencies to your Maven project :

```XML

<dependency>
    <groupId>io.udev</groupId>
    <artifactId>querydsl-elasticsearch</artifactId>
    <version>${querydsl-elasticsearch.version}</version>
</dependency>
```

### Creating the query types

Code generation is currently accessible for Elasticsearch, pending approval of
the [pull request](https://github.com/querydsl/querydsl/pull/3685) in the querydsl-apt
repository.

### Querying

Querying with Querydsl Elasticsearch is as simple as this:

```JAVA 
QTweet qTweet = QTweet.tweet;

ElasticsearchOperations operations; // a bean initialized by spring-data-elasticsearch
ElasticsearchQuery query = new ElasticsearchQuery(qTweet, Tweet.class);
List<Document> documents = query
        .where(qTweet.views.between(2, 7).and(qTweet.title.startsWith("Breaking")))
        .limit(3)
        .offset(2)
        .fetch();
 ```
