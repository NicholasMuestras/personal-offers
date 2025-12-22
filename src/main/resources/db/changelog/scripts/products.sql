-- liquibase formatted sql

-- changeset nobraztsov:1
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2048) NOT NULL
);
