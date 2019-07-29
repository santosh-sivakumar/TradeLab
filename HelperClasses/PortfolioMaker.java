import java.util.*;
import java.io.*;
import java.lang.Math;

public class PortfolioMaker {

	List<Vector> vectorList;
	int granularity;

	PortfolioMaker (int inputGranularity) {
	// Portfolio Maker object to create a desired number of portfolios of given variety
	// contains a list of possible vectors to use in portfolio construction
	// when establishing intervals of vector tabulation, granularity is used
		vectorList = new ArrayList<Vector>();
		granularity = inputGranularity;
	}

	private boolean vectorValidity (String stockName, String startDate, String endDate, 
		DataCollection dc, DateModifications dm) {
	// method to determine if data collection from vector is enough to use in simulation
	// for given vector / time period, checks if data collected consistently during period
	// consistency defined as no gaps in data collection (> 3 days - trading holidays)
	// method leaves small room for error - in the case of an extraneous 3-day weekend .. to fix
		int numNonTradingDays = 0;
		String currDate = startDate;

		while (!currDate.equals(endDate) && numNonTradingDays <= 3) {
			if (!dc.checkIfVectorDataExists(stockName, currDate)) {
				numNonTradingDays += 1;
			}
			else {
				numNonTradingDays = 0;
			}
			currDate = dm.incrementDate(currDate);
		}
		if (numNonTradingDays > 3) {
			return false;
		}
		return true;
	}

	private Vector createVector (String stockName, String startDate, String endDate, 
		DataCollection dc, DateModifications dm) {
	// given a stock name and given start/end date, create + return vector for stock
	// values of vector correspond to % changes in share price
	// intervals to compute % change determined by Portfolio Maker instance granularity

		while (!dc.checkIfVectorDataExists(stockName, startDate)) {
			startDate = dm.incrementDate(startDate);
		}
		while (!dc.checkIfVectorDataExists(stockName, endDate)) {
			endDate = dm.decrementDate(endDate);
		}

		List<Double> vectorValues = new ArrayList<Double>();
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

	private List<Vector> createVectors (String filename, String startDate, String endDate, 
		DataCollection dc, DateModifications dm) throws Exception {
	// method to iterate through all vector names (DataCollection method) + check for validity
	// validity defined in vector Validity function -- if true, vector is added
	// create vector method is called to initalize specific vector
		List<String> vectorNames = dc.vectorNamesList(filename);

		for (int index = 0; index < vectorNames.size(); index++) {
			String vectorName = vectorNames.get(index);
			if (vectorValidity(vectorName, startDate, endDate, dc, dm)) {
				Vector currVector = createVector(vectorName, startDate, endDate, dc, dm);
				if (vectorList.size() == 0) {
					vectorList.add(createVector(vectorName, startDate, endDate, dc, dm));
				}
				if (vectorList.size() > 0 && 
					currVector.numbers.size() == vectorList.get(0).numbers.size()) {
					vectorList.add(createVector(vectorName, startDate, endDate, dc, dm));
				}
			}
		}
		return vectorList;
	}	
		
	public Portfolio diverseHAC (String filename, int clusterSize, double factor, String startDate, double cash, 
		String endDate, String strategyStartDate, String strategyEndDate,
		DataCollection dc, DateModifications dm) throws Exception {
	// method to create + return a diverse portfolio using Hierarchical Agglomerative Clustering
	// assembles list of vectors using helper methods (create Vectors)
	// calls instance of HAC clustering algorithm, reorganizing algorithm for easy access
	// iterates through list of clusters, arbitrarily picks a single stock from each cluster
	// makes sure that all vectors are complete for given trading dates
	// assembles + returns portfolio of arbitrarily selected vectors
		List<String> stocksInPortfolio = new ArrayList<String>();
		List<Double> valuesInPortfolio = new ArrayList<Double>();

		List<Vector> stockVectors = createVectors (filename, startDate, endDate, dc, dm);
		HAC hac = new HAC();
		List<List<Vector>> listOfClusters = hac.assembleClusters(stockVectors, clusterSize, factor);
		List<String> stocksToAdd = new ArrayList<String>();

		for (int count = 0; count < listOfClusters.size(); count++) {
			int sizeOfCluster = listOfClusters.get(count).size();
			double rand = Math.random();
			int randomElementChosen = (int)(rand * (sizeOfCluster - 1));
			Vector stockChosen = listOfClusters.get(count).get(randomElementChosen);
			stocksToAdd.add(stockChosen.name);

		}

		for (int index = 0; index < stocksToAdd.size(); index++) {
			String vectorName = stocksToAdd.get(index);
			if (vectorValidity(vectorName, strategyStartDate, strategyEndDate, dc, dm)) {
				stocksInPortfolio.add(vectorName);
			}
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

	public Portfolio diversekMeans (String filename, int numClusters, String startDate,  
		double cash, String endDate, String strategyStartDate, String strategyEndDate,
		DataCollection dc, DateModifications dm) throws Exception {
	// method to create + return a diverse portfolio using k-Means Clustering
	// assembles list of vectors using helper methods (create Vectors)
	// calls instance of k Means clustering algorithm
	// iterates through list of clusters, arbitrarily picks a single stock from each cluster
	// assembles + returns portfolio of arbitrarily selected vectors

		List<String> stocksInPortfolio = new ArrayList<String>();
		List<Double> valuesInPortfolio = new ArrayList<Double>();

		List<Vector> stockVectors = createVectors (filename, startDate, endDate, dc, dm);
		KMeans kMeans = new KMeans();
		List<List<Vector>> listOfClusters = kMeans.cluster (stockVectors, numClusters);

		List<String> stocksToAdd = new ArrayList<String>();

		for (int count = 0; count < listOfClusters.size(); count++) {
			int sizeOfCluster = listOfClusters.get(count).size();
			double rand = Math.random();
			int randomElementChosen = (int)(rand * (sizeOfCluster - 1));
			Vector stockChosen = listOfClusters.get(count).get(randomElementChosen);
			stocksToAdd.add(stockChosen.name);
		}

		for (int index = 0; index < stocksToAdd.size(); index++) {
			String vectorName = stocksToAdd.get(index);
			if (vectorValidity(vectorName, strategyStartDate, strategyEndDate, dc, dm)) {
				stocksInPortfolio.add(vectorName);
			}
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

	public Portfolio uniformHAC (String filename, int clusterSize, double factor, String startDate, double cash, 
		String endDate, String strategyStartDate, String strategyEndDate,
		DataCollection dc, DateModifications dm) throws Exception {
	// method to create + return a uniform portfolio using Hierarchical Agglomerative Clustering
	// assembles list of vectors using helper methods (create Vectors)
	// calls instance of HAC clustering algorithm, reorganizing algorithm for easy access
	// iterates through list of clusters, arbitrarily picks a single stock from each cluster
	// makes sure that all vectors are complete for given trading dates
	// assembles + returns portfolio of arbitrarily selected vectors
		List<String> stocksInPortfolio = new ArrayList<String>();
		List<Double> valuesInPortfolio = new ArrayList<Double>();

		List<Vector> stockVectors = createVectors (filename, startDate, endDate, dc, dm);
		HAC hac = new HAC();
		List<List<Vector>> listOfClusters = hac.assembleClusters(stockVectors, clusterSize, factor);
		List<String> stocksToAdd = new ArrayList<String>();

		int indexOfChoice = (int)(Math.random() * (listOfClusters.size() - 1));
		List<Vector> clusterOfChoice = listOfClusters.get(indexOfChoice);

		for (int count = 0; count < clusterOfChoice.size(); count ++) {
			Vector stockChosen = clusterOfChoice.get(count);
			stocksToAdd.add(stockChosen.name);
		}

		for (int index = 0; index < stocksToAdd.size(); index++) {
			String vectorName = stocksToAdd.get(index);
			if (vectorValidity(vectorName, strategyStartDate, strategyEndDate, dc, dm)) {
				stocksInPortfolio.add(vectorName);
			}
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

	public Portfolio uniformkMeans (String filename, int numClusters, String startDate,  
		double cash, String endDate, String strategyStartDate, String strategyEndDate,
		DataCollection dc, DateModifications dm) throws Exception {
	// method to create + return a uniform portfolio using K-Means Clustering
	// assembles list of vectors using helper methods (create Vectors)
	// calls instance of HAC clustering algorithm, reorganizing algorithm for easy access
	// chooses arbitrary cluster from list of clusters, assembles + returns portfolio of these

		List<String> stocksInPortfolio = new ArrayList<String>();
		List<Double> valuesInPortfolio = new ArrayList<Double>();

		List<Vector> stockVectors = createVectors (filename, startDate, endDate, dc, dm);
		KMeans kMeans = new KMeans();
		List<List<Vector>> listOfClusters = kMeans.cluster (stockVectors, numClusters);

		List<String> stocksToAdd = new ArrayList<String>();

		int indexOfChoice = (int)(Math.random() * (listOfClusters.size() - 1));
		List<Vector> clusterOfChoice = listOfClusters.get(indexOfChoice);

		for (int count = 0; count < clusterOfChoice.size(); count ++) {
			Vector stockChosen = clusterOfChoice.get(count);
			stocksToAdd.add(stockChosen.name);
		}

		for (int index = 0; index < stocksToAdd.size(); index++) {
			String vectorName = stocksToAdd.get(index);
			if (vectorValidity(vectorName, strategyStartDate, strategyEndDate, dc, dm)) {
				stocksInPortfolio.add(vectorName);
			}
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

	public Portfolio userInputPortfolio(List<String> listOfStocks, List<Double> listOfValues, 
		Double cash, String strategyStartDate, String strategyEndDate, 
		DataCollection dc, DateModifications dm) {
	// Given a user inputted list of stocks they want in portfolio + corresponding number of $$
	// portfolio checks if all stocks are viable, and adds to portfolio
	// for each $$ amount inputted, converts to number of shares available

		List<String> stocksInPortfolio = new ArrayList<String>();
		List<Double> valuesInPortfolio = new ArrayList<Double>();

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
