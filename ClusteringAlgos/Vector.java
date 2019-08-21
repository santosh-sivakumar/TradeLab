import java.util.ArrayList;
import java.lang.Math;

// public vector class - represents a given object within a cluster
public class Vector {
	
	ArrayList<Double> numbers = new ArrayList<Double>();
	String name;
	
	Vector (String inputName, ArrayList<Double> inputNumbers) {
		name = inputName;
		numbers = inputNumbers;
	}

	// returns vector magnitude, calculated to be the square root of sum(coordinates^2)
	public double magnitude() { 
		assert (numbers.size() > 0) : "Incompatible # dimensions";
		double magnitude = 0.0;

		for (int index = 0; index < numbers.size(); index++) {
			magnitude += (numbers.get(index) * (numbers.get(index)));
		}
			
		return Math.sqrt(magnitude);
	}
	
	// returns Euclidian distance between two Vectors of equal num dimensions
	public double distance(Vector vector1, Vector vector2){ 
		double squaredDistance = 0.0;

		assert (vector1.numbers.size() == vector2.numbers.size()): "Vectors not compatible";

		for (int index = 0; index < vector1.numbers.size(); index++) {
			double difference = vector1.numbers.get(index) - vector2.numbers.get(index);
			difference = difference * difference;
			squaredDistance = squaredDistance + difference;
		}
			 
		double distance = Math.sqrt(squaredDistance);	
		return distance;
	}
}
