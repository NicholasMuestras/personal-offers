-- liquibase formatted sql

-- changeset nobraztsov:1
CREATE TABLE rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    terms JSONB NOT NULL,
    product_id UUID,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
