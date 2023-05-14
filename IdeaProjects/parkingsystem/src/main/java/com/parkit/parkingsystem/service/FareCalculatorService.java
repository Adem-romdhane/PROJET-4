package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    private static final int MILLISECONDS_TO_HOUR = 1000 * 60 * 60;
    private static TicketDAO ticketDAO = new TicketDAO();

    public void calculateFare(Ticket ticket, boolean discount) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:"
                    + ticket.getOutTime().toString());
        }

        Ticket ticketFound = ticketDAO.getTicket(ticket.getVehicleRegNumber());

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        double discountCar = 0.0; // Réduction du prix
        double discountBike = 0.0;

        double duration = (outHour - inHour)/MILLISECONDS_TO_HOUR; // MILLISECONDS_TO_HOUR;


        if (discount ) { // on vérifie si l'utilisateur est resté moins de 30 min
            discountCar = 0.05 * duration * Fare.CAR_RATE_PER_HOUR;
            discountBike = 0.05 * duration * Fare.BIKE_RATE_PER_HOUR;
        }

        if (duration <= 0.5) { // si le user reste moins de 30 min
            ticket.setPrice(0.0); //on set le prix à 0
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR - discountCar);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR - discountBike);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}