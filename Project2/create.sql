CREATE TABLE Item (
       ItemID INT NOT NULL,
       Name VARCHAR(40) NOT NULL,
       Description VARCHAR(4000),
       First_Bid DECIMAL(8, 2) NOT NULL,
       Started TIMESTAMP NOT NULL,
       Ends TIMESTAMP NOT NULL,
       Number_of_Bids INT NOT NULL,
       Currently DECIMAL(8, 2) NOT NULL,
       Country VARCHAR(20) NOT NULL,
       Buy_Price DECIMAL(8, 2),

       PRIMARY KEY(ItemID),
       CHECK (ItemID >= 0 AND
       	      Ends > Started AND
	      First_Mid >= 0 AND
	      Currently >= 0)
);

CREATE TABLE ItemCategory(
       ItemID INT NOT NULL,
       Category VARCHAR(40) NOT NULL,

       FOREIGN KEY(ItemID) REFERENCES Item(ItemID),
       PRIMARY KEY(ItemID, Category)
);

CREATE TABLE ItemLocation(
       ItemID INT NOT NULL,
       Location VARCHAR(80) NOT NULL,
       Latitude DECIMAL(6, 6),
       Longitude DECIMAL(6, 6),

       FOREIGN KEY(ItemID) REFERENCES Item(ItemID)
);

CREATE TABLE Bidder(
       UserID VARCHAR(20) NOT NULL,
       Rating INT NOT NULL,
       Location VARCHAR(80),
       Country VARCHAR(40),

       PRIMARY KEY(UserID),

       CHECK (Rating >= 0)
);

CREATE TABLE Bids(
       ItemID INT NOT NULL,
       UserID VARCHAR(20) NOT NULL,
       Amount DECIMAL(8, 2) NOT NULL,
       BidTime TIMESTAMP NOT NULL,

       FOREIGN KEY(ItemID) REFERENCES Item(ItemID),
       FOREIGN KEY(UserID) REFERENCES Bidder(UserID),
       PRIMARY KEY(ItemID, UserID, Amount, BidTime),

       CHECK (Amount >= 0)
);

CREATE TABLE Seller(
       UserID VARCHAR(20) NOT NULL,
       Rating INT NOT NULL,

       PRIMARY KEY(UserID),

       CHECK (Rating >= 0)
);

CREATE TABLE Auction(
       ItemID INT NOT NULL,
       UserID VARCHAR(20) NOT NULL,

       FOREIGN KEY(ItemID) REFERENCES Item(ItemID),
       FOREIGN KEY(UserID) REFERENCES Seller(UserID),
       PRIMARY KEY(ItemID)
);
