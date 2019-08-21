import java.util.ArrayList;
import java.lang.Math; 

// public class to implement kMeans clustering algorithm on a vector ArrayList
public class KMeans {

	//computes + returns a midpoint Vector from a given ArrayList of Vectors
	// throws exceptions for empty vector ArrayList, Vectors of different lengths
	private Vector midpoint (ArrayList<Vector> vectors) { 
		
		ArrayList<Double> midpointCoordinates = new ArrayList<Double>();
		int numVectors = vectors.size();

		assert (numVectors > 0) : "Incompatible vector ArrayList for midpoint computation";

		int lenVector = vectors.get(0).numbers.size();

		for (int index = 0; index < numVectors; index++) {
			int vectorSize = vectors.get(index).numbers.size();
			assert (vectorSize == lenVector) : "Vector ArrayList contains inconsistent # of dimensions";
		}	
			
		for (int dimension = 0; dimension < lenVector; dimension++) {
			double coordinate = 0.0;
			
			for (int curr = 0; curr < numVectors; curr ++) {
				coordinate += vectors.get(curr).numbers.get(dimension);
			}
				
			coordinate /= numVectors;
			midpointCoordinates.add(coordinate);	
		}

		return new Vector("midpoint", midpointCoordinates);
	}

	// given a starting vector and ArrayList of cluster centers
	// iterates through centers, computes distance, updates closest center index if necessary
	// returns index of closest center
	private int closestCenter (Vector startVector, ArrayList<Vector> ArrayListOfCenters) { 

		int closestIndex = 0;
		double distanceToClosest = startVector.distance(startVector, ArrayListOfCenters.get(0));
		
		
		for (int center = 0; center < ArrayListOfCenters.size(); center++) {
			double distance = startVector.distance(startVector,ArrayListOfCenters.get(center));
			
			if (distance < distanceToClosest) {
				closestIndex = center;
				distanceToClosest = distance;
			}
		}
		
		return closestIndex;
	}

	// given 2 ArrayLists - old center vectors and new center vectors
	// compares corresponding old center/new center by index
	// computes normalized distance between old/new center (distance adjusted by magnitude)
	// if normalized distance greater than very small threshold, clustering not finished
	// repeats process for all centers, if all pass then clustering is done
	// goal = to ensure center coordinates have changed very minimally between reclustering
	private boolean isClusteringFinished (ArrayList<Vector> oldCenters, ArrayList<Vector> currCenters) { 


		double NORMALIZATION_THRESHOLD = 0.001; //arbitrary threshold, can be manipulated

		if (oldCenters.size() != currCenters.size()) {
			return false;
		}
	
		for (int index = 0; index < currCenters.size(); index++) {
			Vector oldCenter = oldCenters.get(index);
			Vector currCenter = currCenters.get(index);
			assert (currCenter.numbers.size() == oldCenter.numbers.size()) : "Incompatible centers";
			double distanceBetweenCenters = oldCenter.distance(oldCenter, currCenter);
			double magnitude = currCenter.magnitude();
			double normalizedDistance = distanceBetweenCenters / magnitude;

			if (normalizedDistance > NORMALIZATION_THRESHOLD) {
				return false;
			}
		}

		return true;
	}
	
	// initializes ArrayList of desired number of clusters (each cluster is a ArrayList of vectors)
	private ArrayList<ArrayList<Vector>> emptyClusters (int quantity) { 

		assert (quantity > 0) : "Incompatible number of empty clusters desired";
	
		ArrayList<ArrayList<Vector>> clusterArrayList = new ArrayList<ArrayList<Vector>>();
		
		for (int count = 0; count < quantity; count++) {
			ArrayList<Vector> cluster = new ArrayList<Vector>();
			clusterArrayList.add(cluster);
		}
		
		return clusterArrayList;
	}

	// complete clustering algorithm
	// iterates through all vectors, find closest center, add to corresponding cluster
	// by calling isClusteringFinished method, determine if centers are consistent enough
	// else, recompute cluster centers by calculating midpoint of clusters
	// repeat until clustering is finished, return ArrayList of clusters
	public ArrayList<ArrayList<Vector>> cluster (ArrayList<Vector> vectors, int numClusters) {

		assert (vectors.size() > 0) : " Incompatible vector ArrayList size";
		assert  (numClusters > 1) : "Incompatible number of clusters";
		assert (vectors.size() > numClusters) : "# Clusters desired incompatible";

		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}
	
		ArrayList<ArrayList<Vector>> returnArrayList = new ArrayList<ArrayList<Vector>>(); 

		int numVectors = vectors.size();
		
		ArrayList<Vector> currCenters = new ArrayList<Vector>();
		for (int count = 0; count < numClusters; count++) { 
		// chooses random centers to begin, adds to ArrayList of centers
			int randomIndex = (int)(Math.random() * numVectors);
			currCenters.add(new Vector("current", vectors.get(randomIndex).numbers));
		}

		ArrayList<Vector> oldCenters = new ArrayList<Vector>(); 
		// initializes old ArrayList of centers for isClusteringFinished method

		do { 
		// main loop
		// iterates through all vectors, for each vector finds closest center
		// adds vector to cluster with index corresponding to center's index in center ArrayList
		// checks if clustering is complete, repeats as necessary
			
			ArrayList<ArrayList<Vector>> currClusters = emptyClusters(numClusters);
			

			for (int index = 0; index < numVectors; index++) {
				int closestCenter = closestCenter(vectors.get(index), currCenters);
				(currClusters.get(closestCenter)).add(vectors.get(index));	
			}
			
			numClusters = currCenters.size(); 
			// if cluster is empty, catches exception case by recomputing number of clusters
			
			oldCenters.clear(); 
			// update old centers
			for (int index = 0; index < numClusters; index ++) {
				oldCenters.add(currCenters.get(index));
			}
			
			currCenters.clear(); 
			// clear current centers, compute midpoints of each cluster
			// These midpoints become new centers
			for (int index = 0; index < numClusters; index ++) {
				if ((currClusters.get(index)).size() > 0) {
					currCenters.add(midpoint(currClusters.get(index)));
				}
			}

			if (isClusteringFinished(oldCenters, currCenters)) { 
			// check if clustering is complete
				for (int index = 0; index < currClusters.size(); index ++) {
					returnArrayList.add(currClusters.get(index));
				}
			}
			
		} while (! isClusteringFinished(oldCenters, currCenters));
		return returnArrayList;
	}
}
