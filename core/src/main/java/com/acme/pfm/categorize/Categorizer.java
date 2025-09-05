package com.acme.pfm.categorize;

import com.acme.pfm.Transaction;

import java.math.BigDecimal;

public class Categorizer {
    private final Ruleset ruleset;

    public Categorizer(Ruleset ruleset) {
        this.ruleset = ruleset;
    }

    public String categorize(Transaction t) {
        String desc = t.getDescription() == null ? "" : t.getDescription().toLowerCase();
        BigDecimal amt = t.getAmount();
        int sign = amt == null ? 0 : amt.signum();

        // 1) Rules
        if (ruleset != null && ruleset.rules != null) {
            for (Rule r : ruleset.rules) {
                if (r.descPattern != null && !r.descPattern.matcher(desc).find()) continue;
                if (r.min_amount != null && amt != null && amt.compareTo(r.min_amount) < 0) continue;
                if (r.max_amount != null && amt != null && amt.compareTo(r.max_amount) > 0) continue;
                if (r.direction != null) {
                    if ("debit".equalsIgnoreCase(r.direction) && sign > 0) continue;
                    if ("credit".equalsIgnoreCase(r.direction) && sign < 0) continue;
                }
                return r.category != null ? r.category : "Uncategorized";
            }
        }

        // 2) Heuristics
        if (desc.contains("uber") || desc.contains("ola") || desc.contains("cab")) return "Transport";
        if (desc.contains("groc") || desc.contains("mart") || desc.contains("bazaar")) return "Food";
        if (desc.contains("coffee") || desc.contains("cafe") || desc.contains("starbucks") || desc.contains("ccd")) return "Food";
        if (desc.contains("electricity") || desc.contains("power")) return "Bills";
        if (desc.contains("salary") || desc.contains("payroll")) return "Income";

        // 3) Default
        return "Uncategorized";
    }
}
