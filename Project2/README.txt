Part B

1. List relations

From constructing an ER model, my relations are

Item(ItemID, Name, Description, First_Bid, Started, Ends, Number_of_Bids, Currently, Country, Buy_Price[null])
	key - Item ID

ItemCategory(ItemID, Category) 
	key - both

ItemLocation(ItemID, Location, Latitude[null], Longitude[null])
	key - ItemID

Bidder(UserID, Rating, Location[null], Country[null])
	key - UserID
Bids(ItemID, UserID, Amount, Time)
	key - ItemID, UserID

Seller(UserID, Rating)
	key - UserID
Auction(ItemID, UserID)


2. Nontrivial FDs

Excluding the keys, there are no functional dependancies in the stated relations.

