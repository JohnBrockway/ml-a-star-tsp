# ml-a-star-tsp
A Java implementation of A* Search to solve Travelling Salesman problems. Takes an argument specifying a file path to the input problem, in the following form:
```
# of Cities
CityName x-coordinate y-coordinate
CityName2 x-coordinate y-coordinate
etc...
```
See the provided [example.tsp](example.tsp) for a more concrete example.

To run, execute the following commands in the src directory:
```
javac TravellingSalesman.java City.java State.java
java TravellingSalesman ../example.tsp
```

See [Wikipedia](https://en.wikipedia.org/wiki/A*_search_algorithm) for more information on A* Search.
