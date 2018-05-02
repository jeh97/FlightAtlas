CREATE TABLE IF NOT EXISTS "airlines" (
  "_id" integer PRIMARY KEY AUTOINCREMENT NOT NULL,
  "name" text NOT NULL,
  "alias" text(128),
  "IATA" text(128) PRIMARY KEY,
  "ICAO" text(128),
  "callsign" text(128),
  "country" text(128)
);

CREATE TABLE IF NOT EXISTS "airports" (
  "_id" integer PRIMARY KEY AUTOINCREMENT NOT NULL,
  "name" text NOT NULL,
  "city" text(128),
  "country" text(128),
  "IATA" text(128) PRIMARY KEY,
  "ICAO" text(128),
  "latitude" double(128),
  "longitude" double(128),
  "altitude" double(128),
  "timezone" double(128),
  "DST" text(128),
  "tz" text(128),
  FOREIGN KEY ("city","country") REFERENCES metros("city","country") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "metros" (
  "_id" integer PRIMARY KEY AUTOINCREMENT NOT NULL,
  "city" text NOT NULL,
  "country" text(128),
  "timezone" double(128),
  "DST" text(128),
  "tz" text(128),
  "latitude" double(128),
  "longitude" double(128),
  PRIMARY KEY("city", "country")
);

CREATE TABLE IF NOT EXISTS "routes" (
  "_id" integer PRIMARY KEY AUTOINCREMENT NOT NULL,
  "origin" text(128),
  "destination" text(128),
  "operator" text(128),
  FOREIGN KEY("origin") REFERENCES airports("IATA") ON DELETE CASCADE,
  FOREIGN KEY("destination") REFERENCES airports("IATA") ON DELETE CASCADE,
  FOREIGN KEY("operator") REFERENCES airlines("IATA") ON DELETE CASCADE
);

