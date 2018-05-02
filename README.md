# ml-a-star-tsp
A Java implementation of A* Search to solve Travelling Salesman-esque problems. Takes an argument specifying a file path to the input problem, in the following form:
```
# of Cities
CityName x-coordinate y-coordinate
CityName2 x-coordinate y-coordinate
etc...
```
See the provided [example.tsp](example.tsp) for a more concrete example.
The algorithm assumes that all Cities in the input have direct paths between them; that is, the input forms a complete graph. The solution is thus an optimal path that visits every City, ending at the same location at which it started.

To run, execute the following commands in the src directory:
```
javac TravellingSalesman.java City.java State.java
java TravellingSalesman ../example.tsp
```

See [Wikipedia](https://en.wikipedia.org/wiki/A*_search_algorithm) for more information on A* Search.
