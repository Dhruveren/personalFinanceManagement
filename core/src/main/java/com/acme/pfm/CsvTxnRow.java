package com.acme.pfm;

import com.opencsv.bean.CsvBindByName;

public class CsvTxnRow {
    @CsvBindByName(column = "Id") private String id;
    @CsvBindByName(column = "Date") private String date;
    @CsvBindByName(column = "Description") private String description;
    @CsvBindByName(column = "Amount") private String amount;
    @CsvBindByName(column = "Category") private String category;

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }
}
