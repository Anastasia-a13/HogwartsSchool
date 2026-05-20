CREATE TABLE car (
                     id SERIAL PRIMARY KEY,
                     brand VARCHAR(100) NOT NULL,
                     model VARCHAR(100) NOT NULL,
                     cost DECIMAL(10, 2) NOT NULL
);
CREATE TABLE person (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        age INT NOT NULL CHECK (age >= 0),
                        has_driver_license BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE TABLE person_car (
                            person_id INT NOT NULL REFERENCES person(id),
                            car_id INT NOT NULL REFERENCES car(id),
                            PRIMARY KEY (person_id, car_id)
);