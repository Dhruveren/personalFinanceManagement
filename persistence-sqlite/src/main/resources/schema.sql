-- existing transactions table DDL…
CREATE TABLE IF NOT EXISTS transactions
(
    id          TEXT PRIMARY KEY,
    date        TEXT    NOT NULL,
    amount      NUMERIC NOT NULL,
    description TEXT,
    category    TEXT
);

-- now add the budgets table
CREATE TABLE IF NOT EXISTS budgets
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    month    TEXT    NOT NULL,
    category TEXT    NOT NULL,
    amount   NUMERIC NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_budgets_month_category
    ON budgets (month, category);
