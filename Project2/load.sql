LOAD DATA LOCAL INFILE 'Item.dat' INTO TABLE Item FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';

LOAD DATA LOCAL INFILE 'ItemCategory.dat' INTO TABLE ItemCategory FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';

LOAD DATA LOCAL INFILE 'ItemLocation.dat' INTO TABLE ItemLocation FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';

LOAD DATA LOCAL INFILE 'EbayUser.dat' INTO TABLE EbayUser FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';

LOAD DATA LOCAL INFILE 'Bidder.dat' INTO TABLE Bidder FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';

LOAD DATA LOCAL INFILE 'Bids.dat' INTO TABLE Bids FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';

LOAD DATA LOCAL INFILE 'Seller.dat' INTO TABLE Seller FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';

LOAD DATA LOCAL INFILE 'Auction.dat' INTO TABLE Auction FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';