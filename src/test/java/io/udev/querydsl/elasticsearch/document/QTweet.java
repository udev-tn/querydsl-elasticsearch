package io.udev.querydsl.elasticsearch.document;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QTweet is a Querydsl query type for Tweet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTweet extends EntityPathBase<Tweet> {

    private static final long serialVersionUID = 1701283826L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTweet tweet = new QTweet("tweet");

    public final QUser author;

    public final StringPath id = createString("id");

    public final StringPath text = createString("text");

    public final NumberPath<Long> views = createNumber("views", Long.class);

    public QTweet(String variable) {
        this(Tweet.class, forVariable(variable), INITS);
    }

    public QTweet(Path<? extends Tweet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTweet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTweet(PathMetadata metadata, PathInits inits) {
        this(Tweet.class, metadata, inits);
    }

    public QTweet(Class<? extends Tweet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QUser(forProperty("author")) : null;
    }

}

