#!/bin/bash

mysql CS144 < drop.sql

mysql CS144 < create.sql

ant
ant run-all

sort -u Item.dat -o Item.dat
sort -u ItemCategory.dat -o ItemCategory.dat
sort -u ItemLocation.dat -o ItemLocation.dat
sort -u EbayUser.dat -o EbayUser.dat
sort -u Bidder.dat -o Bidder.dat
sort -u Bids.dat -o Bids.dat
sort -u Seller.dat -o Seller.dat
sort -u Auction.dat -o Auction.dat


mysql CS144 < load.sql


rm *dat
