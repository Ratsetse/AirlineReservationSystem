-- Insert sample data into flight_information
INSERT INTO flight_information VALUES
('AI101', 'Air India Express', 'ECO', 20, 100),
('AI102', 'Air India Business', 'EXE', 30, 80),
('SG201', 'SpiceJet Economy', 'ECO', 25, 120);

-- Insert sample data into fare
INSERT INTO fare VALUES
('R001', 'Delhi', 'Mumbai', 'Goa', '08:00', '12:00', 'AI101', 'ECO', 5000.00),
('R002', 'Delhi', 'Direct', 'Goa', '14:00', '16:30', 'AI102', 'EXE', 8000.00),
('R003', 'Delhi', 'Bangalore', 'Goa', '10:00', '15:00', 'SG201', 'ECO', 4500.00);

-- Insert sample data into fleet_information
INSERT INTO fleet_information VALUES
('A320-001', '12', '138', 'Jet', '828 km/h', '37.57 m', '35.8 m'),
('B737-001', '16', '144', 'Jet', '839 km/h', '39.5 m', '35.9 m');