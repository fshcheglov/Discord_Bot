CREATE TABLE IF NOT EXISTS species (
    id IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS leader (
    id IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS modifier_type (
    id IDENTITY NOT NULL PRIMARY KEY,
    type VARCHAR(256)
);

INSERT INTO modifier_type (type) VALUES ('Population');
INSERT INTO modifier_type (type) VALUES ('Resource Points');
INSERT INTO modifier_type (type) VALUES ('Manpower Points');
INSERT INTO modifier_type (type) VALUES ('Economic Points');


CREATE TABLE IF NOT EXISTS government (
    id IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(256)
);

INSERT INTO government (name) VALUES ('Oligarchic Democracy');
INSERT INTO government (name) VALUES ('Military Junta');
INSERT INTO government (name) VALUES ('Authoritarian Dictatorship');
INSERT INTO government (name) VALUES ('Technocracy');
INSERT INTO government (name) VALUES ('Federation');
INSERT INTO government (name) VALUES ('Controlled Democracy');
INSERT INTO government (name) VALUES ('Absolute Monarchy');
INSERT INTO government (name) VALUES ('Constitutional Monarchy');
INSERT INTO government (name) VALUES ('Republic');
INSERT INTO government (name) VALUES ('Feudal Kingdom');

CREATE TABLE IF NOT EXISTS economy (
    id IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(256)
);
INSERT INTO economy (name) VALUES ('Free Market Capitalism');
INSERT INTO economy (name) VALUES ('Free Market Socialism');
INSERT INTO economy (name) VALUES ('Controlled Market');
INSERT INTO economy (name) VALUES ('Planned Economy');
INSERT INTO economy (name) VALUES ('Feudal Economy');

CREATE TABLE IF NOT EXISTS planet(
    id IDENTITY NOT NULL PRIMARY KEY,
    star int,
    owner int,
    name VARCHAR(256),
    type VARCHAR(256),
    resources int,
    population int,
    development int,
    planet_size int,
    is_habitable int
);

CREATE TABLE IF NOT EXISTS star_type(
    id IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(256),
    map_color int
);
INSERT INTO star_type (name, map_color) VALUES ('O-Class Blue Giant', 0x00FFFF);
INSERT INTO star_type (name, map_color) VALUES ('B-Class Blue Giant', 0x0000FF);
INSERT INTO star_type (name, map_color) VALUES ('A-Class White Star', 0xFFFFFF);
INSERT INTO star_type (name, map_color) VALUES ('F-Class Yellow-White Star', 0xFFFF00);
INSERT INTO star_type (name, map_color) VALUES ('K-Class Orange Dwarf', 0xFFBF00);
INSERT INTO star_type (name, map_color) VALUES ('M-Class Red Dwarf', 0xFF0000);

CREATE TABLE IF NOT EXISTS nation (
    id IDENTITY NOT NULL PRIMARY KEY,
    ownerID VARCHAR(256),
    name VARCHAR(256),
    leader int,
    government int,
    economic_type int,
    primary_species int,
    resource_points int,
    economic_points int,
    manpower_points int,
    stability double,
    centralization double,
    approval double,
    population int,
    capital int,

    FOREIGN KEY (leader) REFERENCES leader(id),
    FOREIGN KEY (government) REFERENCES government(id),
    FOREIGN KEY (economic_type) REFERENCES economy(id),
    FOREIGN KEY (primary_species) REFERENCES species(id),
    FOREIGN KEY (capital) REFERENCES planet(id)
);

CREATE TABLE IF NOT EXISTS modifier (
    id IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(256),
    is_flat int,
    type int,
    modifier_value double,
    duration int,
    nation int,

    FOREIGN KEY (type) REFERENCES modifier_type(id),
    FOREIGN KEY (nation) REFERENCES nation(id)
);

CREATE TABLE IF NOT EXISTS system2(
    id IDENTITY NOT NULL PRIMARY KEY,
    owner int NULL,
    name VARCHAR(256),
    map_x int,
    map_y int,

    FOREIGN KEY (owner) REFERENCES nation(id)
);

CREATE TABLE IF NOT EXISTS star(
    id IDENTITY NOT NULL PRIMARY KEY,
    system int,
    name VARCHAR(256),
    type int,
    resources int,

    FOREIGN KEY (type) REFERENCES star_type(id),
    FOREIGN KEY (system) REFERENCES system2(id)
);

ALTER TABLE planet ADD FOREIGN KEY (owner) REFERENCES nation(id);
ALTER TABLE planet  ADD FOREIGN KEY (star) REFERENCES star(id);