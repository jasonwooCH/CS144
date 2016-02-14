CREATE TABLE ItemPoint (KEY(ItemID)) ENGINE=MyISAM
	SELECT ItemID, Latitude, Longitude
	FROM ItemLocation
	WHERE Latitude IS NOT NULL AND Longitude IS NOT NULL;

ALTER TABLE ItemPoint ADD Coords POINT;

UPDATE ItemPoint
SET    Coords = POINT(Latitude, Longitude);

ALTER TABLE ItemPoint MODIFY Coords POINT NOT NULL;

CREATE SPATIAL INDEX sp_index ON ItemPoint (Coords);