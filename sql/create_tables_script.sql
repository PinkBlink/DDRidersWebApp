CREATE TABLE IF NOT EXISTS scooter_rents(
scooter_rent_id SERIAL PRIMARY KEY NOT NULL,
is_ongoing BOOLEAN NOT NULL,
period TIME NOT NULL,
customer_id BIGINT NOT NULL,
scooter_id BIGINT NOT NULL
);

CREATE TYPE scooter_type AS ENUM ('long_range', 'urban', 'folding');

CREATE TABLE IF NOT EXISTS customers(
customer_id SERIAL PRIMARY KEY NOT NULL,
name VARCHAR(40),
surname VARCHAR(40),
email VARCHAR(40) UNIQUE,
password_hash VARCHAR(60) NOT NULL,
scooter_rent_id BIGINT
);

CREATE TABLE IF NOT EXISTS Scooters(
scooter_id SERIAL PRIMARY KEY NOT NULL,
available BOOLEAN NOT NULL,
type scooter_type NOT NULL,
battery_level INT CHECK (battery_level>=0 AND battery_level<=100),
scooter_rent_id BIGINT
);

ALTER TABLE scooter_rents
ADD CONSTRAINT fk_sooter_rents_customer_id
FOREIGN KEY(customer_id)
REFERENCES customers(customer_id);

ALTER TABLE scooter_rents
ADD CONSTRAINT fk_sooter_rents_scooter_id
FOREIGN KEY(scooter_id)
REFERENCES scooters(scooter_id);

ALTER TABLE customers
ADD CONSTRAINT fk_customers_scooter_rent_id
FOREIGN KEY(scooter_rent_id)
REFERENCES scooter_rents(scooter_rent_id);

ALTER TABLE scooters
ADD CONSTRAINT fk_scooters_scooter_rent_id
FOREIGN KEY(scooter_rent_id)
REFERENCES scooter_rents(scooter_rent_id);