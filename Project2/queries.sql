-- 1
SELECT COUNT(*)
FROM EbayUser;

-- 2
SELECT COUNT(*)
FROM ItemLocation
WHERE BINARY Location='New York';

-- 3
SELECT COUNT(*)
FROM
(
	SELECT ItemID
	FROM ItemCategory
	GROUP BY ItemID
	HAVING COUNT(Category) = 4
) S;

-- 4
SELECT ItemID
FROM Item
WHERE Ends >='2001-12-20 00:00:01' AND Number_of_Bids > 0
      AND Currently >= ALL (SELECT Currently
     	  	    FROM Item
		    WHERE Ends >='2001-12-20 00:00:01' AND Number_of_Bids > 0);

-- 5
SELECT COUNT(*)
FROM Seller
WHERE Rating > 1000;

-- 6
SELECT COUNT(*)
FROM Seller S, Bidder B
WHERE S.UserID = B.UserID;

-- 7
SELECT COUNT(*)
FROM
(
	SELECT DISTINCT(Category) as Category
	FROM ItemCategory IC, Bids B
	WHERE IC.ItemID = B.ItemID AND B.Amount > 100
) S;

