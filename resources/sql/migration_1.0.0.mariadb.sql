-- CREATE SCHEMA
CREATE SCHEMA IF NOT EXISTS `chatop-oc`;


-- REMOVE TABLES
DROP TABLE IF EXISTS `chatop-oc`.`messages`;
DROP TABLE IF EXISTS `chatop-oc`.`rentals`;
DROP TABLE IF EXISTS `chatop-oc`.`users`;


-- USERS
CREATE TABLE `chatop-oc`.`users`(
   `id` bigint PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT 'Unique non natural id',
   `email` character varying(255) NOT NULL COMMENT 'Email used for authentification',
   `name` character varying(255) NOT NULL COMMENT 'Lastname and firstname',
   `password` character varying(255) NOT NULL,
   `api_token` UUID NOT NULL,
   `created_at` datetime NOT NULL COMMENT 'Created at date',
   `updated_at` datetime NOT NULL COMMENT 'Update at date'
);

CREATE UNIQUE INDEX users_email_unique ON `chatop-oc`.`users` (`email`);


-- RENTALS
CREATE TABLE `chatop-oc`.`rentals`(
   `id` bigint PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT 'Unique non natural id',
   `name` character varying(255) NOT NULL COMMENT 'Title describing the rental',
   `surface` integer NOT NULL COMMENT 'Available surface in square meters',
   `price` integer NOT NULL COMMENT 'Price in euros cents',
   `pictures` json NOT NULL COMMENT 'Array of pictures urls',
   `description` character varying(2000) NOT NULL COMMENT 'Plain text description for the rental',
   `owner_id` bigint NOT NULL COMMENT 'User id FK : the owner of the rental',
   `created_at` datetime NOT NULL COMMENT 'Created at date',
   `updated_at` datetime NOT NULL COMMENT 'Update at date'
);

CREATE INDEX rentals_owner_id ON `chatop-oc`.`rentals`(`owner_id`);


-- MESSAGES
CREATE TABLE `chatop-oc`.`messages`(
   `id` bigint PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT 'Unique non natural id',
   `text_message` character varying(2000) NOT NULL COMMENT 'Plain text message content',
   `rental_id` bigint NOT NULL COMMENT 'Rental id FK',
   `user_id` bigint NOT NULL COMMENT 'User id FK',
   `created_at` datetime NOT NULL COMMENT 'Created at date',
   `updated_at` datetime NOT NULL COMMENT 'Update at date'
);

CREATE INDEX messages_rental_id ON `chatop-oc`.`messages` (`rental_id`);
CREATE INDEX messages_user_id ON `chatop-oc`.`messages` (`user_id`);


-- FOREIGN KEYS
ALTER TABLE IF EXISTS `chatop-oc`.`messages` DROP CONSTRAINT IF EXISTS `messages_rentals_rental_id`;
ALTER TABLE IF EXISTS `chatop-oc`.`messages` ADD CONSTRAINT `messages_rentals_rental_id` FOREIGN KEY(`rental_id`) REFERENCES `chatop-oc`.`rentals`(`id`);
ALTER TABLE IF EXISTS `chatop-oc`.`messages` DROP CONSTRAINT IF EXISTS `messages_users_user_id`;
ALTER TABLE IF EXISTS `chatop-oc`.`messages` ADD CONSTRAINT `messages_users_user_id` FOREIGN KEY(`user_id`) REFERENCES `chatop-oc`.`users`(`id`);
ALTER TABLE IF EXISTS `chatop-oc`.`rentals` DROP CONSTRAINT IF EXISTS `rentals_users_owner_id`;
ALTER TABLE IF EXISTS `chatop-oc`.`rentals` ADD CONSTRAINT `rentals_users_owner_id` FOREIGN KEY(`owner_id`) REFERENCES `chatop-oc`.`users`(`id`);

