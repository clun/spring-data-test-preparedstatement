package com.datastax.springdatatest.conf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DropKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.springdatatest.todo.Todo;

@Configuration
@EnableCassandraRepositories(basePackages = { "com.datastax.springdatatest.todos" })
public class SpringDataConfig extends AbstractCassandraConfiguration implements BeanClassLoaderAware {

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspaceName;
    
    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoint;
    
    @Value("${spring.data.cassandra.local-datacenter}")
    private String locaDataCenter;
    
    /** {@inheritDoc} */
    @Override
    protected String getKeyspaceName() {
        return keyspaceName;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getContactPoints() {
        return contactPoint;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getLocalDataCenter() {
        return locaDataCenter;
    }
    
    /** {@inheritDoc} */
    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return Arrays.asList(CreateKeyspaceSpecification
              .createKeyspace(keyspaceName)
              .with(KeyspaceOption.DURABLE_WRITES, true)
              .withSimpleReplication(1)
              .ifNotExists());
    }
    
    /** {@inheritDoc} */
    @Override
    protected KeyspacePopulator keyspacePopulator() {
      return new ResourceKeyspacePopulator(scriptOf(
              SchemaBuilder.createTable(Todo.TABLENAME)
               .ifNotExists()
               .withPartitionKey(Todo.COLUMN_UID, DataTypes.UUID)
               .withColumn(Todo.COLUMN_TITLE, DataTypes.TEXT)
               .withColumn(Todo.COLUMN_COMPLETED, DataTypes.BOOLEAN)
               .withColumn(Todo.COLUMN_OFFSET, DataTypes.INT)
               .build().getQuery()));
    }

    @Bean
    public CassandraOperations cassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
        CassandraTemplate ct =  new CassandraTemplate(sessionFactory, converter);
        // NEW !
        ct.setUsePreparedStatements(true);
        return ct;
    }
    
    /** {@inheritDoc} */ 
    @Override
    protected List<DropKeyspaceSpecification> getKeyspaceDrops() {
      //return Arrays.asList(DropKeyspaceSpecification.dropKeyspace(keyspaceName).ifExists());
      return new ArrayList<>();
    }
    
}
