package com.ercanbeyen.bankingapplication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.Arrays;
import java.util.List;

@Configuration
@PropertySource("classpath:cassandra.properties")
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration {
    private final String localDataCenter;
    private final String contactPoints;
    private final String entityBasePackage;
    private final String keyspace;

    CassandraConfig(
            @Value("${spring.data.cassandra.local-datacenter}") String localDataCenter,
            @Value("${spring.data.cassandra.entity-base-package}") String entityBasePackage,
            @Value("${spring.data.cassandra.contactpoints}") String contactPoints,
            @Value("${spring.data.cassandra.keyspace-name}") String keyspace) {
        this.entityBasePackage = entityBasePackage;
        this.localDataCenter = localDataCenter;
        this.contactPoints = contactPoints;
        this.keyspace = keyspace;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    public List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        CreateKeyspaceSpecification specification = CreateKeyspaceSpecification.createKeyspace(keyspace)
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .ifNotExists();

        return Arrays.asList(specification);
    }

    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[] { entityBasePackage };
    }

    @Override
    protected String getLocalDataCenter() {
        return localDataCenter;
    }

    @Override
    protected String getContactPoints() {
        return contactPoints;
    }
}
