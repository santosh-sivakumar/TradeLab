import java.io.*;
import java.util.*;
import java.lang.Math; 

public class HAC {

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

	public int findClosest (Vector startVector, List<Vector> vectors) {
	// given a starting vector and list of vectors
	// iterates through centers, computes distance, updates closest vector index if necessary
	// returns index of closest vector
		
		assert (startVector.numbers.size() > 0): "Start Vector has incompatible # of dimensions";
		assert (vectors.size() > 1) : "Incompatible list of vectors";

		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == startVector.numbers.size()) : "# Dimension inconsistency";
		}

		double smallest = startVector.distance(startVector, vectors.get(0));

		int closestVector = 0;
		
		if (vectors.get(0) == startVector) {
			smallest = startVector.distance(startVector, vectors.get(1));
			closestVector = 1;
		}

		for (int curr = 0; curr < vectors.size(); curr ++) {
			if (vectors.get(curr) != startVector) {
				double distance = startVector.distance(startVector, vectors.get(curr));
				if (distance < smallest) {
					smallest = distance;
					closestVector = curr;
				}
			}
		}
		
		return closestVector;
	}
	
	public List<Vector> closestTwoVectors (List<Vector> vectors) {
	// iterates through list of vectors
	// for each vector, finds the closest vector and computes distance
	// finds two vectors in list with shortest distance in between, return pair as a list

		assert (vectors.size() > 1) : "Incompatible list of vectors";
		
		int size = vectors.get(0).numbers.size();
		for (int index = 1; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}

		int index1 = 0;
		int index2 = 1;
		double shortestDistance = vectors.get(index1).distance(vectors.get(index1),vectors.get(index2));
		
		for (int curr = 0; curr < vectors.size(); curr++) {
			int closestIndex = findClosest(vectors.get(curr), vectors);
			double currDistance = vectors.get(curr).distance(vectors.get(curr), vectors.get(closestIndex));
			if (currDistance < shortestDistance) {
				shortestDistance = currDistance;
				index1 = curr;
				index2 = closestIndex;
			}
		}

		List<Vector> closestTwo = new ArrayList<Vector>();
		closestTwo.add(vectors.get(index1));
		closestTwo.add(vectors.get(index2));
		return closestTwo;
	}

	private Map<Vector, Vector> haClustering (List<Vector> vectors, int numClusters) {
	// complete clustering algorithm
	// maintains a regularly updating list of "live vectors" in need of further clustering
	// finds two closest vectors, creates parent vector from pair [midpoint of two vectors]
	// replaces two child vectors with parent vector, create clustering algorithm once again
	// process will repeat until number of live vecttors = desired number of clusters 
	// cluster tree implemented using HashMap data structure
		// keys are child vectors, values are parent vectors
	// for top vectors in cluster tree, key = value = vector [self]
	// method returns cluster tree HashMap

		assert (vectors.size() > 0) : " Incompatible vector list size";
		assert  (numClusters > 0) : "Incompatible number of clusters";
		assert (vectors.size() > numClusters) : "# Clusters desired incompatible";

		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}
	
		Map<Vector, Vector> branches = new HashMap<Vector, Vector>();
		List<Vector> liveVectors = new ArrayList<Vector>();

		for (int index = 0; index < vectors.size(); index++) {
			liveVectors.add(vectors.get(index));
		}
		while (liveVectors.size() > numClusters) {
			List<Vector> closestVectors = closestTwoVectors(liveVectors);
			
			Vector vector1 = closestVectors.get(0);
			Vector vector2 = closestVectors.get(1);
			List<Vector> vectorsForMidpoint = new ArrayList<Vector>();
			vectorsForMidpoint.add(vector1);
			vectorsForMidpoint.add(vector2);
			Vector average = midpoint(vectorsForMidpoint);
				
			branches.put(vector1, average);
			branches.put(vector2, average);
			
			liveVectors.remove(vector1);
			liveVectors.remove(vector2);
			liveVectors.add(average);
		}

		if (liveVectors.size() == numClusters) {
			for (int index = 0; index < liveVectors.size(); index++) {
				branches.put(liveVectors.get(index), liveVectors.get(index));
			}
		}

		return branches;
	}
	
	public Map<Vector, Integer> sortClusters (List<Vector> vectors, int numClusters) {
	// from cluster tree HashMap, find top vectors [key and value are equal]
	// create a HashMap of top cluster vectors, keys are vectors and values are cluster ID
	// iterate through main cluster tree HashMap and identify top vectors in tree
	// create new HashMap [to be returned] of original vectors and correspond cluster ID
		// iterate through all vectors in main list, find cluster ID, add to HashMap
	// return cluster ID HashMap
	
		assert (vectors.size() > 0) : " Incompatible vector list size";
		assert  (numClusters > 0) : "Incompatible number of clusters";
		assert (vectors.size() > numClusters) : "# Clusters desired incompatible";

		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}

		Map<Vector, Integer> clusterID = new HashMap<Vector, Integer>();
		Map<Vector,Vector> branches = haClustering (vectors, numClusters);
		Map<Object, Integer> topClusters = new HashMap<Object, Integer>();
		List<Vector> branchesKeys = new ArrayList<Vector>(branches.keySet());
		
		int currIndex = 0;

		for (int index = 0; index < branchesKeys.size(); index++) {
			if (branches.get(branchesKeys.get(index)) == branchesKeys.get(index)) {
				topClusters.put(branchesKeys.get(index), currIndex);
				currIndex += 1;
			}
		}
		
		for (int index = 0; index < vectors.size(); index++) {
		
			Vector currVector = vectors.get(index);
			Vector parentVector = branches.get(currVector);
			
			while (branches.get(parentVector) != parentVector) {
				parentVector = branches.get(parentVector);
			}
			
			clusterID.put(currVector, topClusters.get(parentVector));
		}
		return clusterID;
	}

	public List<List<Vector>> listOfClusters (Map<Vector,Integer> clusterMap, int numClusters) {
	// public method, given sorted hashMap of Vector-clusterID pairs, assembles list of clusters
	// each cluster represented as list of Vectors, all clusters put into output list

		assert (numClusters > 0) : "Incompatible number of clusters";
		List<Vector> hashMapKeys = new ArrayList<>(clusterMap.keySet());
		List<List<Vector>> listOfClusters = new ArrayList<List<Vector>>();

		for (int count = 0; count < numClusters; count++) {
			List<Vector> currentCluster = new ArrayList<Vector>();
			for (int index = 0; index < hashMapKeys.size(); index++) {
				if (clusterMap.get(hashMapKeys.get(index)) == count) {
					currentCluster.add(hashMapKeys.get(index));
				}
			}
			listOfClusters.add(currentCluster);
		}
		return listOfClusters;
	}

	
	private Vector haClusteringUsingKMeans (List<Vector> vectors, Map<Vector,Vector> clusterMap) {
	// complete clustering algorithm
	// maintains a regularly updating list of "live vectors" in need of further clustering
	// finds two closest vectors, creates parent vector from pair [midpoint of two vectors]
	// replaces two child vectors with parent vector, create clustering algorithm once again
	// process will repeat until number of live vecttors = desired number of clusters 
	// cluster tree implemented using HashMap data structure
		// keys are child vectors, values are parent vectors
	// for top vectors in cluster tree, key = value = vector [self]
	// method returns cluster tree HashMap

		assert (vectors.size() > 0) : " Incompatible vector list size";

		Vector topVector = vectors.get(0);

		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}
	
		List<Vector> liveVectors = new ArrayList<Vector>();

		for (int index = 0; index < vectors.size(); index++) {
			liveVectors.add(vectors.get(index));
		}
		while (liveVectors.size() > 1) {
			List<Vector> closestVectors = closestTwoVectors(liveVectors);
			
			Vector vector1 = closestVectors.get(0);
			Vector vector2 = closestVectors.get(1);
			List<Vector> vectorsForMidpoint = new ArrayList<Vector>();
			vectorsForMidpoint.add(vector1);
			vectorsForMidpoint.add(vector2);
			Vector average = midpoint(vectorsForMidpoint);
				
			clusterMap.put(vector1, average);
			clusterMap.put(vector2, average);
			
			liveVectors.remove(vector1);
			liveVectors.remove(vector2);
			liveVectors.add(average);
		}

		if (liveVectors.size() == 1) {
			for (int index = 0; index < liveVectors.size(); index++) {
				clusterMap.put(liveVectors.get(index), liveVectors.get(index));
				topVector = liveVectors.get(index);
			}
		}

		return topVector;
	}

	private Map<Vector, Vector> hACWithKmeans (List<Vector> vectors) {
	// HAC clustering using KMeans initialization to improve run time efficiency
	// splits list of vectors into sqrt(num Vectors) clusters using K-Means cluster
	// within and between clusters, implements hierarchical clustering algorithm
	// run time - O(n*sqrt(n)) --> usability with large [4500 stock] data set
		assert (vectors.size() > 0) : "Incompatible vector list";
		int numClustersRound1 = (int)(Math.sqrt(vectors.size()));

		KMeans kMeansForHAC = new KMeans ();
		List<List<Vector>> clustersRound1 = kMeansForHAC.cluster(vectors, numClustersRound1);

		Map<Vector,Vector> finalClusters = new HashMap<Vector,Vector>();
		List<Vector> topVectorsRound1 = new ArrayList<Vector>();

		for (int index = 0; index < clustersRound1.size(); index++){
			Vector topVector = haClusteringUsingKMeans (clustersRound1.get(index), finalClusters);
			topVectorsRound1.add(topVector);
		}

		Vector topVectorRound2 = haClusteringUsingKMeans (topVectorsRound1, finalClusters);
		return finalClusters;

	}

	private void createParentToChildMaps (Map <Vector, Vector> childToParent, 
		Map<Vector, Vector> childOne, Map<Vector,Vector> childTwo) {
	// given HAC child to parent HashMap, returns two individual Parent-to-Child Maps
	// iterates through list of vector children, places child-parent key-value set
	   // in appropriate map based on number of children already looked at
	// relies on proper binary tree setup for HAC output

		List<Vector> childKeys = new ArrayList<Vector>(childToParent.keySet());
		for (int index = 0; index < childKeys.size(); index++) {
			Vector child = childKeys.get(index);
			Vector parent = childToParent.get(child);

			if (!child.equals(parent)) {
				
				if (!childOne.containsKey(parent)) {
					childOne.put(parent, child);
				}
				else {
					assert (!childTwo.containsKey(parent)) : "Improper binary tree";
					childTwo.put(parent, child);
				}
			}
		}
	} 

	private Vector returnRoot (Map<Vector,Vector> childToParent) {
	// method returns root of given binary tree
	// root represented as node in child-parent map where key/value are equal

		List<Vector> childKeys = new ArrayList<Vector>(childToParent.keySet());

		assert (childKeys.size() > 0): "Incompatible HashMap";

		Vector root = childKeys.get(0);

		for (int index = 0; index < childKeys.size(); index++) {
			Vector child = childKeys.get(index);
			Vector parent = childToParent.get(child);

			if (child.equals(parent)) {
				root = child;
			}
		}
		return root;
	}

	private int numChildLeaves (Map<Vector,Integer> numLeaves,
		Map<Vector,Vector> childOne, Map<Vector,Vector> childTwo, Vector currVector) {
	// method recursively computes number of sub-leaves for each node in binary tree
	// sub-leaves = number of original vectors that exist under a given node
	// adds node/number of sub-leaves to continuously updating HashMap

		int numSubLeaves = 1;

		if (!childOne.containsKey(currVector)) {
			assert (!childTwo.containsKey(currVector)) : "Improper binary tree";
			numLeaves.put(currVector, numSubLeaves);
			return numSubLeaves;
		}

		Vector subChildOne = childOne.get(currVector);
		Vector subChildTwo = childTwo.get(currVector);		
		numSubLeaves = numChildLeaves (numLeaves, childOne, childTwo, subChildOne) 
						+ numChildLeaves (numLeaves, childOne, childTwo, subChildTwo);

		numLeaves.put(currVector, numSubLeaves);
		return numSubLeaves;
	}

	private boolean isViable (Vector node, Map<Vector,Integer> numLeaves, int clusterSize, double factor) {
	// viability  = whether a node contains more subleaves than required cluster size
	// cluster size determined using desired size and acceptable distortion factor
	// if not, all vectors under node do not constitute a complete vector (or more)
	// method determines node viability using HashMap and returns boolean value
		double lowerThreshold = clusterSize - (clusterSize * factor);

		if (lowerThreshold < numLeaves.get(node)) {
			return true;
		}
		return false;
	}

	private boolean inClusterRange (Vector node, Map<Vector,Integer> numLeaves, int clusterSize, double factor) {
	// similar to is Viable method, this method returns, for a node, whether its sub tree is a viable cluster
	// viability = whether a node contains num Subleaves greater than min cluster size/less than max cluster size
	// cluster size determined using desired size and acceptable distortion factor
	// method determines node viability using HashMap and returns boolean value

		double lowerThreshold = clusterSize - (clusterSize * factor);
		double upperThreshold = clusterSize + (clusterSize *factor);

		if (lowerThreshold < numLeaves.get(node) && numLeaves.get(node) < upperThreshold) {
			return true;
		}
		return false;
	}

	private List<Vector> listOfTopNodes (List<Vector> vectors, int clusterSize, double factor,
		Map<Vector,Vector> childOne, Map<Vector,Vector> childTwo, 
		Map<Vector,Integer> numLeaves, Map<Vector,Vector> childToParent) {
	// method maintains a queue of all nodes to be checked for cluster viability
	// using viability/in Cluster Range methods, determines top nodes for each cluster
	// creates and maintains list of top nodes to be returned

		List<Vector> topNodes = new ArrayList<Vector>();

		Vector root = returnRoot (childToParent);
		System.out.println(numLeaves.get(root));
		assert (isViable(root, numLeaves, clusterSize, factor)) : "Incompatible numbers given list";

		List<Vector> currNodes = new ArrayList<Vector>();
		currNodes.add(root);

		while (currNodes.size() > 0) {
			Vector currVector = currNodes.get(0);
			currNodes.remove(0);
			Vector currChildOne = childOne.get(currVector);
			Vector currChildTwo = childTwo.get(currVector);

			if (isViable(currChildOne, numLeaves, clusterSize, factor)){
				if (inClusterRange(currChildOne, numLeaves, clusterSize, factor)) {
					topNodes.add(currChildOne);
				}
				else {
					currNodes.add(currChildOne);
				}
			}

			if (isViable(currChildTwo, numLeaves, clusterSize, factor)){
				if (inClusterRange(currChildTwo, numLeaves, clusterSize, factor)) {
					topNodes.add(currChildTwo);
				}
				else {
					currNodes.add(currChildTwo);
				}
			}

			if (!isViable(currChildOne, numLeaves, clusterSize, factor) &&
				!isViable(currChildTwo, numLeaves, clusterSize, factor)) {
				topNodes.add(currVector);
			}
		}
		return topNodes;
	}

	public List<List<Vector>> assembleClusters (List<Vector> vectors, int clusterSize, double factor) {
	// method executes HAC with KMeans clustering + returns list of all processed clusters
	// calls on all helper methods, determines top nodes for each cluster
	// implements while loop with constantly updating queue of nodes to be checked for cluster addition
	// assembles + returns final cluster list
		
		List<List<Vector>> clusters = new ArrayList<List<Vector>>();
		assert (vectors.size() > 0) : " Incompatible vector list size";

		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}
		Map<Vector,Vector> childToParent = hACWithKmeans (vectors);
		Map<Vector, Vector> childOne = new HashMap<Vector,Vector>();
		Map<Vector, Vector> childTwo = new HashMap<Vector,Vector>();
		Map<Vector,Integer> numLeaves = new HashMap<Vector,Integer>();

		createParentToChildMaps (childToParent, childOne, childTwo);
		Vector root = returnRoot (childToParent);
		int numTopLeaves = numChildLeaves (numLeaves, childOne, childTwo, root);
		List<Vector> topNodes = listOfTopNodes (vectors, clusterSize, factor, 
			childOne, childTwo, numLeaves, childToParent);

		for (int index = 0; index < topNodes.size(); index++) {
			List<Vector> currCluster = new ArrayList<Vector>();
			List<Vector> liveNodes = new ArrayList<Vector>();
			liveNodes.add(topNodes.get(index));

			while (liveNodes.size() > 0) {
				Vector currVector = liveNodes.get(0);
				liveNodes.remove(0);
				Vector currChildOne = childOne.get(currVector);
				Vector currChildTwo = childTwo.get(currVector);

				if (numLeaves.get(currChildOne) > 1){
					liveNodes.add(currChildOne);
				}
				else {
					assert (numLeaves.get(currChildOne) == 1) : "Incompatible node";
					currCluster.add(currChildOne);
				}

				if (numLeaves.get(currChildTwo) > 1){
					liveNodes.add(currChildTwo);
				}
				else {
					assert (numLeaves.get(currChildTwo) == 1) : "Incompatible node";
					currCluster.add(currChildTwo);
				}
			}
			clusters.add(currCluster);
		}

		return clusters;
	}
}
