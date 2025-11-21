package com.airline.model;

import java.time.LocalDate;

public class Reservation {
    private int reservationId;
    private String flightCode;
    private int customerCode;
    private String seatClass;
    private int seatNumber;
    private LocalDate travelDate;
    private String status; // CONFIRMED, WAITING
    private double fare;

    public Reservation() {}

    public Reservation(int reservationId, String flightCode, int customerCode,
                       String seatClass, int seatNumber, LocalDate travelDate,
                       String status, double fare) {
        this.reservationId = reservationId;
        this.flightCode = flightCode;
        this.customerCode = customerCode;
        this.seatClass = seatClass;
        this.seatNumber = seatNumber;
        this.travelDate = travelDate;
        this.status = status;
        this.fare = fare;
    }

    // Getters and Setters
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getFlightCode() { return flightCode; }
    public void setFlightCode(String flightCode) { this.flightCode = flightCode; }

    public int getCustomerCode() { return customerCode; }
    public void setCustomerCode(int customerCode) { this.customerCode = customerCode; }

    public String getSeatClass() { return seatClass; }
    public void setSeatClass(String seatClass) { this.seatClass = seatClass; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
}