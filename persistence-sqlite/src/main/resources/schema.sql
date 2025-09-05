CREATE TABLE IF NOT EXISTS transactions (
    id TEXT PRIMARY KEY,
    date TEXT NOT NULL,
    description TEXT,
    amount TEXT NOT NULL,
    category TEXT
);