CREATE TABLE ScooterRents(
scooter_rent_id BIGINT PRIMARY KEY NOT NULL,
is_ongoing BOOLEAN NOT NULL,
period TIME NOT NULL,
customer_id BIGINT NOT NULL,
scooter_id BIGINT NOT NULL,

CONSTRAINT fk_customer_id
FOREIGN KEY (customer_id)
REFERENCES Customers(customer_id),

CONSTRAINT fk_scooter_id
FOREIGN KEY(scooter_id)
REFERENCES Scooters(scooter_id)
);

CREATE TYPE scooter_type AS ENUM ('long_range', 'urban', 'folding');

CREATE TABLE Customers(
customer_id BIGINT PRIMARY KEY NOT NULL,
name VARCHAR(40),
surname VARCHAR(40),
email VARCHAR(40) UNIQUE,
password_hash VARCHAR(60) NOT NULL,
scooter_rent_id BIGINT,

CONSTRAINT fk_scooter_rent_id_customers
FOREIGN KEY (scooter_rent_id)
REFERENCES ScooterRents(scooter_rent_id)
);

CREATE TABLE Scooters(
scooter_id BIGINT PRIMARY KEY NOT NULL,
available BOOLEAN NOT NULL,
type scooter_type NOT NULL,
battery_level INT CHECK (battery_level>=0 AND battery_level<=100)
scooter_rent_id BIGINT,

CONSTRAINT fk_scooter_rent_id_scooters
FOREIGN KEY (scooter_rent_id)
REFERENCES ScooterRents(scooter_rent_id)
);