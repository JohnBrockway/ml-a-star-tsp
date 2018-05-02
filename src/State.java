import java.util.ArrayList;

/**
 * A class to represent a state in the search tree, with a field for the path, as well as one for cities not yet on the tour
 * @author John Brockway
 */
public class State {

	/**
	 * The cities visited to the point, in an ordered list
	 */
	public ArrayList<City> citiesInOrder;
	/**
	 * The cities not yet visited in this path
	 */
	public ArrayList<City> citiesNotVisited;

	/**
	 * Constructor with already-visited cities specified
	 * @param citiesInOrder The cities visited to the point, in an ordered list
	 * @param cities The complete list of cities
	 */
	public State(ArrayList<City> citiesInOrder, City[] cities) {
		citiesNotVisited = new ArrayList<City>();
		this.citiesInOrder = citiesInOrder;

		// Based on what is missing from the path, construct the list of cities not yet in the path
		for (int i = 0 ; i < cities.length ; i++) {
			if (!citiesInOrder.contains((cities[i]))) {
				citiesNotVisited.add(cities[i]);
			}
		}

		// If, after constructing this list, all cities have been visited, add the start/destination city to the list (as long as it has also not been visited twice)
		if (citiesNotVisited.isEmpty() && !citiesInOrder.get(citiesInOrder.size()-1).name.equals(citiesInOrder.get(0).name)) {
			citiesNotVisited.add(citiesInOrder.get(0));
		}
	}
}