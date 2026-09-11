DROP TABLE IF EXISTS contract;
CREATE TABLE IF NOT EXISTS contract (
    updated TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    sale_order_number VARCHAR(255) NOT NULL UNIQUE,
    partner_id INT,
    partner_name VARCHAR(255),
    customer_company_id INT,
    customer_company_name VARCHAR(255),
    frame_number VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    net_value NUMERIC(12, 2),
    start_leasing DATE,
    end_leasing DATE,
    term INT,
    insurance_rate NUMERIC(12, 2),
    status VARCHAR(50) NOT NULL
    );