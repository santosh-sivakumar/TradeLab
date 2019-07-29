import java.util.*;

public class MDAStrategy {

	String name;
	int longRunFrame;
	int shortRunFrame;
	Double volatilePercentage;
	List<Double> initialStockValues;
	
		
	MDAStrategy (String inputName, int inputLongRunFrameFrame, 
		int inputShortRunFrameFrame, double inputVolatilePercentage) {
	// public class to initialize new instance of the MDA (Moving Day Average) Strategy
	// short run frame = number of days in short run for MDA computation
	// long run frame = number of days in long run for MDA computation
	// threshold percentage = range outside of which stocks should be sold/bought
	// volatile percentage = what fraction of stock should be sold when deciding to sell
	
		name = inputName;
		longRunFrame = inputLongRunFrameFrame;
		shortRunFrame = inputShortRunFrameFrame;
		volatilePercentage = inputVolatilePercentage;
		initialStockValues = new ArrayList<Double>();
	}

	public void initialize (Portfolio port, DataCollection dc, String stockDate, DateModifications dm) {
	// public method to initialize instance of MDA strategy on given date
	// if all values not recorded on given date, decrement date using DataCollection method
	// for all stocks in portfolio, retrieve share price on given date + add to list

		while (!dc.checkIfPortfolioDataExists(port, stockDate)) {
			stockDate = dm.decrementDate (stockDate);
		}

		assert (port.stocksInPortfolio.size() > 0) : "Incompatible Portfolio";
		assert (port.valuesInPortfolio.size() > 0) : "Incompatible Portfolio";

		for (int stockIndex = 0; stockIndex < port.stocksInPortfolio.size(); stockIndex++) {
			String stockID = port.stocksInPortfolio.get(stockIndex) + "#" + stockDate;
			initialStockValues.add(dc.dataPoints.get(stockID).close);
		}
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

	private double computeLongRunAverage (Portfolio port, DataCollection dc, int stockIndex, 
		String stockDate, DateModifications dm) {
	// method to compute and return long run average of given stock ending at given date
	// updates count to reach long run time parameter initialized within strategy
	// as date decrements, average updates
		
		double longRunAverage = 0.0;
		String stockName = port.stocksInPortfolio.get(stockIndex);

		for (int count = 0; count <  longRunFrame; count++) {
			while (!dc.checkIfVectorDataExists (stockName, stockDate)) {
				stockDate = dm.decrementDate(stockDate);
			}
			String stockID = stockName + "#" + stockDate;
		
			longRunAverage += (dc.dataPoints.get(stockID).close);
		}

		longRunAverage /= longRunFrame;
		// System.out.print("long run backtracked date = ");
		// System.out.println(stockDate);
		return longRunAverage;
	}
	
	private double computeShortRunAverage (Portfolio port, DataCollection dc, int stockIndex, 
		String stockDate, DateModifications dm) {
	// method to compute and return short run average of given stock ending at given date
	// updates count to reach short run time parameter initialized within strategy
	// as date decrements, average updates

		double shortRunAverage = 0.0;
		String stockName = port.stocksInPortfolio.get(stockIndex);

		for (int count = 0; count <  shortRunFrame; count++) {
			stockDate = dm.decrementDate(stockDate);
			while (!dc.checkIfVectorDataExists (stockName, stockDate)) {
				stockDate = dm.decrementDate(stockDate);
			}
			String stockID = stockName + "#" + stockDate;
		
			shortRunAverage += (dc.dataPoints.get(stockID).close);
		}
		
		shortRunAverage /= shortRunFrame;
		// System.out.print("short run backtracked date = ");
		// System.out.println(stockDate);
		return shortRunAverage;
	}

	private boolean compareAveragesToBuy(double shortRunAverage, double longRunAverage, 
		double yestShortRunAverage, double yestLongRunAverage) {
	// method to compare shortrun/longrun average of given date, as well as previous date
	// if short run crosses up long run in given day, indicates that stock should be bought [true]
	
		if (yestShortRunAverage < yestLongRunAverage && shortRunAverage > longRunAverage) {
			return true;
		}
		return false;
	}
	
	private boolean compareAveragesToSell(double shortRunAverage, double longRunAverage, 
		double yestShortRunAverage, double yestLongRunAverage) {
	// method to compare shortrun/longrun average of given date, as well as previous date
	// if short run crosses down long run in given day, indicates that stock should be sold [true]
	
		if (yestShortRunAverage > yestLongRunAverage && shortRunAverage < longRunAverage) {
			return true;
		}
		return false;
	}
	
	
	private void update (Portfolio port, String stockDate,  DataCollection dc, DateModifications dm) {
	// method to execute one day of trading strategy
	// initializes list of all stocks to be bought on given day (executed after sales)
	// for each stock, computes SR/LR average on given day/previous trading day
	// using comparator helper methods, determines if stock should be bought/sold
	// if buy, add stock to list of stocks to buy. If sell, execute "sell" operation
	// once all sales have been made, compute sum of current $$ invested in all stocks to be bought
	// for each stock in list to buy, execute "buy" operation
	
		List<Integer> indexOfStocksToBuy = new ArrayList<Integer>();
	
		for (int index = 0; index < (port.stocksInPortfolio).size(); index++) {
		
			double shortRunAverage = computeShortRunAverage (port, dc, index, stockDate, dm);
			double longRunAverage = computeLongRunAverage (port, dc, index, stockDate, dm);
			
			String yesterday = dm.decrementDate(stockDate);
			while (dc.checkIfPortfolioDataExists (port, yesterday)) {
				yesterday = dm.decrementDate(yesterday);
			}

			double yestShortRunAverage = computeShortRunAverage (port, dc, index, yesterday, dm);
			double yestLongRunAverage = computeLongRunAverage (port, dc, index, yesterday, dm);

			
			String stockName = port.stocksInPortfolio.get(index);
			String stockID = (stockName + "#" + stockDate);

			if (compareAveragesToBuy(shortRunAverage, longRunAverage, 
				yestShortRunAverage, yestLongRunAverage)) {
				indexOfStocksToBuy.add(index);
			}
			
			if (compareAveragesToSell(shortRunAverage, longRunAverage, 
				yestShortRunAverage, yestLongRunAverage)) {
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
			update (port, currDate, dc, dm);
			currDate = dm.incrementDate(currDate);
			while (!dc.checkIfPortfolioDataExists(port, currDate)) {
				currDate = dm.incrementDate(currDate);
			}
		}
	}

}
	
	
	
	
	
	
	