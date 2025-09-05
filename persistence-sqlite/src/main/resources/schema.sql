CREATE TABLE IF NOT EXISTS transactions
(
    id          TEXT PRIMARY KEY,
    date        TEXT NOT NULL,
    description TEXT,
    amount      TEXT NOT NULL,
    category    TEXT
);

-- Budgets: per category per month (YYYY-MM)
CREATE TABLE IF NOT EXISTS budgets
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    month    TEXT    NOT NULL, -- 'YYYY-MM'
    category TEXT    NOT NULL,
    amount   NUMERIC NOT NULL  -- BigDecimal-compatible
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_budgets_month_category
    ON budgets (month, category);
