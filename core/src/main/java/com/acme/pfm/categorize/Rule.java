package com.acme.pfm.categorize;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class Rule {
    public String name;
    public String category;
    public String description_regex; // raw pattern from YAML
    public BigDecimal min_amount;
    public BigDecimal max_amount;
    public String direction; // "debit" | "credit" | null

    // compiled artifacts (not in YAML)
    public transient Pattern descPattern;
}
