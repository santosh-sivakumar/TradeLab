import java.util.*;
import java.lang.Math;

public class Vector {
	
	List<Double> numbers = new ArrayList<Double>();
	String name;
	
	Vector (String inputName, List<Double> inputNumbers) {
	// public vector class - represents a given object within a cluster
		name = inputName;
		numbers = inputNumbers;
	}
		
	public double magnitude() { 
	// returns vector magnitude, calculated to be the square root of sum(coordinates^2)
		assert (numbers.size() > 0) : "Incompatible # dimensions";
		double magnitude = 0.0;

		for (int index = 0; index < numbers.size(); index++) {
			magnitude += (numbers.get(index) * (numbers.get(index)));
		}
			
		return Math.sqrt(magnitude);
	}

	public static double distance(Vector vector1, Vector vector2) throws RuntimeException { 
	// returns distance between two Vectors of equal num dimensions
		double squaredDistance = 0.0;

		if (vector1.numbers.size() != vector2.numbers.size()) {
			throw new RuntimeException();
		}
			
		for (int index = 0; index < vector1.numbers.size(); index++) {
			double difference = vector1.numbers.get(index) - vector2.numbers.get(index);
			difference = difference * difference;
			squaredDistance = squaredDistance + difference;
		}
			
		double distance = Math.sqrt(squaredDistance);	
		return distance;
	}
}

