package com.acme.pfm;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class TransactionMapper {
    public TransactionMapper() {
    }

    public static Transaction from(CsvTxnRow r, DateTimeFormatter fmt) {
        if (r == null) throw new IllegalArgumentException("row is null");
        if (fmt == null) throw new IllegalArgumentException("formatter is null");
        String dateStr = r.getDate();
        if (dateStr == null || dateStr.isBlank()) throw new IllegalArgumentException("date is empty");
        String amountStr = r.getAmount();
        if (amountStr == null || amountStr.isBlank()) throw new IllegalArgumentException("amount is empty");
        LocalDate d = LocalDate.parse(dateStr.trim(), fmt);
        String cleaned = amountStr.trim()
                .replaceAll("[,\s]", "")
                .replace("₹", "")
                .replace("$", "")
                .replace("€", "");
// Support leading/trailing minus normalization if needed
        if (cleaned.endsWith("-")) cleaned = "-" + cleaned.substring(0, cleaned.length() - 1);
        BigDecimal amt = new BigDecimal(cleaned);
        return new Transaction(r.getId(), d, r.getDescription(), amt, r.getCategory());
    }
}

