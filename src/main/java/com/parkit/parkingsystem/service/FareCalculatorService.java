package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FareCalculatorService {
    public static double DISCOUNT_PERCENTAGE = 0.05;

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }


        int inHour = ticket.getInTime().getHours();
        int outHour = ticket.getOutTime().getHours();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        float duration = hoursBetween(ticket.getInTime(), ticket.getOutTime()) / (float) (60 * 60 * 1000);

        if (duration <= 0.5){
            ticket.setPrice(0.0);
            return;
        }

        double timeSpent = (double) Math.round(duration * 1000)/1000;
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(timeSpent * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(timeSpent * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    public static Long hoursBetween(Date start, Date end) {
        return  end.getTime() - start.getTime();
    }

    public static double applyDiscountIfApplicable(double initialPrice, String registrationNumber) {
        if (isReccurentRegistrationNumber(registrationNumber, new TicketDAO())){
            return  initialPrice * (1 - DISCOUNT_PERCENTAGE);
        }
        return  initialPrice;
    }

    private static boolean isReccurentRegistrationNumber(String registrationNumber, TicketDAO ticketDAO) {
        return ticketDAO.getTicketByRegistrationNumber(registrationNumber).size() > 1;
    }
}