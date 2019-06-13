package modelClasses;

import java.io.Serializable;

/**
 * A sample modelClasses.Student class to be able to test Hazelcast using objects. The class
 * must implement Serializable interface to be storable/recoverable in/from
 * Hazelcast.
 * @author Efe Acer
 * @version 1.0
 */
public class Student implements Serializable {

    //Properties
    private String name;
    private String surname;
    private int age;
    private int id;
    private double gpa;
    private boolean graduated;

    /**
     * Constructor of the sample modelClasses.Student class.
     * @param name Name of the modelClasses.Student
     * @param surname Surname of the modelClasses.Student
     * @param age Age of the modelClasses.Student
     * @param id modelClasses.Student's ID number
     * @param gpa modelClasses.Student cumulative GPA
     */
    public Student(String name, String surname, int age, int id, double gpa, boolean graduated) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.id = id;
        this.gpa = gpa;
        this.graduated = graduated;
    }

    /**
     * Getter for the modelClasses.Student's name
     * @return Name of the modelClasses.Student
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the modelClasses.Student's surname.
     * @return Surname of the modelClasses.Student
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Getter for the modelClasses.Student's age.
     * @return Age of the modelClasses.Student
     */
    public int getAge() {
        return age;
    }

    /**
     * Getter for the modelClasses.Student's ID number.
     * @return modelClasses.Student's ID number
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the modelClasses.Student's cumulative GPA.
     * @return modelClasses.Student's cumulative GPA
     */
    public double getGpa() { return gpa; }

    /**
     * Getter for the modelClasses.Student's graduation status.
     * @return modelClasses.Student's graduation status
     */
    public boolean isGraduated() { return graduated; }

    /**
     * The overriden toString() method of the sample modelClasses.Student class to retrieve
     * the String representation.
     * @return The String representation of the sample modelClasses.Student object
     */
    @Override
    public String toString() {
        return System.lineSeparator() + "--- STUDENT ---" + System.lineSeparator() +
                "Name: " + name + System.lineSeparator() +
                "Surname: " + surname + System.lineSeparator() +
                "Age: " + age + System.lineSeparator() +
                "ID: " + id + System.lineSeparator() +
                "GPA: " + gpa + System.lineSeparator() +
                "Graduation Status: " + (graduated ? "Graduated" : "Studying") + System.lineSeparator();
    }
}