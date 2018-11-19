package edu.osu.ride.model.driver;

public class Driver {
    public String type;
    public int driverArrivalInSecs;
    public int destinationArrivalInSecs;
    public double estimatedCost;

    public Driver(String type, int driverArrivalInSecs, int destinationArrivalInSecs, double estimatedCost) {
        this.type = type;
        this.driverArrivalInSecs = driverArrivalInSecs;
        this.destinationArrivalInSecs = driverArrivalInSecs + destinationArrivalInSecs;
        this.estimatedCost = estimatedCost;
    }
}
