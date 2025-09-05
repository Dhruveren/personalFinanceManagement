package com.acme.pfm.analytics;

import com.acme.pfm.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MonthlyAggregator {
    public static Map<String, BigDecimal> spendByCategoryParallel(List<Transaction> txs) {
        ConcurrentHashMap<String, BigDecimal> sum = new ConcurrentHashMap<>();
        txs.parallelStream()
                .filter(t -> t.getAmount().signum() < 0)
                .forEach(t -> sum.merge(
                        t.getCategory() == null ? "Uncategorized" : t.getCategory(),
                        t.getAmount().abs(),
                        BigDecimal::add));
        return sum;
    }
}
