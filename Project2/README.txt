Part B

1. List relations

From constructing an ER model, my relations are

Item(ItemID, Name, Description, First_Bid, Started, Ends, Number_of_Bids, Currently, Country, Buy_Price[null])
	key - Item ID

ItemCategory(ItemID, Category) 
	key - both
	Foreign key - Item(ItemID)

ItemLocation(ItemID, LocationName, Latitude[null], Longitude[null])
	key - ItemID
	Foreign key - Item(ItemID)

EbayUser(UserID)
	key - UserID

Bidder(UserID, Rating, Location[null], Country[null])
	key - UserID
	Foreign key - EbayUser(UserID)
	
Bids(ItemID, UserID, Amount, Time)
	key - ItemID, UserID
	Foreign key - Item(ItemID), Bidder(UserID)

Seller(UserID, Rating)
	key - UserID
	Foreign key - User(UserID)

Auction(ItemID, UserID)
	key - both
	Foreign key - Item(ItemID), Seller(UserID)

2. Nontrivial FDs

Excluding the keys, there are no functional dependancies in the stated relations.

