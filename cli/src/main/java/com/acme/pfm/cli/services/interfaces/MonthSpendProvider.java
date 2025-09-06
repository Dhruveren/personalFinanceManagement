package com.acme.pfm.cli.services.interfaces;

import java.math.BigDecimal;
import java.util.Map;

public interface MonthSpendProvider {
    Map<String, BigDecimal> spendByCategory(String month);
}
