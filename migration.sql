CREATE TABLE "rentals" (
	"id" BIGINT NOT NULL,
	"name" VARCHAR(255) NOT NULL,
	"surface" SMALLINT NOT NULL,
	"price" INTEGER NOT NULL,
	"pictures" JSON NOT NULL,
	"description" TEXT NOT NULL,
	"user_id" BIGINT NOT NULL,
	"created_at" TIMESTAMPTZ NOT NULL,
	"updated_at" TIMESTAMPTZ NOT NULL,
	PRIMARY KEY ("id"),
	KEY "idx_user_id" ("user_id"),
	CONSTRAINT "fk_rentals_users_id" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE RESTRICT ON DELETE RESTRICT
)
;
COMMENT ON COLUMN "rentals"."id" IS 'Unique non natural id';
COMMENT ON COLUMN "rentals"."name" IS 'Location name';
COMMENT ON COLUMN "rentals"."surface" IS 'Surface in sqrt meters';
COMMENT ON COLUMN "rentals"."price" IS 'Price in cents';
COMMENT ON COLUMN "rentals"."pictures" IS 'JSON Array of picture urls';
COMMENT ON COLUMN "rentals"."description" IS 'Description of the property to rent';
COMMENT ON COLUMN "rentals"."user_id" IS 'User foreign key';
COMMENT ON COLUMN "rentals"."created_at" IS 'Created at date';
COMMENT ON COLUMN "rentals"."updated_at" IS 'Update at date';

CREATE TABLE "users" (
	"id" BIGINT NOT NULL,
	"name" VARCHAR(255) NOT NULL,
	"email" VARCHAR(255) NOT NULL,
	"created_at" TIMESTAMPTZ NOT NULL,
	"updated_at" TIMESTAMPTZ NOT NULL,
	PRIMARY KEY ("id"),
	UNIQUE "email" ("email")
)
;
COMMENT ON COLUMN "users"."id" IS 'Identifiant non naturel unique';
COMMENT ON COLUMN "users"."name" IS 'Lastname and firstname';
COMMENT ON COLUMN "users"."email" IS 'Email used for authentification';
COMMENT ON COLUMN "users"."created_at" IS 'Created at date';
COMMENT ON COLUMN "users"."updated_at" IS 'Update at date';

CREATE TABLE "credentials" (
	"id" BIGINT NOT NULL,
	"password" VARCHAR(255) NOT NULL,
	"user_id" BIGINT NOT NULL,
	"created_at" TIMESTAMPTZ NOT NULL,
	"updated_at" TIMESTAMPTZ NOT NULL,
	PRIMARY KEY ("id"),
	UNIQUE "unique_user_id" ("user_id"),
	CONSTRAINT "fk_credentials_users_id" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON UPDATE RESTRICT ON DELETE CASCADE
)
;
COMMENT ON COLUMN "credentials"."id" IS 'Unique non natural id';
COMMENT ON COLUMN "credentials"."password" IS 'Hashed passwords';
COMMENT ON COLUMN "credentials"."user_id" IS 'User foreign key';
COMMENT ON COLUMN "credentials"."created_at" IS 'Created at date';
COMMENT ON COLUMN "credentials"."updated_at" IS 'Update at date';
