-- Drop and recreate database
DROP DATABASE IF EXISTS airline_reservation;
CREATE DATABASE airline_reservation;

\c airline_reservation;

-- Customer Details Table
CREATE TABLE customer_details (
    cust_code SERIAL PRIMARY KEY,
    t_date DATE NOT NULL DEFAULT CURRENT_DATE,
    cust_name VARCHAR(100) NOT NULL,
    father_name VARCHAR(100),
    gender VARCHAR(10),
    d_o_b DATE,
    address TEXT,
    tel_no VARCHAR(15),
    profession VARCHAR(50),
    security VARCHAR(50),
    concession VARCHAR(20)
);

-- Flight Information Table
CREATE TABLE flight_information (
    f_code VARCHAR(10) PRIMARY KEY,
    f_name VARCHAR(100) NOT NULL,
    c_code VARCHAR(10),
    t_exe_seatno INTEGER DEFAULT 30,
    t_eco_seatno INTEGER DEFAULT 100
);

-- Fleet Information Table
CREATE TABLE fleet_information (
    no_aircraft VARCHAR(20) PRIMARY KEY,
    club_pre_capacity VARCHAR(10),
    eco_capacity VARCHAR(10),
    engine_type VARCHAR(50),
    cruisespeed VARCHAR(20),
    air_length VARCHAR(20),
    wing_spam VARCHAR(20)
);

-- Fare Table
CREATE TABLE fare (
    route_code VARCHAR(10) PRIMARY KEY,
    s_place VARCHAR(50),
    via VARCHAR(50),
    d_place VARCHAR(50),
    d_time TIME,
    a_time TIME,
    f_code VARCHAR(10),
    c_code VARCHAR(10),
    fare DECIMAL(10,2),
    FOREIGN KEY (f_code) REFERENCES flight_information(f_code)
);

-- Reserved Seat Table
CREATE TABLE reserved_seat (
    f_code VARCHAR(10),
    t_date DATE,
    t_res_eco_seat INTEGER DEFAULT 0,
    t_res_exe_seat INTEGER DEFAULT 0,
    waiting_no INTEGER DEFAULT 0,
    PRIMARY KEY (f_code, t_date),
    FOREIGN KEY (f_code) REFERENCES flight_information(f_code)
);

-- Reservations Table
CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    flight_code VARCHAR(10) REFERENCES flight_information(f_code),
    customer_code INTEGER REFERENCES customer_details(cust_code),
    seat_class VARCHAR(10),
    seat_number INTEGER,
    travel_date DATE,
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    fare DECIMAL(10,2),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cancellation Table
CREATE TABLE cancellation (
    cancel_id SERIAL PRIMARY KEY,
    cust_code INTEGER REFERENCES customer_details(cust_code),
    class VARCHAR(20),
    s_no INTEGER,
    days_left INTEGER,
    hours_left INTEGER,
    basic_amount DECIMAL(10,2),
    cancel_amount DECIMAL(10,2),
    cancel_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO flight_information (f_code, f_name, c_code) VALUES
('AI101', 'Air India Express', 'ECO'),
('AI102', 'Air India Business', 'EXE'),
('SG201', 'SpiceJet Economy', 'ECO'),
('UK305', 'Vistara Premium', 'EXE');

INSERT INTO customer_details (cust_name, father_name, gender, tel_no, profession, concession) VALUES
('John Doe', 'Robert Doe', 'Male', '1234567890', 'Engineer', 'None'),
('Jane Smith', 'Michael Smith', 'Female', '0987654321', 'Doctor', 'Student'),
('Alice Johnson', 'David Johnson', 'Female', '1122334455', 'Teacher', 'Senior Citizen');

INSERT INTO reservations (flight_code, customer_code, seat_class, seat_number, travel_date, fare) VALUES
('AI101', 1, 'ECO', 15, CURRENT_DATE + 5, 5000.00),
('AI102', 2, 'EXE', 5, CURRENT_DATE + 3, 8000.00);