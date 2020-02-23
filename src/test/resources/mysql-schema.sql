use test_db;

CREATE TABLE IF NOT EXISTS `account` (
    `id` binary(16) NOT NULL,
    `email` varchar(256) NOT NULL UNIQUE,
    `password` varchar(512) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `category` (
    `id` binary(16) NOT NULL,
    `name` varchar(512) NOT NULL UNIQUE,
    `icon_code` int(10) DEFAULT NULL,
    `account_id` binary(16) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `label` (
    `id` binary(16) NOT NULL,
    `name` varchar(512) NOT NULL UNIQUE,
    `account_id` binary(16) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `payment` (
    `id` binary(16) NOT NULL,
    `amount` decimal(16,9) NOT NULL,
    `description` varchar(512) DEFAULT NULL,
    `date` datetime NOT NULL,
    `account_id` binary(16) NOT NULL,
    `category_id` binary(16) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `account_id` (`account_id`),
    KEY `category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `payment_labels` (
    `account_id` binary(16) NOT NULL,
    `category_id` binary(16) NOT NULL,
    KEY `account_id` (`account_id`),
    KEY `category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

ALTER TABLE `category`
	ADD CONSTRAINT `category_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE;

ALTER TABLE `label`
	ADD CONSTRAINT `label_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE;

ALTER TABLE `payment`
	ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE,
	ADD CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE;

ALTER TABLE `payment_labels`
	ADD CONSTRAINT `payment_labels_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE,
	ADD CONSTRAINT `payment_labels_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE;