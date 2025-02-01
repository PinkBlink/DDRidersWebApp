CREATE TYPE scooter_type AS ENUM ('LONG_RANGE', 'URBAN', 'FOLDING');
CREATE TYPE order_status AS ENUM ('ONGOING','COMLETED');
CREATE TYPE scooter_status AS ENUM ('RENTED','AVAILABLE');

CREATE TABLE IF NOT EXISTS customers(
customer_id SERIAL PRIMARY KEY NOT NULL,
name VARCHAR(40),
surname VARCHAR(40),
email VARCHAR(40) UNIQUE,
password_hash VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS scooters(
scooter_id SERIAL PRIMARY KEY NOT NULL,
scooter_type scooter_type NOT NULL,
scooter_status scooter_status NOT NULL,
battery_level INT CHECK (battery_level>=0 AND battery_level<=100)
);

CREATE TABLE IF NOT EXISTS orders(
order_id SERIAL PRIMARY KEY NOT NULL,
customer_id BIGINT REFERENCES customers(customer_id),
scooter_id BIGINT REFERENCES scooters(scooter_id),
start_time TIMESTAMP NOT NULL,
end_time TIMESTAMP,
order_status order_status NOT NULL
);