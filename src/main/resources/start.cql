CREATE KEYSPACE cassandra
    WITH replication = {'class':'SimpleStrategy', 'replication_factor': 1}

CREATE TABLE ratings(
    id UUID PRIMARY KEY,
    customer_national_id TEXT,
    createdAt TIMESTAMP,
    rate DOUBLE
);

SELECT * FROM ratings