package com.acme.pfm.cli.services.dto;

public class TransactionDto {
    private String date;
    private double amount;
    private String description;
    private String category;
    private String type;

    // Constructors
    public TransactionDto() {}

    public TransactionDto(String date, double amount, String description,
                          String category, String type) {
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.type = type;
    }

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
