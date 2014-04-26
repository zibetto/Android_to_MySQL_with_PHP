#!/bin/bash
: '
This script will add a thousand users to the table login like so:
user1 pass1
...
user1000 pass1000

To run it, just execute:

./scriptInsertInto

remember:
myapp = your data base name
login = your table name

'

for i in {1..1000}
do
   echo "insert into login (username,passwd) values (\"user$i\",md5(\"pass$i\"));"
done | mysql -h localhost -u root -p myapp;



