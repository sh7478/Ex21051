/**
 * @author shaked hazan shaked1246@gmail.com
 * @version 1.0
 * @since 9/05/2026
 * Data model class representing an expense item.
 */
package com.example.ex21051;

public class Expense {
    private String name;
    private String description;
    private double amount;
    private String category;
    private String date;

    /**
     * Constructor for creating an Expense object with all fields.
     * <p>
     *
     * @param name The name of the expense.
     * @param description A short description of the expense.
     * @param amount The cost of the expense.
     * @param category The category the expense belongs to.
     * @param date The date the expense occurred.
     */
    public Expense(String name, String description, double amount, String category, String date)
    {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    /**
     * Default constructor for Firebase serialization.
     * <p>
     */
    public Expense()
    {}

    /**
     * Gets the name of the expense.
     * <p>
     *
     * @return The name string.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the expense.
     * <p>
     *
     * @param name The name string.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the expense.
     * <p>
     *
     * @return The description string.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the expense.
     * <p>
     *
     * @param description The description string.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the amount of the expense.
     * <p>
     *
     * @return The amount as a double.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the expense.
     * <p>
     *
     * @param amount The cost as a double.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the category of the expense.
     * <p>
     *
     * @return The category string.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the expense.
     * <p>
     *
     * @param category The category string.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the date of the expense.
     * <p>
     *
     * @return The date string.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the expense.
     * <p>
     *
     * @param date The date string.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Returns a string representation of the expense for the ListView.
     * <p>
     *
     * @return A formatted string with amount, name, and date.
     */
    @Override
    public String toString() {
        String[] parts = date.split("-");
        String displayDate = parts[2] + "/" + parts[1] + "/" + parts[0];
        return amount + "₪ | " + name + "\n" + displayDate;
    }

    /**
     * Checks if the expense date matches the specified month.
     * <p>
     *
     * @param month The month index (0-based) to match.
     * @return true if the month matches, false otherwise.
     */
    public boolean monthMatch(int month) {
        String[] parts = date.split("-");
        int m = Integer.parseInt(parts[1]);
        return m == month;
    }
}
