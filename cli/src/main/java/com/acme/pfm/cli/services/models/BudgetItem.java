package com.acme.pfm.cli.services.models;

import java.math.BigDecimal;

public record BudgetItem(String category, BigDecimal amount) {}
