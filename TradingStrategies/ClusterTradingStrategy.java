import java.util.ArrayList;

// public class to initialize new instance of the Cluster Trading Strategy
// initialized cluster share price % change average
// threshold percentage = range outside of which stocks should be sold/bought
// volatile percentage = what fraction of stock should be sold when deciding to sell	
public class ClusterTradingStrategy {

	String name;
	Double clusterAvg;
	Double thresholdNumSD;
	Double volatilePercentage;
	
	ClusterTradingStrategy (String inputName, Double inputThresholdNumSD, Double inputVolatilePercentage) {

		name = inputName;
		clusterAvg = 0.0;
		thresholdNumSD = inputThresholdNumSD;
		volatilePercentage = inputVolatilePercentage;	
	}

	// method computes percentage change in a given stock for a given day
	// generates previous trading date from decrement method in dc object
	// retrieves stock's share price on current, previous days
	// compures + returns percent change (diff in share prices/original share price)
	private double computePercentChange (String stockName, String stockDate, DataCollection dc, DateModifications dm) {

		assert (dc.checkIfVectorDataExists(stockName, stockDate)) : "Incompatible date provided";

		String previousDate = dm.decrementDate(stockDate);

		while (!(dc.checkIfVectorDataExists(stockName, previousDate))){
			previousDate = dm.decrementDate(previousDate);
		}
		
		String stockID = stockName + "#" + stockDate;
		String stockIDPrevious = stockName + "#" + previousDate;
		double change = (dc.dataPoints.get(stockID).close - dc.dataPoints.get(stockIDPrevious).close);
		double percentChange = ((change / dc.dataPoints.get(stockIDPrevious).close) * 100);

		return percentChange;
	}

	// method iterates through portfolio ArrayList of stocks + computes average % change on given day
	// for each stock in portfolio, computes percent change by calling method
	// continually updates mean, returns at end
	private double computeMean(Portfolio port, DataCollection dc, String stockDate, DateModifications dm) {

		double mean = 0.0;

		for (int index = 0; index < port.stocksInPortfolio.size(); index++) {
			mean += computePercentChange (port.stocksInPortfolio.get(index), stockDate, dc, dm);
		}

		mean /= port.stocksInPortfolio.size();
		return mean;
	}

	// method iterates through portfolio ArrayList of stocks + computes SD of % change on given day
	// for each stock in portfolio, computes percent change by calling method
	// continually updates SD (calculating using mean), returns at end
	private double computeStandardDev (Portfolio port, DataCollection dc, String stockDate, DateModifications dm, double mean) {

		double standardDev = 0.0;

		for (int index = 0; index < port.stocksInPortfolio.size(); index++) {
			double percentChange = computePercentChange (port.stocksInPortfolio.get(index), stockDate, dc, dm);
			standardDev += ((percentChange - mean) * (percentChange - mean));
		}

		standardDev = Math.sqrt(standardDev/port.stocksInPortfolio.size());
		return standardDev;
	}

	// method computes + returns zScore for a given stock's share price on a given day
	private double computeZScore (double stockPrice, double mean, double standardDev) {

		double zScore = ((stockPrice - mean)/standardDev);
		return zScore;
	}

	// method sells shares of stock at given index
	// determines current amount invested in stock, computes amount to sell using volatile %
	// sell: add dollars made to cash holdings, subtract $$ from amount invested in stock
	private void rebalancePortfolioSell (Portfolio port, int stockIndex, String stockID, DataCollection dc) {

		double currNumShares = (port.valuesInPortfolio).get(stockIndex);
		double currPriceOfShare = dc.dataPoints.get(stockID).close;
		double numSharesToSell = currNumShares * (0.01 * volatilePercentage);
		double dollarsToMake = currPriceOfShare * numSharesToSell;
		port.cash += dollarsToMake;
		double updatedNumShares = (port.valuesInPortfolio).get(stockIndex) - numSharesToSell;
		(port.valuesInPortfolio).set(stockIndex, updatedNumShares);
	}

	// method sells shares of stock at given index
	// sum = total amount invested among all stocks which will be bought in this trading day
	// computes percentage of sum account for by given stock (stock Proportion)
	// if amount of loose cash exceeds cash threshold, execute "buy operation"
	// buy: subtract dollars spent from cash holdings, add $$ to amount invested in stock
	private void rebalancePortfolioBuy (double numStocksToBuy, Portfolio port, int stockIndex, String stockID, DataCollection dc) {

		double CASH_THRESHOLD = 10.0;
		double stockFraction = (1.0 / numStocksToBuy);
		double dollarsToSpend = port.cash * stockFraction;
		
		if (dollarsToSpend > CASH_THRESHOLD) {
			double currNumShares = (port.valuesInPortfolio).get(stockIndex);
			double currPriceOfShare = dc.dataPoints.get(stockID).close;
			double numSharesToBuy = dollarsToSpend / currPriceOfShare;
			port.cash -= dollarsToSpend;
			double updatedNumShares = (port.valuesInPortfolio).get(stockIndex) + numSharesToBuy;
			(port.valuesInPortfolio).set(stockIndex, updatedNumShares);
		}
	}

	// method to execute one day of trading strategy
	// initializes ArrayList of all stocks to be bought on given day (executed after sales)
	// iterates through stocks in portfolio, assembles ID using stock name + given date
	// determines whether given date's zScore exceeds/falls short of threshold limit
	// if below, add stock to ArrayList of stocks to buy. If above, execute "sell" operation
	// once all sales have been made, compute sum of current $$ invested in all stocks to be bought
	// for each stock in ArrayList to buy, execute "buy" operation
	private void update (Portfolio port, DataCollection dc, String stockDate, DateModifications dm) {

		double mean = computeMean (port, dc, stockDate, dm);
		double standardDev = computeStandardDev (port, dc, stockDate, dm, mean);

		ArrayList<Integer> indexOfStocksToBuy = new ArrayList<Integer>();
	
		for (int index = 0; index < port.stocksInPortfolio.size(); index++) {
			String stockName = (port.stocksInPortfolio).get(index);
			String stockID = (stockName + "#" + stockDate);
			double stockPrice = dc.dataPoints.get(stockID).close;

			if (computeZScore(stockPrice, mean, standardDev) < (thresholdNumSD * -1)) {
				indexOfStocksToBuy.add(index);
			}
			
			if (computeZScore(stockPrice, mean, standardDev) > thresholdNumSD) {
				rebalancePortfolioSell(port, index, stockID, dc);
			}
		}
		
		double numSharesToBuy = indexOfStocksToBuy.size();

		for (int index = 0; index < (indexOfStocksToBuy).size(); index++) {
			int portfolioIndex = indexOfStocksToBuy.get(index);
			String stockName = (port.stocksInPortfolio).get(portfolioIndex);
			String stockID = (stockName + "#" + stockDate);
			rebalancePortfolioBuy(numSharesToBuy, port, portfolioIndex, stockID, dc);
		}
	}

	// method to execute trading strategy daily within a certain time period
	// calls dataCollection increment date method until all stocks in portfolio are recorded
	// executes trading strategy on given date (update method), updates date
	// for each date update, method checks to ensure portfolio contains all stocks
	public void allUpdates (Portfolio port, DataCollection dc, DateModifications dm, String startDate, String endDate) {
	
		while (!dc.checkIfPortfolioDataExists(port, startDate)) {
			startDate = dm.incrementDate(startDate);
		}
		while (!dc.checkIfPortfolioDataExists(port, endDate)) {
			endDate = dm.incrementDate(endDate);
		}

		String currDate = startDate;

		while (!currDate.equals(endDate)) {
			update (port, dc, currDate, dm);
			currDate = dm.incrementDate(currDate);
			while (!dc.checkIfPortfolioDataExists(port, currDate)) {
				currDate = dm.incrementDate(currDate);
			}
		}
	}

}
