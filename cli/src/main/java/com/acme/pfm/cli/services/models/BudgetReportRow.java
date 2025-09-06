package com.acme.pfm.cli.services.models;

import java.math.BigDecimal;

public record BudgetReportRow(String category, BigDecimal budget, BigDecimal spend, BigDecimal variance) {
}
