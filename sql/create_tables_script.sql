create type scooter_type as ENUM ('LONG_RANGE', 'URBAN', 'FOLDING');
create type order_status as ENUM ('ONGOING','COMLETED');
create type scooter_status as ENUM ('RENTED','AVELIABLE');

CREATE TABLE IF NOT EXISTS customers(
customer_id SERIAL PRIMARY KEY NOT NULL,
name VARCHAR(40),
surname VARCHAR(40),
email VARCHAR(40) UNIQUE,
password_hash VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS scooters(
scooter_id SERIAL PRIMARY KEY NOT NULL,
type scooter_type NOT NULL,
status scooter_status NOT NULL,
battery_level INT CHECK (battery_level>=0 AND battery_level<=100)
);

CREATE TABLE IF NOT EXISTS orders(
order_id SERIAL PRIMARY KEY NOT NULL,
status order_status NOT NULL,
period TIME NOT NULL,
customer_id BIGINT REFERENCES customers(customer_id),
scooter_id BIGINT REFERENCES scooters(scooter_id)
);