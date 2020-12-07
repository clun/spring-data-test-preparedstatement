package com.datastax.springdatatest.todo;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.insertInto;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.support.CachedPreparedStatementCreator;
import org.springframework.data.cassandra.core.cql.support.PreparedStatementCache;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentEntity;
import org.springframework.data.cassandra.repository.support.MappingCassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;
import org.springframework.stereotype.Repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;


@Repository
public class TodoRepository extends SimpleCassandraRepository<Todo, UUID> {

    protected final CqlSession cqlSession;
    
    protected final CassandraOperations cassandraTemplate;
    
    protected final PreparedStatementCache cache = PreparedStatementCache.create();
    
    @SuppressWarnings("unchecked")
    public TodoRepository(CqlSession cqlSession, CassandraOperations ops) {
        super(new MappingCassandraEntityInformation<Todo, UUID>(
                (CassandraPersistentEntity<Todo>) ops.getConverter().getMappingContext()
                .getRequiredPersistentEntity(Todo.class), ops.getConverter()), ops);
        this.cqlSession = cqlSession;
        this.cassandraTemplate = ops;
    }
    
    public Optional<Todo> findById_prepared(UUID id) {
        List<Todo> values = cassandraTemplate.getCqlOperations()
                .query(prepareFindById().bind().setUuid(Todo.COLUMN_UID, id), 
                       (row, rowNum) -> cassandraTemplate.getConverter().read(Todo.class, row));
        if (values.isEmpty()) return Optional.empty();
        return Optional.ofNullable(values.get(0));
    }
    
    public void save_prepared(Todo todo) {
        cassandraTemplate.getCqlOperations().execute(prepareSave().bind()
                .setUuid(Todo.COLUMN_UID, todo.getUid())
                .setString(Todo.COLUMN_TITLE, todo.getTitle())
                .setBoolean(Todo.COLUMN_COMPLETED, todo.isCompleted())
                .setInt(Todo.COLUMN_OFFSET, todo.getOffset()));
    }
    
    private PreparedStatement prepareSave() {
        return CachedPreparedStatementCreator.of(cache, insertInto(Todo.TABLENAME)
                    .value(Todo.COLUMN_UID, bindMarker(Todo.COLUMN_UID))
                    .value(Todo.COLUMN_TITLE, bindMarker(Todo.COLUMN_TITLE))
                    .value(Todo.COLUMN_COMPLETED, bindMarker(Todo.COLUMN_COMPLETED))
                    .value(Todo.COLUMN_OFFSET, bindMarker(Todo.COLUMN_OFFSET))
                    .build()).createPreparedStatement(cqlSession);
    }
    
    private PreparedStatement prepareFindById() {
        return CachedPreparedStatementCreator.of(cache, selectFrom(Todo.TABLENAME)
                    .column(Todo.COLUMN_UID).column(Todo.COLUMN_TITLE)
                    .column(Todo.COLUMN_COMPLETED).column(Todo.COLUMN_OFFSET)
                    .whereColumn(Todo.COLUMN_UID).isEqualTo(bindMarker(Todo.COLUMN_UID))
                    .build()).createPreparedStatement(cqlSession);
    }
    
}
