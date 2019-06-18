package modelClasses;

import java.io.Serializable;

/**
 * The class representing the log of the data computed using the fraud
 * detection algorithm on a specific bank customer and the customer's living areas.
 * @author Efe Acer
 * @version 1.0
 */
public class LogData implements Serializable {

    // Inner classes to be able to separately store the key attributes and other attributes
    public static class Key implements Serializable { // Attributes composing the Key
        int timeOfDay; // TM_GRP_FLG: Flag to separate a day into three groups
        int weekDay; // WK_DAY_FLG: Flag to distinguish between week days and weekends
        double latitudeNumber;  // LATITUDE_NUM
        double longitudeNumber; // LONGITUDE_NUM
        int customerID; // IP_ID_OUT

        public Key(int timeOfDay, int weekDay, double latitudeNumber, double longitudeNumber, int customerID) {
            this.timeOfDay = timeOfDay;
            this.weekDay = weekDay;
            this.latitudeNumber = latitudeNumber;
            this.longitudeNumber = longitudeNumber;
            this.customerID = customerID;
        }

        @Override
        public String toString() {
            return String.format("[KEY: (%d, %d, %f, %f, %10d)]", timeOfDay, weekDay,
                    latitudeNumber, longitudeNumber, customerID);
        }

        // Getters

        public int getTimeOfDay() { return timeOfDay; }

        public int getWeekDay() { return weekDay; }

        public double getLatitudeNumber() { return latitudeNumber; }

        public double getLongitudeNumber() { return longitudeNumber; }

        public int getCustomerID() { return customerID; }
    }

    public static class Attributes implements Serializable { // Other attributes
        // LIV_AREA_DIST_OUT: Minimum distance between the place of transaction and the customer's living areas
        int distToLivArea;
        int distToHome; // HOME_DIST_OUT: Distance between the place of transaction and the customer's home
        int distToWork; // WORK_DIST_OUT: Distance between the place of transaction and the customer's work address

        Attributes(int distToLivArea, int distToHome, int distToWork) {
            this.distToLivArea = distToLivArea;
            this.distToHome = distToHome;
            this.distToWork = distToWork;
        }

        @Override
        public String toString() {
            return String.format("[ATTRIBUTES: (%5d, %5d, %5d)]", distToLivArea, distToHome, distToWork);
        }
    }

    // Properties
    private Key key;
    private Attributes attributes;

    /**
     * Constructor of the LogData class, which models the cached fraud computation data.
     * @param timeOfDay Flag to separate a day into three groups
     * @param weekDay Flag to distinguish between week days and weekends
     * @param latitudeNumber Latitude number of the place of transaction
     * @param longitudeNumber Longitude number of the place of transaction
     * @param customerID Customer's ID
     * @param distToLivArea Minimum distance between the place of transaction and the customer's living areas
     * @param distToHome Distance between the place of transaction and the customer's home
     * @param distToWork Distance between the place of transaction and the customer's work address
     */
    public LogData(int timeOfDay, int weekDay, double latitudeNumber, double longitudeNumber, int customerID,
                   int distToLivArea, int distToHome, int distToWork) {
        this.key = new Key(timeOfDay, weekDay, latitudeNumber, longitudeNumber, customerID);
        this.attributes = new Attributes(distToLivArea, distToHome, distToWork);
    }

    /**
     * Alternative constructor for the LogData class.
     * @param key The key attributes of the LogData object
     * @param attributes The other attributes of the LogData object
     */
    public LogData(Key key, Attributes attributes) {
        this.key = key;
        this.attributes = attributes;
    }

    /**
     * Getter for the key attributes of the LogData object.
     * @return Key attributes of the LogData object
     */
    public Key getKey() { return key; }

    /**
     * Getter for the attributes of the LogData object.
     * @return Attributes of the LogData object
     */
    public Attributes getAttributes() { return attributes; }

    /**
     * Returns the String representation of the LogData object.
     * @return The String representation of the LogData object
     */
    @Override
    public String toString() {
        return String.format("( %d, %d, %f, %f, %10d, %5d, %5d, %5d)",
        key.timeOfDay, key.weekDay, key.latitudeNumber, key.longitudeNumber, key.customerID,
                attributes.distToLivArea, attributes.distToHome, attributes.distToWork);
    }
}
