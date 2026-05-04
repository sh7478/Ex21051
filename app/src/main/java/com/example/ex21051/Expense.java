package com.example.ex21051;

public class Expense {
    private String name;
    private String description;
    private int amount;
    private String category;
    private String date;

    public Expense(String name, String description, int amount, String category, String date)
    {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public Expense()
    {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
