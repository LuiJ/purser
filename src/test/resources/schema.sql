CREATE TABLE IF NOT EXISTS ACCOUNT (
    id BINARY(16) NOT NULL,
    email VARCHAR(256) NOT NULL,
    password VARCHAR(512) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS CATEGORY (
    id BINARY(16) NOT NULL,
    name VARCHAR(512) NOT NULL,
    icon_code INT(10),
    account_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    KEY account_id (account_id)
);

CREATE TABLE IF NOT EXISTS LABEL (
    id BINARY(16) NOT NULL,
    name VARCHAR(512) NOT NULL,
    account_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    KEY account_id (account_id)
);

CREATE TABLE IF NOT EXISTS PAYMENT (
    id BINARY(16) NOT NULL,
    amount decimal(16,9) NOT NULL,
    description VARCHAR(512),
    date datetime NOT NULL,
    account_id BINARY(16) NOT NULL,
    category_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    KEY account_id (account_id),
    KEY category_id (category_id)
);

CREATE TABLE IF NOT EXISTS PAYMENT_LABELS (
    account_id BINARY(16) NOT NULL,
    category_id BINARY(16) NOT NULL,
    KEY account_id (account_id),
    KEY category_id (category_id)
);

ALTER TABLE CATEGORY
	ADD CONSTRAINT category_ibfk_1 FOREIGN KEY (account_id) REFERENCES ACCOUNT (id) ON DELETE CASCADE;

ALTER TABLE LABEL
	ADD CONSTRAINT label_ibfk_1 FOREIGN KEY (account_id) REFERENCES ACCOUNT (id) ON DELETE CASCADE;

ALTER TABLE PAYMENT
	ADD CONSTRAINT payment_ibfk_1 FOREIGN KEY (account_id) REFERENCES ACCOUNT (id) ON DELETE CASCADE,
	ADD CONSTRAINT payment_ibfk_2 FOREIGN KEY (category_id) REFERENCES CATEGORY (id) ON DELETE CASCADE;

ALTER TABLE PAYMENT_LABELS
	ADD CONSTRAINT payment_labels_ibfk_1 FOREIGN KEY (account_id) REFERENCES ACCOUNT (id) ON DELETE CASCADE,
	ADD CONSTRAINT payment_labels_ibfk_2 FOREIGN KEY (category_id) REFERENCES CATEGORY (id) ON DELETE CASCADE;