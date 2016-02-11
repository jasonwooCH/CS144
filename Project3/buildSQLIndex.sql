ALTER TABLE ItemLocation ADD Coords POINT;

UPDATE ItemLocation
SET    Coords = POINT(Latitude, Longitude);

ALTER TABLE ItemLocation MODIFY Coords NOT NULL;

CREATE SPATIAL INDEX sp_index ON ItemLocation (Coords);