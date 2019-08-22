import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.lang.Math;

// Portfolio Maker object to create a desired number of portfolios of given variety
// contains a list of possible vectors to use in portfolio construction
// when establishing intervals of vector tabulation, granularity is used
public class PortfolioMaker {

	ArrayList<Vector> vectorList;
	int granularity;

	PortfolioMaker (int inputGranularity) {

		vectorList = new ArrayList<Vector>();
		granularity = inputGranularity;
	}

	// method to determine if data collection from vector is enough to use in simulation
	// for given vector / time period, for each day, check if vector exists/if day is valid
	private boolean vectorValidity (String stockName, String startDate, String endDate, DataCollection dc, DateModifications dm) {

		String currDate = startDate;

		while (!currDate.equals(endDate)) {
			if (dc.tradingDays.get(currDate) == null || 
				dc.checkIfVectorDataExists(stockName, currDate)) {
				
				currDate = dm.incrementDate(currDate);
			}
			else {
				return false;
			}
			
		}
		return true;
	}

	// given a stock name and given start/end date, create + return vector for stock
	// values of vector correspond to % changes in share price
	// intervals to compute % change determined by Portfolio Maker instance granularity
	private Vector createVector (String stockName, String startDate, String endDate, DataCollection dc, DateModifications dm) {

		while (!dc.checkIfVectorDataExists(stockName, startDate)) {
			startDate = dm.incrementDate(startDate);
		}
		while (!dc.checkIfVectorDataExists(stockName, endDate)) {
			endDate = dm.decrementDate(endDate);
		}

		ArrayList<Double> vectorValues = new ArrayList<Double>();
		String periodEndDate = startDate;
		String periodStartDate = startDate;

		while (!periodEndDate.equals(endDate)) {
			periodStartDate = periodEndDate;
			for (int currCount = 0; currCount < granularity; currCount++) {
				if (!periodEndDate.equals(endDate)) {
					periodEndDate = dm.incrementDate(periodEndDate);
				}

				while (!dc.checkIfVectorDataExists (stockName, periodEndDate) && !periodEndDate.equals(endDate)) {
					periodEndDate = dm.incrementDate(periodEndDate);
				}
			}

			if (!periodEndDate.equals(endDate)) {
				String periodStartID = stockName + "#" + periodStartDate;
				String periodEndID = stockName + "#" + periodEndDate;
				double change = dc.dataPoints.get(periodEndID).close - dc.dataPoints.get(periodStartID).close;
				double percentChange = (100 * (change / dc.dataPoints.get(periodStartID).close));
				vectorValues.add(percentChange);
			}
		}
		return new Vector(stockName, vectorValues);
	}

	// method to iterate through all vector names (DataCollection method) + check for validity
	// validity defined in vector Validity function -- if true, vector is added
	// create vector method is called to initalize specific vector
	private ArrayList<Vector> createVectors (String filename, String startDate, String endDate, DataCollection dc, DateModifications dm, ArrayList<String> vectorNames) {

		for (int i = 0; i < vectorNames.size(); i++) {
			String vectorName = vectorNames.get(i);
			if (vectorValidity(vectorName, startDate, endDate, dc, dm)) {
				Vector currVector = createVector(vectorName, startDate, endDate, dc, dm);
				if (vectorList.size() == 0) {
					vectorList.add(createVector(vectorName, startDate, endDate, dc, dm));
				}
				else if (vectorList.size() > 0 && 
					currVector.numbers.size() == vectorList.get(0).numbers.size()) {
					vectorList.add(createVector(vectorName, startDate, endDate, dc, dm));
				}
			}
		}
		return vectorList;
	}	

	// method to create + return a diverse portfolio using Hierarchical Agglomerative Clustering
	// assembles list of vectors using helper methods (create Vectors)
	// calls instance of HAC clustering algorithm, reorganizing algorithm for easy access
	// iterates through list of clusters, arbitrarily picks a single stock from each cluster
	// makes sure that all vectors are complete for given trading dates
	// assembles + returns portfolio of arbitrarily selected vectors
	public Portfolio diverseHAC (String filename, int clusterSize, double factor, String startDate, double cash, String endDate, String strategyStartDate, String strategyEndDate, DataCollection dc, DateModifications dm, ArrayList<String> vectorNames) {

		ArrayList<String> stocksInPortfolio = new ArrayList<String>();
		ArrayList<Double> valuesInPortfolio = new ArrayList<Double>();

		try {
			ArrayList<Vector> stockVectors = createVectors (filename, startDate, endDate, dc, dm, vectorNames);
			HAClustering hac = new HAClustering();
			ArrayList<ArrayList<Vector>> listOfClusters = hac.assembleClusters(stockVectors, clusterSize, factor);
			ArrayList<String> stocksToAdd = new ArrayList<String>();
			
			for (int count = 0; count < listOfClusters.size(); count++) {
				int sizeOfCluster = listOfClusters.get(count).size();
				double rand = Math.random();
				int randomElementChosen = (int)(rand * (sizeOfCluster - 1));
				Vector stockChosen = listOfClusters.get(count).get(randomElementChosen);
				stocksInPortfolio.add(stockChosen.name);

			}
		} 
		catch (Exception e) {
			System.out.println("Vectors could not be created");
		}

		Portfolio port = new Portfolio ("Diverse HAC Portfolio", cash,
			stocksInPortfolio, valuesInPortfolio);

		while (!dc.checkIfPortfolioDataExists (port, strategyStartDate)) {
			strategyStartDate = dm.incrementDate(strategyStartDate);
		}

		double cashPerStock = cash / stocksInPortfolio.size();

		for (int portIndex = 0; portIndex < stocksInPortfolio.size(); portIndex++) {
			String stockID = stocksInPortfolio.get(portIndex) + "#" + strategyStartDate;
			double currSharePrice = dc.dataPoints.get(stockID).close;
			double numShares = cashPerStock / currSharePrice;
			valuesInPortfolio.add(numShares);
		}

		return port;
	}

	// method to create + return a diverse portfolio using k-Means Clustering
	// assembles list of vectors using helper methods (create Vectors)
	// calls instance of k Means clustering algorithm
	// iterates through list of clusters, arbitrarily picks a single stock from each cluster
	// finds share price on starting date, computes number of shares given amt of cash specified
	// assembles + returns portfolio of arbitrarily selected vectors
	public Portfolio diversekMeans (String filename, int numClusters, String startDate, double cash, String endDate, String strategyStartDate, String strategyEndDate, DataCollection dc, DateModifications dm, ArrayList<String> vectorNames) {

		ArrayList<String> stocksInPortfolio = new ArrayList<String>();
		ArrayList<Double> valuesInPortfolio = new ArrayList<Double>();

		try {
			ArrayList<Vector> stockVectors = createVectors (filename, startDate, endDate, dc, dm, vectorNames);
			KMeans kMeans = new KMeans();
			ArrayList<ArrayList<Vector>> listOfClusters = kMeans.cluster (stockVectors, numClusters);
			
			ArrayList<String> stocksToAdd = new ArrayList<String>();

			for (int count = 0; count < listOfClusters.size(); count++) {
				int sizeOfCluster = listOfClusters.get(count).size();
				double rand = Math.random();
				int randomElementChosen = (int)(rand * (sizeOfCluster - 1));
				Vector stockChosen = listOfClusters.get(count).get(randomElementChosen);
				stocksInPortfolio.add(stockChosen.name);
			}
		} 
		catch (Exception e) {
			System.out.println("Vectors could not be created");
		}

		Portfolio port = new Portfolio ("Diverse kMeans Portfolio", cash,
			stocksInPortfolio, valuesInPortfolio);

		while (!dc.checkIfPortfolioDataExists (port, strategyStartDate)) {
			strategyStartDate = dm.incrementDate(strategyStartDate);
		}

		double cashPerStock = cash / stocksInPortfolio.size();
		
		for (int portIndex = 0; portIndex < stocksInPortfolio.size(); portIndex++) {
			String stockID = stocksInPortfolio.get(portIndex) + "#" + strategyStartDate;
			double currSharePrice = dc.dataPoints.get(stockID).close;
			double numShares = cashPerStock / currSharePrice;
			valuesInPortfolio.add(numShares);
		}



		return port;

	}

	// method to create + return a uniform portfolio using Hierarchical Agglomerative Clustering
	// assembles list of vectors using helper methods (create Vectors)
	// calls instance of HAC clustering algorithm, reorganizing algorithm for easy access
	// iterates through list of clusters, arbitrarily picks a single stock from each cluster
	// makes sure that all vectors are complete for given trading dates
	// assembles + returns portfolio of arbitrarily selected vectors
	public Portfolio uniformHAC (String filename, int clusterSize, double factor, String startDate, double cash, String endDate, String strategyStartDate, String strategyEndDate, DataCollection dc, DateModifications dm, ArrayList<String> vectorNames) {

		ArrayList<String> stocksInPortfolio = new ArrayList<String>();
		ArrayList<Double> valuesInPortfolio = new ArrayList<Double>();

		try {
			ArrayList<Vector> stockVectors = createVectors (filename, startDate, endDate, dc, dm, vectorNames);
			HAClustering hac = new HAClustering();
			ArrayList<ArrayList<Vector>> listOfClusters = hac.assembleClusters(stockVectors, clusterSize, factor);
			ArrayList<String> stocksToAdd = new ArrayList<String>();

			int indexOfChoice = (int)(Math.random() * (listOfClusters.size() - 1));
			ArrayList<Vector> clusterOfChoice = listOfClusters.get(indexOfChoice);

			for (int count = 0; count < clusterOfChoice.size(); count ++) {
				Vector stockChosen = clusterOfChoice.get(count);
				stocksInPortfolio.add(stockChosen.name);
			}
		} 
		catch (Exception e) {
			System.out.println("Vectors could not be created");
		}

		Portfolio port = new Portfolio ("Uniform HAC Portfolio", cash,
			stocksInPortfolio, valuesInPortfolio);

		while (!dc.checkIfPortfolioDataExists (port, strategyStartDate)) {
			strategyStartDate = dm.incrementDate(strategyStartDate);
		}

		double cashPerStock = cash / stocksInPortfolio.size();

		for (int portIndex = 0; portIndex < stocksInPortfolio.size(); portIndex++) {
			String stockID = stocksInPortfolio.get(portIndex) + "#" + strategyStartDate;
			double currSharePrice = dc.dataPoints.get(stockID).close;
			double numShares = cashPerStock / currSharePrice;
			valuesInPortfolio.add(numShares);
		}

		return port;
	}

	// method to create + return a uniform portfolio using K-Means Clustering
	// assembles list of vectors using helper methods (create Vectors)
	// calls instance of HAC clustering algorithm, reorganizing algorithm for easy access
	// chooses arbitrary cluster from list of clusters, assembles + returns portfolio of these
	public Portfolio uniformkMeans (String filename, int numClusters, String startDate, double cash, String endDate, String strategyStartDate, String strategyEndDate, DataCollection dc, DateModifications dm, ArrayList<String> vectorNames){

		ArrayList<String> stocksInPortfolio = new ArrayList<String>();
		ArrayList<Double> valuesInPortfolio = new ArrayList<Double>();
		
		try {
			ArrayList<Vector> stockVectors = createVectors (filename, startDate, endDate,dc, dm, vectorNames);
			KMeans kMeans = new KMeans();
			ArrayList<ArrayList<Vector>> listOfClusters = kMeans.cluster (stockVectors, (stockVectors.size()/numClusters));

			ArrayList<String> stocksToAdd = new ArrayList<String>();

			int indexOfChoice = (int)(Math.random() * (listOfClusters.size() - 1));
			ArrayList<Vector> clusterOfChoice = listOfClusters.get(indexOfChoice);

			for (int count = 0; count < clusterOfChoice.size(); count ++) {
				Vector stockChosen = clusterOfChoice.get(count);
				stocksInPortfolio.add(stockChosen.name);
			}
		} 
		catch (Exception e) {
			System.out.println("Vectors could not be created");
		}
		
		Portfolio port = new Portfolio ("Uniform kMeans Portfolio", cash,
			stocksInPortfolio, valuesInPortfolio);

		while (!dc.checkIfPortfolioDataExists (port, strategyStartDate)) {
			strategyStartDate = dm.incrementDate(strategyStartDate);
		}

		double cashPerStock = cash / stocksInPortfolio.size();

		for (int portIndex = 0; portIndex < stocksInPortfolio.size(); portIndex++) {
			String stockID = stocksInPortfolio.get(portIndex) + "#" + strategyStartDate;
			double currSharePrice = dc.dataPoints.get(stockID).close;
			double numShares = cashPerStock / currSharePrice;
			valuesInPortfolio.add(numShares);
		}

		return port;
	}

	// Given a user inputted list of stocks they want in portfolio + corresponding number of $$
	// portfolio checks if all stocks are viable, and adds to portfolio
	// for each $$ amount inputted, converts to number of shares available
	public Portfolio userInputPortfolio(ArrayList<String> listOfStocks, ArrayList<Double> listOfValues, Double cash, String strategyStartDate, String strategyEndDate, DataCollection dc, DateModifications dm) {

		ArrayList<String> stocksInPortfolio = new ArrayList<String>();
		ArrayList<Double> valuesInPortfolio = new ArrayList<Double>();

		for (int index = 0; index < listOfStocks.size(); index++) {
			String vectorName = listOfStocks.get(index);
			Double valueInvested = listOfValues.get(index);
			if (vectorValidity(vectorName, strategyStartDate, strategyEndDate, dc, dm)) {
				stocksInPortfolio.add(vectorName);
				valuesInPortfolio.add(valueInvested);
			}
			else {
				System.out.print("Stock ");
				System.out.print(vectorName);
				System.out.println(" not viable for given dates. Cannot be added to portfolio");
			}
		}

		Portfolio port = new Portfolio ("User Input Portfolio", cash,
			stocksInPortfolio, valuesInPortfolio);


		while (!dc.checkIfPortfolioDataExists (port, strategyStartDate)) {
			strategyStartDate = dm.incrementDate(strategyStartDate);
		}

		for (int portIndex = 0; portIndex < stocksInPortfolio.size(); portIndex++) {
			String stockID = stocksInPortfolio.get(portIndex) + "#" + strategyStartDate;
			double dollarsInStock = valuesInPortfolio.get(portIndex);
			double currSharePrice = dc.dataPoints.get(stockID).close;
			double numShares = dollarsInStock / currSharePrice;
			valuesInPortfolio.set(portIndex, numShares);
		}
		return port;
	}

}
