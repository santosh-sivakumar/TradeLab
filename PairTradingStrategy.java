import java.util.*;
import java.io.*;

public class PairTradingStrategy {

	Double avgRatio;
	Double thresholdPercentage;
	Double volatilePercentage;
	
	PairTradingStrategy (String name, double thresholdPercentage, double volatilePercentage) {
	// public class to initialize new instance of the Pair Trading Strategy
	// threshold percentage = range outside of which stocks should be sold/bought
	// volatile percentage = what fraction of stock should be sold when deciding to sell
		avgRatio = 0.0;
		thresholdPercentage = thresholdPercentage;
		volatilePercentage = volatilePercentage;
	}

	private void initialize (Portfolio port, DataCollection dc, DateModifications dm,
		String startDate, String endDate) {
	// public method to initialize portfolio values on given date
	// if all values not recorded on end date, decrement date using DataCollection method
	// if all values not recorded on start date, increment date using DataCollection method
	// compute ratio for all days between start date/end date, calculate average ratio of pair

		assert (port.stocksInPortfolio.size() == 2) : "Incompatible for Pairs Trading";
		assert (port.valuesInPortfolio.size() == 2) : "Incompatible for Pairs Trading";

		String currentDate = startDate;
		
		while (!dc.checkIfPortfolioDataExists(port, endDate)) {
			endDate = dm.decrementDate(endDate);
		}
		
		avgRatio = 0.0;
		int numRatios = 0;


		while (!currentDate.equals(endDate)) {

			while (!dc.checkIfPortfolioDataExists(port, currentDate)) {
				currentDate = dm.incrementDate(currentDate);
			}	
			
			String num = (port.stocksInPortfolio.get(0) + "#" + currentDate);
			String denom = (port.valuesInPortfolio.get(1) + "#" + currentDate);
			avgRatio += dc.dataPoints.get(num).close / dc.dataPoints.get(denom).close;

			currentDate = dm.incrementDate (currentDate);	
		}

		avgRatio /= numRatios;		
	}
	
	private void rebalancePortfolio (Portfolio port, int stockToBuy, int stockToSell) {
	// if called, method will sell shares in one stock and buy shares in other (basic Pair Strategy)
	// determine $$ to sell using amount in stock to sell/volatile percentage
	// execute "sale" by holding $$ from sale, subtracting $$ from amount in sell Stock
	// execute "buy" by adding $$ held to buy Stock
	
		double amountInStockToSell = (port.valuesInPortfolio).get(stockToSell);
		double dollarsVolatile = (amountInStockToSell * volatilePercentage);
		double amountAfterSale = (port.valuesInPortfolio).get(stockToSell) - dollarsVolatile;
		(port.valuesInPortfolio).set(stockToSell, amountAfterSale);
		
		
		double amountAfterBuy = (port.valuesInPortfolio).get(stockToBuy) + dollarsVolatile;
		(port.valuesInPortfolio).set(stockToBuy, amountAfterBuy);
	}

	private void update (Portfolio port, DataCollection dc, String date) {
	// method called on each trading day
	// compute values in numerator/denominator of ratio - both stocks in pair, compute ratio
	// determine if ratio exists on either side of threshold, call rebalance method correspondingly
		
		String num = (port.stocksInPortfolio.get(0) + "#" + date);
		String denom = (port.stocksInPortfolio.get(1) + "#" + date);
		Value numerator = dc.dataPoints.get(num);
		Value denominator = dc.dataPoints.get(denom);
		double currRatio = numerator.close/denominator.close;

		if (currRatio > (avgRatio * (1.0 + thresholdPercentage))) {
			rebalancePortfolio (port, 1, 0);
		}
		else if (currRatio < (avgRatio * (1.0 - thresholdPercentage))) {
			rebalancePortfolio (port, 0, 1);
		}
	}
	
	public void allUpdates (Portfolio port, DataCollection dc, DateModifications dm,
		String startDate, String endDate) {
	// main method to execute all updates between given start/end dates
	// checks if all stocks in portfolio recorded on end date, decrements correspondingly
	// for each trading day, checks if all stocks recorded + increments as necessary
	// calls update method on portfolio, subsequently increments date for next day

		String currentDate = startDate;

		while (!dc.checkIfPortfolioDataExists(port,endDate)) {
			endDate = dm.decrementDate(endDate);
		}

		while (!currentDate.equals(endDate)) {

			while (!dc.checkIfPortfolioDataExists(port, currentDate)) {
				currentDate = dm.incrementDate(currentDate);
			}	
			update (port, dc, currentDate);
			currentDate = dm.incrementDate(currentDate);	
		}
	}

}
	
	
	
	
	
	
	
	
	