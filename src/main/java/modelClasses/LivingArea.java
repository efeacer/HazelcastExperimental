package modelClasses;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A class that models the living area of the bank customers.
 * @author Efe Acer
 * @version 1.0
 */
public class LivingArea implements Serializable {

    // Inner classes to be able to separately store the key attributes and other attributes
    class Key implements Serializable { // Attributes composing the Key
        int customerID; // IP_ID
        int timeOfDay; // TM_GRP_FLG: Flag to separate a day into three groups
        int weekDay; // WK_DAY_FLG: Flag to distinguish between week days and weekends

        Key(int customerID, int timeOfDay, int weekDay) {
            this.customerID = customerID;
            this.timeOfDay = timeOfDay;
            this.weekDay = weekDay;
        }

        @Override
        public String toString() {
            return String.format("[KEY: (%d, %d, %d)]", customerID, timeOfDay, weekDay);
        }
    }
    class Attributes implements Serializable { // Other attributes
        LocalDate processDate; // PROCESS_DATE: Process date (mm-dd-yyyy but Java stores as yyyy-mm-dd)
        int partitionNumber; // PARTITION_NUM: Partition number
        double longitudeNumber; // LONGITUDE_NUM
        double latitudeNumber;  // LATITUDE_NUM

        Attributes(LocalDate processDate, int partitionNumber, double longitudeNumber, double latitudeNumber) {
            this.processDate = processDate;
            this.partitionNumber = partitionNumber;
            this.longitudeNumber = longitudeNumber;
            this.latitudeNumber = latitudeNumber;
        }

        @Override
        public String toString() {
            return String.format("[ATTRIBUTES: (%s, %d, %f, %f)]", processDate, partitionNumber,
                    longitudeNumber, latitudeNumber);
        }
    }

    // Properties
    private Key key;
    private Attributes attributes;

    /**
     * Constructor of the LivingArea class, which models bank customers' active areas.
     * @param processDate Process Date
     * @param partitionNumber Partition Number
     * @param customerID ID of the customer
     * @param timeOfDay Flag to separate a day into three groups (1, 2 or 3)
     * @param weekDay Flag to distinguish between week days and weekends
     * @param longitudeNumber Longitude number
     * @param latitudeNumber Latitude number
     */
    public LivingArea(LocalDate processDate, int partitionNumber, int customerID, int timeOfDay, int weekDay,
                      double longitudeNumber, double latitudeNumber) {
        key = new Key(customerID, timeOfDay, weekDay);
        attributes = new Attributes(processDate, partitionNumber, longitudeNumber, latitudeNumber);
    }

    /**
     * Alternative constructor for the LivingArea class.
     * @param key The key attributes of the LivingArea object
     * @param attributes The other attributes of the LivingArea object
     */
    public LivingArea(Key key, Attributes attributes) {
        this.key = key;
        this.attributes = attributes;
    }

    /**
     * Getter for the process date.
     * @return The process date
     */
    public LocalDate getProcessDate() { return attributes.processDate; }

    /**
     * Getter for the partition number.
     * @return The partition number
     */
    public int getPartitionNumber() { return attributes.partitionNumber; }

    /**
     * Getter for the customer's ID number.
     * @return Customer's ID number
     */
    public int getCustomerID() { return key.customerID; }

    /**
     * Getter for the flag that separates a day into three groups (1, 2, or 3).
     * @return The flag that separates a day into three groups (1, 2, or 3)
     */
    public int getTimeOfDay() { return key.timeOfDay; }

    /**
     * Getter for the flag that separates the week days and the weekends.
     * @return The flag that separates the week days and the weekends
     */
    public int getWeekDay() { return key.weekDay; }

    /**
     * Getter for the longitude number.
     * @return The longitude number
     */
    public double getLongitudeNumber() { return attributes.longitudeNumber; }

    /**
     * Getter for the latitude number.
     * @return The latitude number
     */
    public double getLatitudeNumber() { return attributes.latitudeNumber; }

    /**
     * Getter for the key attributes of the LivingArea object.
     * @return Key attributes of the LivingArea object
     */
    public Key getKey() { return key; }

    /**
     * Getter for the attributes of the LivingArea object.
     * @return Attributes of the LivingArea object
     */
    public Attributes getAttributes() { return attributes; }

    /**
     * Returns the String representation of the LivingArea object.
     * @return The String representation of the living area object
     */
    @Override
    public String toString() {
        return "(" + attributes.processDate + ", " +
                attributes.partitionNumber + ", " +
                key.customerID + ", " +
                key.timeOfDay + ", " +
                key.weekDay + ", " +
                attributes.longitudeNumber + ", " +
                attributes.latitudeNumber + ")";
    }
}