import java.util.*;
import java.io.*;

public class DataCollection {

	String name;
	Map<String, Value> dataPoints;
	
	DataCollection (String inputName) {
	// public class to read data + make necessary modifications
	// dataPoints HashMap records share prices for all stocks in file every day recorded
		dataPoints = new HashMap<String, Value>();
		name = inputName;
	}
	
	public void readDataFromFile (String filename) throws Exception {
	// given an input file, reads through file line-by-line using Buffered Reader
	// for each line, implements a series of splits + assertions to parse stock info
	// each new line contains stock name, date, and share price on specified date
	// method creates new Value object for each name-date-shareinfo set
	// adds to dataPoints HashMap, keys are stock ID (name/date), values are share pricing
		
		File input = new File (filename);
		BufferedReader br = new BufferedReader(new FileReader(input)); 
  		String line; 

  		while ((line = br.readLine()) != null) {
			String[] stock = new String[2];
  			stock = line.split(":");
  			assert (stock.length == 2) : "Incompatible File Line";
  			String nameDetails = stock[0];
    		String [] stockNameArray = nameDetails.split("\\.");
    		assert (stockNameArray.length > 0) : "Incompatible File Line";
    		String stockName = stockNameArray[0];
    		String [] dataValues = stock[1].split(",");
    		assert (dataValues.length > 5) : "Incompatible File Line";
    		String stockDate = dataValues[0];
    		double openValue = Double.parseDouble(dataValues[1]);
    		double closeValue = Double.parseDouble(dataValues[4]);
    		double volumeValue = Double.parseDouble(dataValues[5]);
    		String stockID = stockName + "#" + stockDate;

    		Value curr = new Value (openValue, closeValue, volumeValue);
    		dataPoints.put(stockID, curr);	
    	}
	}
	
	public List<String> vectorNamesList (String filename) throws Exception {
	// creates and maintains a HashMap of vector names [arbitrary keys]
	// purpose of HashMap - prevent duplicate stock names from being added
	// given an input file, reads through file line-by-line using Buffered Reader
	// for each line, implements a series of splits + assertions to parse stock name
	// checks if stock is present, if not adds to list of vector names/HashMap
	// method returns list of unique stock names
	
		Map<String, Integer> vectorsAdded = new HashMap<String,Integer>();
		
		List<String> vectors = new ArrayList<String>();

		File input = new File (filename);
		BufferedReader br = new BufferedReader(new FileReader(input)); 
  		String line; 

  		while ((line = br.readLine()) != null) {
			String[] stock = new String[2];
  			stock = line.split(":");
  			assert (stock.length == 2) : "Incompatible File Line";
  			String nameDetails = stock[0];
    		String [] stockNameArray = nameDetails.split("\\.");
    		assert (stockNameArray.length > 0) : "Incompatible File Line";
    		String stockName = stockNameArray[0];

			if (!vectorsAdded.containsKey(stockName)) {
    			vectors.add(stockName);
    			vectorsAdded.put(stockName, 1);
    		}
    		
    	}
    	return vectors;
    }

	public boolean checkIfVectorDataExists (String stockName, String stockDate) {
	// private method to assemble a stock ID from stock name/date
	// checks dataPoints HashMap for stock ID on given price
	// if share price was recorded on given date (for given stock), method returns true
	
		String stockID = (stockName + "#" + stockDate);
		if (dataPoints.containsKey(stockID)) {
			return true;
		}
		return false;
	}

	public boolean checkIfPortfolioDataExists (Portfolio port, String stockDate) {
	// private method to check if all stocks in a portfolio were recorded on given date
	// iterates through portfolio, calls checkIfVectorDataExists on each stock
	// if all stocks recorded, method returns true. Else, returns false
		for (int index = 0; index < port.stocksInPortfolio.size(); index++) {
			String stockName = port.stocksInPortfolio.get(index);
			if (!checkIfVectorDataExists(stockName, stockDate)) {
				return false;
			}
		}
		return true;	
	}

	public double valueOfPortfolio (Portfolio port, String sharesDate, 
		String priceDate, DateModifications dm) {
	// method to compute value of Portfolio at a given date using specific share prices
	// given portfolio, date of initialization (sharesDate) + data of share prices (priceDate)
	// initializes value of portfolio
	// decrements dates as necessary to ensure share values exist (were recorded)
	// computes number of shares for each stock through numSharesList method
	// determines share price on priceDate through dataPoints + index of stock
	// computes + returns value of Portfolio by summing value of numShares * share price

		double valueOfPortfolio = 0.0;

		while (!checkIfPortfolioDataExists (port, priceDate)) {
			priceDate = dm.decrementDate (priceDate);	
		}

		for (int index = 0; index < port.stocksInPortfolio.size(); index++) {
			String stockName = port.stocksInPortfolio.get(index);
			String stockID = stockName + "#" + priceDate;
			valueOfPortfolio += (dataPoints.get(stockID).close * port.valuesInPortfolio.get(index));

		}

		return valueOfPortfolio;
	}
			
}	
 		
