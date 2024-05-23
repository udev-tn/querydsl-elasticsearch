package io.udev.querydsl.elasticsearch;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import io.udev.querydsl.elasticsearch.document.QTweet;
import io.udev.querydsl.elasticsearch.document.Tweet;
import org.assertj.core.api.Condition;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Field;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.SimpleField;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.data.elasticsearch.core.TotalHitsRelation.EQUAL_TO;

class ElasticsearchQueryTest {

    private ElasticsearchQuery<Tweet, ?> query;
    @Mock
    private ElasticsearchOperations operations;

    @Spy
    private ElasticsearchConverter converter = new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext());

    @Captor
    private ArgumentCaptor<Query> queryCaptor;

    private AutoCloseable closeable;

    private static final QTweet Q_TWEET = QTweet.tweet;

    @BeforeEach
    public void setUp() {
        // Given
        closeable = openMocks(this);


        // When

        // Then
        query = new ElasticsearchQuery<>(operations, Tweet.class);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenQueryHasOrder() {
        // Given
        Tweet tweet = new Tweet();
        SearchHit<Tweet> hit = new SearchHit<>(null, null, null, 1, null, null, null, null, null, null, tweet);
        SearchHits<Tweet> hits = new SearchHitsImpl<>(1, EQUAL_TO, 10, null, null, singletonList(hit), null, null, null);

        long pageSize = 3;
        long pageNum = 2;

        // When
        doReturn(hits).when(operations).search(queryCaptor.capture(), eq(Tweet.class));

        // Then
        List<Tweet> results = query.limit(pageSize)
                .offset(pageNum * pageSize)
                .orderBy(new OrderSpecifier<>(Order.DESC, Q_TWEET.views))
                .fetch();

        assertEquals(1, results.size());
        Query result = queryCaptor.getValue();

        assertThat(result).isInstanceOfSatisfying(StringQuery.class,
                stringQuery -> assertThat(stringQuery)
                        .extracting(StringQuery::getSource)
                        .isEqualTo("{\"match_all\":{}}")
        );

        Pageable pageable = result.getPageable();
        assertEquals(pageable.getPageSize(), pageSize);
        assertEquals(pageable.getPageNumber(), pageNum);
        assertThat(pageable)
                .extracting(Pageable::getSort)
                .isEqualTo(Sort.by("views").descending());

        verify(operations, times(1)).search(result, Tweet.class);
        verifyNoMoreInteractions(operations);
    }

    /**
     * Performs a query based on the provided predicate.
     *
     * @param predicate The predicate to be applied in the query.
     * @return Returns the {@link Query} for assertion testing.
     */
    private Query doQuery(Predicate predicate) {
        // Given
        BooleanBuilder where = new BooleanBuilder(predicate);

        Tweet tweet = new Tweet();
        SearchHit<Tweet> hit = new SearchHit<>(null, null, null, 1, null, null, null, null, null, null, tweet);
        SearchHits<Tweet> hits = new SearchHitsImpl<>(1, EQUAL_TO, 10, null, null, singletonList(hit), null, null, null);

        // When
        doReturn(hits).when(operations).search(queryCaptor.capture(), eq(Tweet.class));

        // Then
        List<Tweet> results = query.where(where).fetch();
        assertEquals(1, results.size());

        Query result = queryCaptor.getValue();

        verify(operations, times(1)).search(result, Tweet.class);
        verifyNoMoreInteractions(operations);

        return result;
    }

    /**
     * Asserts the first criteria of the {@link Query} using the provided consumer.
     *
     * @param result   The {@link Query} to be asserted.
     * @param consumer The consumer to apply assertions on the {@link Query}.
     */
    private void assertFirstCriteria(Query result, Consumer<ObjectAssert<Object>> consumer) {
        assertThat(result).isInstanceOfSatisfying(CriteriaQuery.class, criteriaQuery -> {
            converter.updateQuery(criteriaQuery, Tweet.class);
            Criteria criteria = criteriaQuery.getCriteria();

            ObjectAssert<Object> anAssert = assertThat(criteria)
                    .extracting(Criteria::getCriteriaChain)
                    .asInstanceOf(InstanceOfAssertFactories.LIST)
                    .hasSize(1)
                    .first();

            consumer.accept(anAssert);
        });
    }

    @Test
    public void whenQueryAString() {
        Query result = doQuery(Q_TWEET.text.contains("ab"));

        Field field = new SimpleField("text");
        field.setFieldType(FieldType.Text);
        assertFirstCriteria(result, anAssert -> anAssert.isEqualTo(Criteria.where(field).contains("ab")));
    }

    @Test
    public void whenQueryANumber() {
        Query result = doQuery(Q_TWEET.views.between(2, 7));

        assertFirstCriteria(result, anAssert -> anAssert.is(
                new Condition<>(arg -> { // custom equals
                    boolean isValid = true;
                    if (arg instanceof Criteria criteria) {
                        Criteria.CriteriaEntry entry = criteria.getQueryCriteriaEntries().iterator().next();

                        if (entry instanceof Collection<?> values) {
                            isValid = values.iterator().next().toString().equals("2");
                            isValid &= values.iterator().next().toString().equals("7");
                        }
                        isValid &= Criteria.OperationKey.BETWEEN.equals(entry.getKey());
                    }
                    return isValid;
                }, "")
        ));
    }

    @Test
    public void whenQueryANullity() {
        Query result = doQuery(Q_TWEET.author.isNotNull());

        Field field = new SimpleField("author");
        field.setFieldType(FieldType.Object);
        assertFirstCriteria(result, anAssert -> anAssert.isEqualTo(Criteria.where(field).exists()));
    }

    @Test
    public void whenQueryEquality() {
        String username = randomUUID().toString().split("-")[0];
        Query result = doQuery(Q_TWEET.author.username.eq(username));

        Field field = new SimpleField("author.username");
        field.setFieldType(FieldType.Text);
        assertFirstCriteria(result, anAssert -> anAssert.isEqualTo(Criteria.where(field).is(username)));
    }

    @Test
    public void whenQueryCollection() {
        Query result = doQuery(Q_TWEET.views.in(10, 20));

        assertFirstCriteria(result, anAssert -> anAssert.is(
                new Condition<>(arg -> { // custom equals
                    boolean isValid = true;
                    if (arg instanceof Criteria criteria) {
                        Criteria.CriteriaEntry entry = criteria.getQueryCriteriaEntries().iterator().next();

                        if (entry instanceof Collection<?> values) {
                            isValid = values.iterator().next().toString().equals("10");
                            isValid &= values.iterator().next().toString().equals("20");
                        }
                        isValid &= Criteria.OperationKey.IN.equals(entry.getKey());
                    }
                    return isValid;
                }, "")
        ));
    }

    @Test
    void whenQueryStringMatchesOperation() {
        Query result = doQuery(Q_TWEET.text.matches("ab"));

        Field field = new SimpleField("text");
        field.setFieldType(FieldType.Text);
        assertFirstCriteria(result, anAssert -> anAssert.isEqualTo(Criteria.where(field).matches("ab")));
    }

    @Test
    void whenQueryStringIsEmptyOperation() {
        Query result = doQuery(Q_TWEET.text.isEmpty());

        Field field = new SimpleField("text");
        field.setFieldType(FieldType.Text);
        assertFirstCriteria(result, anAssert -> anAssert.isEqualTo(Criteria.where(field).empty()));
    }

}
