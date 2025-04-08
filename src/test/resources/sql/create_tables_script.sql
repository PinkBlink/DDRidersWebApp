CREATE TYPE scooter_type AS ENUM ('LONG_RANGE', 'URBAN', 'FOLDING');
CREATE TYPE order_status AS ENUM ('ONGOING','COMPLETED');
CREATE TYPE scooter_status AS ENUM ('RENTED','AVAILABLE');


CREATE TABLE IF NOT EXISTS customers(
    id UUID PRIMARY KEY NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE  NOT NULL,
    name VARCHAR(40),
    surname VARCHAR(40),
    email VARCHAR(40) UNIQUE,
    password VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS scooters(
    id UUID PRIMARY KEY NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    scooter_type scooter_type NOT NULL,
    scooter_status scooter_status NOT NULL,
    battery_level INT CHECK (battery_level>=0 AND battery_level<=100)
);

CREATE TABLE IF NOT EXISTS orders(
    id UUID PRIMARY KEY NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    customer_id UUID REFERENCES customers(id),
    scooter_id UUID REFERENCES scooters(id),
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE,
    order_status order_status NOT NULL
);
