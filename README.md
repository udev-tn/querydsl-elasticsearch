## Querydsl Elasticsearch

The Elasticsearch module provides integration with the `spring-data-elasticsearch` library.

### Maven integration

Add the following dependencies to your Maven project :

```XML
<dependency>
    <groupId>io.github.udev-tn</groupId>
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
ElasticsearchQuery query = new ElasticsearchQuery(operations, Tweet.class);
List<Document> documents = query
        .where(qTweet.views.between(2, 7).and(qTweet.title.startsWith("Breaking")))
        .limit(3)
        .offset(2)
        .fetch();
 ```

### Enable Spring Data Repositories

- To use the querydsl elasticsearch repository with synchronized operations, you will need to inject
  the [ElasticsearchQuerydslRepositoryFactoryBean.class](src/main/java/io/udev/querydsl/elasticsearch/repository/support/ElasticsearchQuerydslRepositoryFactoryBean.java)
  into **repositoryFactoryBeanClass** within the `@EnableElasticsearchRepositories` annotation of your application.
  This allows for seamless integration and efficient querying within your Elasticsearch environment.

```java
@SpringBootApplication
@EnableReactiveElasticsearchRepositories(repositoryFactoryBeanClass = io.udev.querydsl.elasticsearch.repository.support.ElasticsearchQuerydslRepositoryFactoryBean.class)
class ElasticsearchWithQuerydslSyncApplication {
    // application definition
}
```

- To use the querydsl elasticsearch repository with reactive operations, you will need to inject
  the [ReactiveElasticsearchQuerydslRepositoryFactoryBean.class](src/main/java/io/udev/querydsl/elasticsearch/repository/support/ReactiveElasticsearchQuerydslRepositoryFactoryBean.java)
  into **repositoryFactoryBeanClass** within the `@EnableElasticsearchRepositories` annotation of your application.

```java
@SpringBootApplication
@EnableReactiveElasticsearchRepositories(repositoryFactoryBeanClass = io.udev.querydsl.elasticsearch.repository.support.ReactiveElasticsearchQuerydslRepositoryFactoryBean.class)
class ReactiveElasticsearchWithQuerydslApplication {
    // application definition
}
```

Following that, you have the opportunity to expand your repositories by using the elasticsearch querydsl contacts.

- For synchronized instances:
  [ElasticsearchExecutor.java](src/main/java/io/udev/querydsl/elasticsearch/repository/ElasticsearchExecutor.java)
- For reactive instances: 
  [ReactiveElasticsearchExecutor.java](src/main/java/io/udev/querydsl/elasticsearch/repository/ReactiveElasticsearchExecutor.java)

### Building from Source

You don't need to build from source to use Querydsl-elasticsearch (binaries available on mvnrepository.com), but if you
want to test the most recent version, you can easily build querydsl-elasticsearch with the maven cli.

You need JDK 17 or above to build the main branch.

```shell
 mvn clean install
```
