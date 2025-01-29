CREATE TYPE scooter_type AS ENUM ('long_range', 'urban', 'folding');

CREATE TABLE IF NOT EXISTS customers(
customer_id SERIAL PRIMARY KEY NOT NULL,
name VARCHAR(40),
surname VARCHAR(40),
email VARCHAR(40) UNIQUE,
password_hash VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS scooters(
scooter_id SERIAL PRIMARY KEY NOT NULL,
available BOOLEAN NOT NULL,
type scooter_type NOT NULL,
battery_level INT CHECK (battery_level>=0 AND battery_level<=100)
);

CREATE TABLE IF NOT EXISTS orders(
order_id SERIAL PRIMARY KEY NOT NULL,
is_ongoing BOOLEAN NOT NULL,
period TIME NOT NULL,
customer_id BIGINT REFERENCES customers(customer_id),
scooter_id BIGINT REFERENCES scooters(scooter_id)
);