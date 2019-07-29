import java.io.*;
import java.util.*;
import java.lang.Math; 

public class KMeans {
// public class to implement kMeans clustering algorithm on a vector list

	private Vector midpoint (List<Vector> vectors) { 
	//computes + returns a midpoint Vector from a given list of Vectors
	// throws exceptions for empty vector list, Vectors of different lengths
		
		List<Double> midpointCoordinates = new ArrayList<Double>();
		int numVectors = vectors.size();

		assert (numVectors > 0) : "Incompatible vector list for midpoint computation";

		int lenVector = vectors.get(0).numbers.size();

		for (int index = 0; index < numVectors; index++) {
			int vectorSize = vectors.get(index).numbers.size();
			assert (vectorSize == lenVector) : "Vector list contains inconsistent # of dimensions";
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
	
	private int closestCenter (Vector startVector, List<Vector> listOfCenters) { 
	// given a starting vector and list of cluster centers
	// iterates through centers, computes distance, updates closest center index if necessary
	// returns index of closest center

		int closestIndex = 0;
		double distanceToClosest = startVector.distance(startVector, listOfCenters.get(0));
		
		
		for (int center = 0; center < listOfCenters.size(); center++) {
			double distance = startVector.distance(startVector,listOfCenters.get(center));
			
			if (distance < distanceToClosest) {
				closestIndex = center;
				distanceToClosest = distance;
			}
		}
		
		return closestIndex;
	}
	
	private boolean isClusteringFinished (List<Vector> oldCenters, List<Vector> currCenters) { 
	// given 2 lists - old center vectors and new center vectors
	// compares corresponding old center/new center by index
	// computes normalized distance between old/new center (distance adjusted by magnitude)
	// if normalized distance greater than very small threshold, clustering not finished
	// repeats process for all centers, if all pass then clustering is done
	// goal = to ensure center coordinates have changed very minimally between reclustering

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
	
	private List<List<Vector>> emptyClusters (int quantity) { 
	// initializes List of desired number of clusters (each cluster is a list of vectors)

		assert (quantity > 0) : "Incompatible number of empty clusters desired";
	
		List<List<Vector>> clusterList = new ArrayList<List<Vector>>();
		
		for (int count = 0; count < quantity; count++) {
			List<Vector> cluster = new ArrayList<Vector>();
			clusterList.add(cluster);
		}
		
		return clusterList;
	}
			
	public List<List<Vector>> cluster (List<Vector> vectors, int numClusters) {
	// complete clustering algorithm
	// iterates through all vectors, find closest center, add to corresponding cluster
	// by calling isClusteringFinished method, determine if centers are consistent enough
	// else, recompute cluster centers by calculating midpoint of clusters
	// repeat until clustering is finished, return list of clusters

		assert (vectors.size() > 0) : " Incompatible vector list size";
		assert  (numClusters > 1) : "Incompatible number of clusters";
		assert (vectors.size() > numClusters) : "# Clusters desired incompatible";

		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}
	
		List<List<Vector>> returnlist = new ArrayList<List<Vector>>(); 

		int numVectors = vectors.size();
		
		List<Vector> currCenters = new ArrayList<Vector>();
		for (int count = 0; count < numClusters; count++) { 
		// chooses random centers to begin, adds to list of centers
			int randomIndex = (int)(Math.random() * numVectors);
			currCenters.add(new Vector("current", vectors.get(randomIndex).numbers));
		}

		List<Vector> oldCenters = new ArrayList<Vector>(); 
		// initializes old list of centers for isClusteringFinished method

		do { 
		// main loop
		// iterates through all vectors, for each vector finds closest center
		// adds vector to cluster with index corresponding to center's index in center list
		// checks if clustering is complete, repeats as necessary
			
			List<List<Vector>> currClusters = emptyClusters(numClusters);
			

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
					returnlist.add(currClusters.get(index));
				}
			}
			
		} while (! isClusteringFinished(oldCenters, currCenters));
		return returnlist;
	}
}
		