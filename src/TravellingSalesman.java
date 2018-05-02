import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a solution to the travelling salesman problem using A* Search
 * @author John Brockway
 */
public class TravellingSalesman {

	/**
	 * The main function which converts the input into a form usable by the search method
	 * @param args Contains the file path of the problem instance, which is in a specified form of "#-of-cities\nName x-Coordinate yCoordinate\nName x-Coordinate yCoordinate etc."
	 */
	public static void main(String[] args) {
		City[] cities = new City[0];
		try {
			BufferedReader input = new BufferedReader(new FileReader(args[0]));
			int numberOfCities = Integer.parseInt(input.readLine());
			cities = new City[numberOfCities];
			for (int i = 0 ; i < numberOfCities ; i++) {
				String line = input.readLine();
				String[] lineSplit = line.split(" ");
				cities[i] = new City(lineSplit[0], Double.parseDouble(lineSplit[1]), Double.parseDouble(lineSplit[2]));
			}
			input.close();
		} catch (IOException io) {
			io.printStackTrace();
		}

		State result = aStarSearch(cities);

		// Output formatted path
		for (int i = 0 ; i < result.citiesInOrder.size() ; i++) {
			System.out.print(result.citiesInOrder.get(i).name);
			if (i != result.citiesInOrder.size() - 1) {
				System.out.print(":");
			}
		}
	}

	/**
	 * The generalized implementation of A* Search
	 * @param cities A list of City that represents every single city in the problem
	 * @return A complete State, with all nodes included in the path, and the path being optimal
	 */
	public static State aStarSearch(City[] cities)
	{
		ArrayList<City> start = new ArrayList<City>();
		start.add(cities[0]);
		State startState = new State(start, cities);

		ArrayList<State> openStates = new ArrayList<State>();
		openStates.add(startState);

		// The list of cities' g values; the start state has a g value of 0
		HashMap<State, Double> gValues = new HashMap<State, Double>();
		gValues.put(startState, 0.0);

		// The list of cities' f values (g value + heuristic); the start state has an f value of simply the heuristic
		HashMap<State, Double> fValues = new HashMap<State, Double>();
		fValues.put(startState, heuristic(cities[0], new ArrayList<City>(), startState.citiesNotVisited));

		while(!openStates.isEmpty()) {

			// Loop over all states that have been examined to find the one with the lowest f value
			double currentStateValue = fValues.get(openStates.get(0));
			State currentState = openStates.get(0);
			for (int i = 0 ; i < openStates.size() ; i++) {
				if(fValues.get(openStates.get(i)) < currentStateValue)
				{
					currentState = openStates.get(i);
					currentStateValue = fValues.get(openStates.get(i));
				}
			}

			// If this state is the goal (all cities visited, including returning to the starting node) return the success
			if (currentState.citiesNotVisited.isEmpty()) {
				return currentState;
			}

			// Remove this state from the open states
			openStates.remove(currentState);

			// Get the neighbouring states of the chosen states as well as their distances from the current state, and loop through them
			LinkedHashMap<State, Double> neighbours = getNeighbours(currentState, cities);
			for (Map.Entry<State, Double> entry : neighbours.entrySet()) {

				// If the state is new, add it to the list of discovered states
				if (!openStates.contains(entry.getKey())) {
					openStates.add(entry.getKey());
				}

				// Calculate the g value of this new state
				double gValue = gValues.get(currentState) + entry.getValue();

				// If this g value is the optimal g value for this state, update the corresponding g and f values for the state
				if (gValues.get(entry.getKey()) ==  null || gValue < gValues.get(entry.getKey())) {
					gValues.put(entry.getKey(), gValue);
					fValues.put(entry.getKey(), gValue + heuristic(cities[0], new ArrayList<City>(), entry.getKey().citiesNotVisited));
				}
			}
		}

		// Should never reach this point assuming there is a solution, which there will be to a complete graph
		return new State(null, null);
	}

	/**
	 * Recursively calculates the heuristic for A*; in this case, finds the minimum spanning tree of the unvisited cities
	 * @param startCity Where the tour started
	 * @param tree The incomplete, currently constructed tree of unvisited cities (empty before first iteration)
	 * @param unvisitedCities The list of cities not visited and not yet in the tree
	 * @return The cost of the minimum spanning tree across unvisited elements of a particular state
	 */
	public static double heuristic(City startCity, ArrayList<City> tree, ArrayList<City> unvisitedCities) {
		if (unvisitedCities.isEmpty()) {
			// If this is a goal state, the heuristic is 0 by definition
			return 0;
		} else {
			// Have to do a deep copy of the current state to make sure there are no unintended side effects of changing the path
			ArrayList<City> workableUnvisited = new ArrayList<City>();
			for (int j = 0 ; j < unvisitedCities.size() ; j++) {
				City nextElement = unvisitedCities.get(j);
				workableUnvisited.add(new City(nextElement.name, nextElement.x, nextElement.y));
			}

			// Since we're not piping through state, we have to manually add the start point to be the end point
			if (!workableUnvisited.contains(startCity) && !tree.contains(startCity)) {
				workableUnvisited.add(startCity);
			}

			if (tree.isEmpty()) {
				// If this is our first pass, start the tree with an arbitrary element
				tree.add(workableUnvisited.get(0));
				workableUnvisited.remove(unvisitedCities.get(0));
				return heuristic(startCity, tree, workableUnvisited);
			} else {
				// Otherwise, we will calculate the cost of a minimum spanning tree spanning all unvisited nodes and back to the start point
				// Kruskal's algorithm (but slower [but fast enough for the sets in the problem]); find least-cost edge along the cut, and insert it to tree
				double closestDistance = getEuclideanDistance(tree.get(0), workableUnvisited.get(0));
				City closestCity = workableUnvisited.get(0);
				for (int i = 0 ; i < tree.size() ; i++) {
					for (int j = 0 ; j < workableUnvisited.size() ; j++) {
						if (getEuclideanDistance(tree.get(i), workableUnvisited.get(j)) < closestDistance) {
							closestDistance = getEuclideanDistance(tree.get(i), workableUnvisited.get(j));
							closestCity = workableUnvisited.get(j);
						}
					}
				}

				tree.add(closestCity);
				workableUnvisited.remove(closestCity);

				return closestDistance + heuristic(startCity, tree, workableUnvisited);
			}
		}
	}

	/**
	 * A function to get the neighbours of any node in the search tree as well as their respective distances from the current state
	 * @param state The current state of the search
	 * @param cities The complete list of cities in the problem
	 * @return A dictionary which includes the neighbouring states and their respective costs 
	 */
	public static LinkedHashMap<State, Double> getNeighbours(State state, City[] cities) {
		LinkedHashMap<State, Double> neighbours = new LinkedHashMap<State, Double>();
		City currentNode = state.citiesInOrder.get(state.citiesInOrder.size()-1);

		for (int i = 0 ; i < state.citiesNotVisited.size() ; i++) {
			City neighbour = state.citiesNotVisited.get(i);

			// Have to do a deep copy of the current state to make sure there are no unintended side effects of changing the path
			ArrayList<City> path = new ArrayList<City>();
			for (int j = 0 ; j < state.citiesInOrder.size() ; j++) {
				City nextElement = state.citiesInOrder.get(j);
				path.add(new City(nextElement.name, nextElement.x, nextElement.y));
			}
			path.add(neighbour);

			State newState = new State(path, cities);

			// Add the newly formed state to the list of neighbours, with the corresponding distance from the current path
			neighbours.put(newState, getEuclideanDistance(currentNode, neighbour));
		}

		return neighbours;
	}

	/**
	 * Gets the Euclidean distance between two points on a 2D plane represented by City objects
	 * @param a A City
	 * @param b A City
	 * @return The Euclidean distance between a and b
	 */
	public static double getEuclideanDistance(City a, City b) {
		return Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
	}
}