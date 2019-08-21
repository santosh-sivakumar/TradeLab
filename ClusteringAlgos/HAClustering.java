import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math; 

// public class to implement Hierarchical Agglomerative clustering algorithm on a vector ArrayList
public class HAClustering {

	// computes + returns a midpoint Vector from a given ArrayList of Vectors
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

	// given a starting vector and ArrayList of vectors
	// iterates through centers, computes distance, updates closest vector index if necessary
	// returns index of closest vector
	public int findClosest (Vector startVector, ArrayList<Vector> vectors) {
		
		assert (startVector.numbers.size() > 0): "Start Vector has incompatible # of dimensions";
		assert (vectors.size() > 1) : "Incompatible ArrayList of vectors";

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

	// iterates through ArrayList of vectors
	// for each vector, finds the closest vector and computes distance
	// finds two vectors in ArrayList with shortest distance in between, return pair as a ArrayList
	public ArrayList<Vector> closestTwoVectors (ArrayList<Vector> vectors) {

		assert (vectors.size() > 1) : "Incompatible ArrayList of vectors";
		
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

		ArrayList<Vector> closestTwo = new ArrayList<Vector>();
		closestTwo.add(vectors.get(index1));
		closestTwo.add(vectors.get(index2));
		return closestTwo;
	}

	// complete clustering algorithm
	// maintains a regularly updating ArrayList of "live vectors" in need of further clustering
	// finds two closest vectors, creates parent vector from pair [midpoint of two vectors]
	// replaces two child vectors with parent vector, create clustering algorithm once again
	// process will repeat until number of live vecttors = desired number of clusters 
	// cluster tree implemented using HashMap data structure
		// keys are child vectors, values are parent vectors
	// for top vectors in cluster tree, key = value = vector [self]
	// method returns cluster tree HashMap
	private HashMap<Vector, Vector> haClustering (ArrayList<Vector> vectors, int numClusters) {

		assert (vectors.size() > 0) : " Incompatible vector ArrayList size";
		assert  (numClusters > 0) : "Incompatible number of clusters";
		assert (vectors.size() > numClusters) : "# Clusters desired incompatible";

		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}
	
		HashMap<Vector, Vector> branches = new HashMap<Vector, Vector>();
		ArrayList<Vector> liveVectors = new ArrayList<Vector>();

		for (int index = 0; index < vectors.size(); index++) {
			liveVectors.add(vectors.get(index));
		}
		while (liveVectors.size() > numClusters) {
			ArrayList<Vector> closestVectors = closestTwoVectors(liveVectors);
			
			Vector vector1 = closestVectors.get(0);
			Vector vector2 = closestVectors.get(1);
			ArrayList<Vector> vectorsForMidpoint = new ArrayList<Vector>();
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

	// from cluster tree HashMap, find top vectors [key and value are equal]
	// create a HashMap of top cluster vectors, keys are vectors and values are cluster ID
	// iterate through main cluster tree HashMap and identify top vectors in tree
	// create new HashMap [to be returned] of original vectors and correspond cluster ID
		// iterate through all vectors in main ArrayList, find cluster ID, add to HashMap
	// return cluster ID HashMap
	public HashMap<Vector, Integer> sortClusters (ArrayList<Vector> vectors, int numClusters) {

		assert (vectors.size() > 0) : " Incompatible vector ArrayList size";
		assert  (numClusters > 0) : "Incompatible number of clusters";
		assert (vectors.size() > numClusters) : "# Clusters desired incompatible";

		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}

		HashMap<Vector, Integer> clusterID = new HashMap<Vector, Integer>();
		HashMap<Vector,Vector> branches = haClustering (vectors, numClusters);
		HashMap<Object, Integer> topClusters = new HashMap<Object, Integer>();
		ArrayList<Vector> branchesKeys = new ArrayList<Vector>(branches.keySet());
		
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

	// public method, given sorted HashMap of Vector-clusterID pairs, assembles ArrayList of clusters
	// each cluster represented as ArrayList of Vectors, all clusters put into output ArrayList
	public ArrayList<ArrayList<Vector>> ArrayListOfClusters (HashMap<Vector,Integer> clusterHashMap, int numClusters) {

		assert (numClusters > 0) : "Incompatible number of clusters";
		ArrayList<Vector> mapKeys = new ArrayList<>(clusterHashMap.keySet());
		ArrayList<ArrayList<Vector>> ArrayListOfClusters = new ArrayList<ArrayList<Vector>>();

		for (int count = 0; count < numClusters; count++) {
			ArrayList<Vector> currentCluster = new ArrayList<Vector>();
			for (int index = 0; index < mapKeys.size(); index++) {
				if (clusterHashMap.get(mapKeys.get(index)) == count) {
					currentCluster.add(mapKeys.get(index));
				}
			}
			ArrayListOfClusters.add(currentCluster);
		}
		return ArrayListOfClusters;
	}

	// complete clustering algorithm
	// maintains a regularly updating ArrayList of "live vectors" in need of further clustering
	// finds two closest vectors, creates parent vector from pair [midpoint of two vectors]
	// replaces two child vectors with parent vector, create clustering algorithm once again
	// process will repeat until number of live vecttors = desired number of clusters 
	// cluster tree implemented using HashMap data structure
		// keys are child vectors, values are parent vectors
	// for top vectors in cluster tree, key = value = vector [self]
	// method returns cluster tree HashMap
	private Vector haClusteringUsingKMeans (ArrayList<Vector> vectors, HashMap<Vector,Vector> clusterHashMap) {

		assert (vectors.size() > 0) : " Incompatible vector ArrayList size";

		Vector topVector = vectors.get(0);

		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}
	
		ArrayList<Vector> liveVectors = new ArrayList<Vector>();

		for (int index = 0; index < vectors.size(); index++) {
			liveVectors.add(vectors.get(index));
		}
		while (liveVectors.size() > 1) {
			ArrayList<Vector> closestVectors = closestTwoVectors(liveVectors);
			
			Vector vector1 = closestVectors.get(0);
			Vector vector2 = closestVectors.get(1);
			ArrayList<Vector> vectorsForMidpoint = new ArrayList<Vector>();
			vectorsForMidpoint.add(vector1);
			vectorsForMidpoint.add(vector2);
			Vector average = midpoint(vectorsForMidpoint);
				
			clusterHashMap.put(vector1, average);
			clusterHashMap.put(vector2, average);
			
			liveVectors.remove(vector1);
			liveVectors.remove(vector2);
			liveVectors.add(average);
		}

		if (liveVectors.size() == 1) {
			for (int index = 0; index < liveVectors.size(); index++) {
				clusterHashMap.put(liveVectors.get(index), liveVectors.get(index));
				topVector = liveVectors.get(index);
			}
		}

		return topVector;
	}

	// HAC clustering using KMeans initialization to improve run time efficiency
	// splits ArrayList of vectors into sqrt(num Vectors) clusters using K-Means cluster
	// within and between clusters, implements hierarchical clustering algorithm
	// run time - O(n*sqrt(n)) --> usability with large [4500 stock] data set
	private HashMap<Vector, Vector> hACWithKmeans (ArrayList<Vector> vectors) {


		assert (vectors.size() > 0) : "Incompatible vector ArrayList";
		int numClustersRound1 = (int)(Math.sqrt(vectors.size()));

		KMeans kMeansForHAC = new KMeans ();
		ArrayList<ArrayList<Vector>> clustersRound1 = kMeansForHAC.cluster(vectors, numClustersRound1);

		HashMap<Vector,Vector> finalClusters = new HashMap<Vector,Vector>();
		ArrayList<Vector> topVectorsRound1 = new ArrayList<Vector>();

		for (int index = 0; index < clustersRound1.size(); index++){
			Vector topVector = haClusteringUsingKMeans (clustersRound1.get(index), finalClusters);
			topVectorsRound1.add(topVector);
		}

		Vector topVectorRound2 = haClusteringUsingKMeans (topVectorsRound1, finalClusters);
		return finalClusters;

	}

	// given HAC child to parent HashMap, returns two individual Parent-to-Child HashMaps
	// iterates through ArrayList of vector children, places child-parent key-value set
	   // in appropriate HashMap based on number of children already looked at
	// relies on proper binary tree setup for HAC output
	private void createParentToChildHashMaps (HashMap <Vector, Vector> childToParent, 
		HashMap<Vector, Vector> childOne, HashMap<Vector,Vector> childTwo) {

		ArrayList<Vector> childKeys = new ArrayList<Vector>(childToParent.keySet());
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

	// method returns root of given binary tree
	// root represented as node in child-parent HashMap where key/value are equal
	private Vector returnRoot (HashMap<Vector,Vector> childToParent) {

		ArrayList<Vector> childKeys = new ArrayList<Vector>(childToParent.keySet());

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

	// method recursively computes number of sub-leaves for each node in binary tree
	// sub-leaves = number of original vectors that exist under a given node
	// adds node/number of sub-leaves to continuously updating HashMap
	private int numChildLeaves (HashMap<Vector,Integer> numLeaves,
		HashMap<Vector,Vector> childOne, HashMap<Vector,Vector> childTwo, Vector currVector) {

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

	// viability  = whether a node contains more subleaves than required cluster size
	// cluster size determined using desired size and acceptable distortion factor
	// if not, all vectors under node do not constitute a complete vector (or more)
	// method determines node viability using HashMap and returns boolean value
	private boolean isViable (Vector node, HashMap<Vector,Integer> numLeaves, int clusterSize, double factor) {

		double lowerThreshold = clusterSize - (clusterSize * factor);

		if (lowerThreshold < numLeaves.get(node)) {
			return true;
		}
		return false;
	}

	// similar to is Viable method, this method returns, for a node, whether its sub tree is a viable cluster
	// viability = whether a node contains num Subleaves greater than min cluster size/less than max cluster size
	// cluster size determined using desired size and acceptable distortion factor
	// method determines node viability using HashMap and returns boolean value
	private boolean inClusterRange (Vector node, HashMap<Vector,Integer> numLeaves, int clusterSize, double factor) {

		double lowerThreshold = clusterSize - (clusterSize * factor);
		double upperThreshold = clusterSize + (clusterSize *factor);

		if (lowerThreshold < numLeaves.get(node) && numLeaves.get(node) < upperThreshold) {
			return true;
		}
		return false;
	}

	// method maintains a queue of all nodes to be checked for cluster viability
	// using viability/in Cluster Range methods, determines top nodes for each cluster
	// creates and maintains ArrayList of top nodes to be returned
	private ArrayList<Vector> ArrayListOfTopNodes (ArrayList<Vector> vectors, int clusterSize, double factor,
		HashMap<Vector,Vector> childOne, HashMap<Vector,Vector> childTwo, 
		HashMap<Vector,Integer> numLeaves, HashMap<Vector,Vector> childToParent) {

		ArrayList<Vector> topNodes = new ArrayList<Vector>();

		Vector root = returnRoot (childToParent);
		assert (isViable(root, numLeaves, clusterSize, factor)) : "Incompatible numbers given ArrayList";

		ArrayList<Vector> currNodes = new ArrayList<Vector>();
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

	// method executes HAC with KMeans clustering + returns ArrayList of all processed clusters
	// calls on all helper methods, determines top nodes for each cluster
	// implements while loop with constantly updating queue of nodes to be checked for cluster addition
	// assembles + returns final cluster ArrayList
	public ArrayList<ArrayList<Vector>> assembleClusters (ArrayList<Vector> vectors, int clusterSize, double factor) {

		
		ArrayList<ArrayList<Vector>> clusters = new ArrayList<ArrayList<Vector>>();
		assert (vectors.size() > 0) : " Incompatible vector ArrayList size";
		int size = vectors.get(0).numbers.size();
		for (int index = 0; index < vectors.size(); index++) {
			Vector curr = vectors.get(index);
			assert (curr.numbers.size() == size) : "# Dimension inconsistency";
		}
		HashMap<Vector,Vector> childToParent = hACWithKmeans (vectors);
		HashMap<Vector, Vector> childOne = new HashMap<Vector,Vector>();
		HashMap<Vector, Vector> childTwo = new HashMap<Vector,Vector>();
		HashMap<Vector,Integer> numLeaves = new HashMap<Vector,Integer>();

		createParentToChildHashMaps (childToParent, childOne, childTwo);
		Vector root = returnRoot (childToParent);
		int numTopLeaves = numChildLeaves (numLeaves, childOne, childTwo, root);
		ArrayList<Vector> topNodes = ArrayListOfTopNodes (vectors, clusterSize, factor, 
			childOne, childTwo, numLeaves, childToParent);

		for (int index = 0; index < topNodes.size(); index++) {
			ArrayList<Vector> currCluster = new ArrayList<Vector>();
			ArrayList<Vector> liveNodes = new ArrayList<Vector>();
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
