import java.util.*;

public class ThresholdStrategy {

	String name;
	Double thresholdPercentage;
	Double volatilePercentage;
	List<Double> initialStockValues;
		
	ThresholdStrategy (String inputName, double inputThresholdPercentage, double inputVolatilePercentage) {
	// public class to initialize new instance of the Threshold Strategy
	// threshold percentage = range outside of which stocks should be sold/bought
	// volatile percentage = what fraction of stock should be sold when deciding to sell

		name = inputName;
		thresholdPercentage = inputThresholdPercentage;
		volatilePercentage = inputVolatilePercentage;
		initialStockValues = new ArrayList<Double>();
	}

	public void initialize (Portfolio port, DataCollection dc, DateModifications dm, String date) {
	// public method to initialize instance of Threshold Strategy on given date
	// if all values not recorded on given date, decrement date using DataCollection method
	// for all stocks in portfolio, retrieve share price on given date + add to list

		while (!dc.checkIfPortfolioDataExists(port, date)) {
			date = dm.incrementDate(date);
		}

		assert (volatilePercentage <= 1.00) : "Incompatible volatile percentage";

		assert (port.stocksInPortfolio.size() > 0) : "Incompatible Portfolio";
		assert (port.valuesInPortfolio.size() > 0) : "Incompatible Portfolio";

		for (int index = 0; index < port.stocksInPortfolio.size(); index++) {
			String stockID = port.stocksInPortfolio.get(index) + "#" + date;
			initialStockValues.add(dc.dataPoints.get(stockID).close);
		}
	}

	private boolean aboveThreshold (Portfolio port, int stockIndex, Value sharePrice) {
	// method to determine if stock price on given date is above threshold
	// retrieves center value of stock + multiplies by threshold factor
	// if share price on given day above upper threshold, return false. Else, true
	
		double upperThreshold = (1.0 + thresholdPercentage);
		double upperLimit = (initialStockValues.get(stockIndex) * (upperThreshold));
	
		if (sharePrice.close > upperLimit) {
			return true;
		}
		return false;
	}
	
	private boolean belowThreshold (Portfolio port, int stockIndex, Value sharePrice) {
	// method to determine if stock price on given date is below threshold
	// retrieves center value of stock + multiplies by threshold factor (subtracting)
	// if share price on given day below lower threshold, return false. Else, true

			
		double lowerThreshold = (1.0 - thresholdPercentage);
		double lowerLimit = (initialStockValues.get(stockIndex) * (lowerThreshold));

		
		if (sharePrice.close < (lowerLimit)) {
			return true;
		}
		return false;
	}
	
	private void rebalancePortfolioSell (Portfolio port, int stockIndex, String stockID, DataCollection dc) {
	// method sells shares of stock at given index
	// determines current amount invested in stock, computes amount to sell using volatile %
	// sell: add dollars made to cash holdings, subtract $$ from amount invested in stock
		
		double currNumShares = (port.valuesInPortfolio).get(stockIndex);
		double currPriceOfShare = dc.dataPoints.get(stockID).close;
		double numSharesToSell = currNumShares * volatilePercentage;
		double dollarsToMake = currPriceOfShare * numSharesToSell;
		port.cash += dollarsToMake;
		double updatedNumShares = (port.valuesInPortfolio).get(stockIndex) - numSharesToSell;
		(port.valuesInPortfolio).set(stockIndex, updatedNumShares);
	}
	
	private void rebalancePortfolioBuy (double numStocksToBuy, Portfolio port, int stockIndex, 
		String stockID, DataCollection dc) {
	// method sells shares of stock at given index
	// sum = total amount invested among all stocks which will be bought in this trading day
	// computes percentage of sum account for by given stock (stock Proportion)
	// if amount of loose cash exceeds cash threshold, execute "buy operation"
	// buy: subtract dollars spent from cash holdings, add $$ to amount invested in stock

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

	private void update (Portfolio port, String currDate, DataCollection dc) {
	// method to execute one day of trading strategy
	// initializes list of all stocks to be bought on given day (executed after sales)
	// iterates through stocks in portfolio, assembles ID using stock name + given date
	// determines whether given date's share price exceeds/falls short of threshold limit
	// if below, add stock to list of stocks to buy. If above, execute "sell" operation
	// once all sales have been made, compute sum of current $$ invested in all stocks to be bought
	// for each stock in list to buy, execute "buy" operation
	
		List<Integer> indexOfStocksToBuy = new ArrayList<Integer>();
	
		for (int index = 0; index < (port.stocksInPortfolio).size(); index++) {
			String stockName = (port.stocksInPortfolio).get(index);
			String stockID = (stockName + "#" + currDate);

			if (belowThreshold(port, index, dc.dataPoints.get(stockID))) {
				indexOfStocksToBuy.add(index);
			}
			
			if (aboveThreshold(port, index, dc.dataPoints.get(stockID))) {
				rebalancePortfolioSell(port, index, stockID, dc);
			}
		}
		
		double numSharesToBuy = indexOfStocksToBuy.size();

		for (int index = 0; index < (indexOfStocksToBuy).size(); index++) {
			int portfolioIndex = indexOfStocksToBuy.get(index);
			String stockName = (port.stocksInPortfolio).get(portfolioIndex);
			String stockID = (stockName + "#" + currDate);
			rebalancePortfolioBuy(numSharesToBuy, port, portfolioIndex, stockID, dc);
		}
	}

	public void allUpdates (Portfolio port, DataCollection dc, DateModifications dm,
	 String startDate, String endDate) {
	// method to execute trading strategy daily within a certain time period
	// calls dataCollection increment date method until all stocks in portfolio are recorded
	// executes trading strategy on given date (update method), updates date
	// for each date update, method checks to ensure portfolio contains all stocks
	
		while (!dc.checkIfPortfolioDataExists(port, startDate)) {
			startDate = dm.incrementDate(startDate);
		}
		while (!dc.checkIfPortfolioDataExists(port, endDate)) {
			endDate = dm.decrementDate(endDate);
		}
		String currDate = startDate;

		while (!currDate.equals(endDate)) {
			update (port, currDate, dc);
			currDate = dm.incrementDate(currDate);
			while (!dc.checkIfPortfolioDataExists(port, currDate)) {
				currDate = dm.incrementDate(currDate);
			}
		}
	}
}
			
			
