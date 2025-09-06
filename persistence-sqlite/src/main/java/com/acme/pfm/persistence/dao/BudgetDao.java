package com.acme.pfm.persistence.dao;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetDao {
    int upsert(String month, String category, BigDecimal amount);
    BigDecimal findAmount(String month, String category);
    List<Row> listByMonth(String month);
    int delete(String month, String category);

    record Row(String category, BigDecimal amount) {}
}
