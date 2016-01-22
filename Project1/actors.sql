/* 1. */
CREATE TABLE Actors (
	Name VARCHAR(40),
	Movie VARCHAR(80),
	Year INTEGER,
	Role VARCHAR(40)
);

/* 2. */
LOAD DATA LOCAL INFILE '~/data/actors.csv' INTO TABLE Actors FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';

/* 3. */
SELECT Name
FROM Actors
WHERE Movie='Die Another Day';

/* 4 */
DROP TABLE Actors;