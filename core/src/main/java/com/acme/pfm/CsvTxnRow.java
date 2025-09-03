package com.acme.pfm;

import com.opencsv.bean.CsvBindByName;

import java.nio.file.Files;
import java.util.List;

public class CsvTxnRow {
    @CsvBindByName(column = "Id")
    private String id;
    @CsvBindByName(column = "Date")
    private String date;
    @CsvBindByName(column = "Description")
    private String description;
    @CsvBindByName(column = "Amount")
    private String amount;
    @CsvBindByName(column = "Category")
    private String category;

    public CsvTxnRow() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}

