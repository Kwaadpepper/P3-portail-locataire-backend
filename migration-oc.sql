CREATE SEQUENCE "chatop-oc"."users_id_seq"
   START WITH 1
   INCREMENT BY 1
   MINVALUE 1
   MAXVALUE 9223372036854775807
   CACHE 1;

CREATE TABLE "chatop-oc"."users"(
   "id" bigint DEFAULT nextval('"chatop-oc".users_id_seq'::regclass) NOT NULL,
   "email" character varying(255) NOT NULL,
   "name" character varying(255) NOT NULL,
   "password" character varying(255) NOT NULL,
   "created_at" timestamp with time zone NOT NULL,
   "updated_at" timestamp with time zone NOT NULL
);

CREATE UNIQUE INDEX users_pkey ON "chatop-oc".users USING btree (id);
CREATE UNIQUE INDEX users_email_unique ON "chatop-oc".users USING btree (email);

COMMENT ON COLUMN "chatop-oc"."users"."id" IS 'Identifiant non naturel unique';
COMMENT ON COLUMN "chatop-oc"."users"."name" IS 'Lastname and firstname';
COMMENT ON COLUMN "chatop-oc"."users"."email" IS 'Email used for authentification';
COMMENT ON COLUMN "chatop-oc"."users"."created_at" IS 'Created at date';
COMMENT ON COLUMN "chatop-oc"."users"."updated_at" IS 'Update at date';

CREATE SEQUENCE "chatop-oc"."rentals_id_seq"
   START WITH 1
   INCREMENT BY 1
   MINVALUE 1
   MAXVALUE 9223372036854775807
   CACHE 1;

CREATE TABLE "chatop-oc"."rentals"(
   "id" bigint DEFAULT nextval('"chatop-oc".rentals_id_seq'::regclass) NOT NULL,
   "name" character varying(255) NOT NULL,
   "surface" numeric NOT NULL,
   "price" numeric NOT NULL,
   "picture" varchar[] NOT NULL,
   "description" character varying(2000) NOT NULL,
   "owner_id" bigint NOT NULL,
   "created_at" timestamp with time zone NOT NULL,
   "updated_at" timestamp with time zone NOT NULL
);

CREATE UNIQUE INDEX rentals_pkey ON "chatop-oc".rentals USING btree (id);
CREATE INDEX rentals_owner_id ON "chatop-oc".rentals USING btree (owner_id);

COMMENT ON COLUMN "chatop-oc"."rentals"."id" IS 'Unique non natural id';
COMMENT ON COLUMN "chatop-oc"."rentals"."name" IS 'Location name';
COMMENT ON COLUMN "chatop-oc"."rentals"."surface" IS 'Surface in sqrt meters';
COMMENT ON COLUMN "chatop-oc"."rentals"."price" IS 'Price in cents';
COMMENT ON COLUMN "chatop-oc"."rentals"."picture" IS 'JSON Array of pictures urls';
COMMENT ON COLUMN "chatop-oc"."rentals"."description" IS 'Description of the property to rent';
COMMENT ON COLUMN "chatop-oc"."rentals"."owner_id" IS 'User foreign key';
COMMENT ON COLUMN "chatop-oc"."rentals"."created_at" IS 'Created at date';
COMMENT ON COLUMN "chatop-oc"."rentals"."updated_at" IS 'Update at date';

CREATE SEQUENCE "chatop-oc"."messages_id_seq"
   START WITH 1
   INCREMENT BY 1
   MINVALUE 1
   MAXVALUE 9223372036854775807
   CACHE 1;

CREATE TABLE "chatop-oc"."messages"(
   "id" bigint DEFAULT nextval('"chatop-oc".messages_id_seq'::regclass) NOT NULL,
   "rental_id" bigint NOT NULL,
   "user_id" bigint NOT NULL,
   "message" character varying(2000) NOT NULL,
   "created_at" timestamp with time zone NOT NULL,
   "updated_at" timestamp with time zone NOT NULL
);

CREATE UNIQUE INDEX messages_pkey ON "chatop-oc".messages USING btree (id);
CREATE INDEX rentals_rental_id ON "chatop-oc".messages USING btree (rental_id);
CREATE INDEX rentals_user_id ON "chatop-oc".messages USING btree (user_id);

COMMENT ON COLUMN "chatop-oc"."messages"."id" IS 'Unique non natural id';
COMMENT ON COLUMN "chatop-oc"."messages"."rental_id" IS 'Rental foreign key';
COMMENT ON COLUMN "chatop-oc"."messages"."user_id" IS 'User foreign key';
COMMENT ON COLUMN "chatop-oc"."messages"."message" IS 'A message to be read for a user';
COMMENT ON COLUMN "chatop-oc"."messages"."created_at" IS 'Created at date';
COMMENT ON COLUMN "chatop-oc"."messages"."updated_at" IS 'Update at date';
