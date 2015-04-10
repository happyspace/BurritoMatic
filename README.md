# BurritoMatic
A model burrito restaurant staffed by robots.

## Running BurritoMatic

1. Clone this repository.
2. from a console cd into repository directory.
3. run Maven install
4. run Maven exec:java

```
mvn clean install
mvn exec:java
```

You should see the following prompt.

```
Welcome to Burrito Matic!
To create a tasty burrito please follow the directions on the screen.

Please choose a burrito by entering the number preceding the burrito's name.
( 1 ) A-la-Carte Burrito
( 2 ) Burrito-in-a-bowl
( 3 ) Regular Burrito
( 4 ) Super Burrito

Burrito Matic>
```

## Additional Prompt Options
    
    inv - show the current inventory
    orders - show burrito orders
    restock - restock burrito ingredients
    exit - exit from Burrito Matic