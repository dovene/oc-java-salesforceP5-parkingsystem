package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class FareCalculatorService {
    public static double DISCOUNT_IN_PERCENTAGE = 5;

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }


        //TODO: Some tests are failing here. Need to check if this logic is correct
        //get the difference correctly to resolve the first ticket
        float duration = hoursBetween(ticket.getInTime(), ticket.getOutTime()) ;

        // free fare if duration does not exceed 30mn or O.5 hour
        if (duration <= 0.5){
            ticket.setPrice(0.0);
            return;
        }

        // round it to avoid different result for same computation
        double timeSpent = (double) Math.round(duration * 1000)/1000;


        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(timeSpent * Fare.CAR_RATE_PER_HOUR);

               // ticket.setPrice(applyDiscountIfApplicable(timeSpent * Fare.CAR_RATE_PER_HOUR, ticket.getVehicleRegNumber()));
                break;
            }
            case BIKE: {
               // ticket.setPrice(applyDiscountIfApplicable(timeSpent * Fare.BIKE_RATE_PER_HOUR, ticket.getVehicleRegNumber()));

                ticket.setPrice(timeSpent * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }

        //Recurrent user
        if (ticket.isRecurrentUser()) {
            ticket.setPrice(ticket.getPrice() - (ticket.getPrice() * DISCOUNT_IN_PERCENTAGE / 100));
        }
    }

    public static float hoursBetween(Date start, Date end) {
        // compute the difference between start date and end date using date in milli second format
        // the result is returned as hour unit
        return  (end.getTime() - start.getTime() )/ (float) (60 * 60 * 1000);
    }

}