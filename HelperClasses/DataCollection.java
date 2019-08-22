import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

// public class to read data + make necessary modifications
// dataPoints HashMap records share prices for all stocks in file every day recorded
public class DataCollection {

	String name;
	HashMap<String,Integer> tradingDays;
	HashMap<String, Value> dataPoints;

	DataCollection (String inputName) {

		tradingDays = new HashMap<String,Integer>();
		dataPoints = new HashMap<String, Value>();
		name = inputName;
	}

	// given an input file, reads through file line-by-line using Buffered Reader
	// for each line, implements a series of splits + assertions to parse stock info
	// each new line contains stock name, date, and share price on specified date
	// method creates new Value object for each name-date-shareinfo set
	// adds to dataPoints HashMap, keys are stock ID (name/date), values are share pricing.        
	public void readData (String stockFile, String tradingDayFile) {

		File stockInput = new File (stockFile);
		assert (stockInput.exists()): "File not found";
   		
   		try {
		
			BufferedReader stockReader = new BufferedReader(new FileReader(stockInput)); 
  			String line; 

  			while ((line = stockReader.readLine()) != null) {
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

    		stockReader.close();

    	} catch (Exception e) {
    		System.out.println("File not compatible");
    	}
    	File tradingDayInput = new File (tradingDayFile);
    	assert (tradingDayInput.exists()): "File not found";

    	try {
		
			BufferedReader dayReader = new BufferedReader(new FileReader(tradingDayInput)); 
  			String line; 

  			while ((line = dayReader.readLine()) != null) {
	    		tradingDays.put(line, 1);	
    		}

    		dayReader.close();

    	} catch (Exception e) {
    		System.out.println("File not compatible");
    	}
	}
	
	// creates and maintains a HashMap of vector names [arbitrary keys]
	// purpose of HashMap - prevent duplicate stock names from being added
	// given an input file, reads through file line-by-line using Buffered Reader
	// for each line, implements a series of splits + assertions to parse stock name
	// checks if stock is present, if not adds to list of vector names/HashMap
	// method returns list of unique stock names
	public ArrayList<String> vectorNamesList (String filename) {
	
		HashMap<String, Integer> vectorsAdded = new HashMap<String,Integer>();
		ArrayList<String> vectors = new ArrayList<String>();
		
		try {

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

				if (vectorsAdded.get(stockName) == null) {
	    			vectors.add(stockName);
	    			vectorsAdded.put(stockName, 1);
	    		}
	    	}

	    	br.close();

    	} catch (Exception e) {
    		System.out.println("File can't be parsed");
    	}
    	return vectors;
    }

	// private method to assemble a stock ID from stock name/date
	// checks dataPoints HashMap for stock ID on given price
	// if share price was recorded on given date (for given stock), method returns true
	public boolean checkIfVectorDataExists (String stockName, String stockDate) {
	
		String stockID = (stockName + "#" + stockDate);
		return dataPoints.containsKey(stockID);
	}
	
	// private method to check if all stocks in a portfolio were recorded on given date
	// iterates through portfolio, calls checkIfVectorDataExists on each stock
	// if all stocks recorded, method returns true. Else, returns false
	public boolean checkIfPortfolioDataExists (Portfolio port, String stockDate) {
		
		for (int i = 0; i < port.stocksInPortfolio.size(); i++) {
			String stockName = port.stocksInPortfolio.get(i);
			if (!checkIfVectorDataExists(stockName, stockDate)) {
				return false;
			}
		}
		return true;	
	}

	// method to compute value of Portfolio at a given date using specific share prices
	// given portfolio, date of initialization (sharesDate) + data of share prices (priceDate)
	// initializes value of portfolio
	// decrements dates as necessary to ensure share values exist (were recorded)
	// computes number of shares for each stock through numSharesList method
	// determines share price on priceDate through dataPoints + index of stock
	// computes + returns value of Portfolio by summing value of numShares * share price
	public double valueOfPortfolio (Portfolio port, String priceDate, DateModifications dm) {

		double valueOfPortfolio = 0.0;

		while (!checkIfPortfolioDataExists (port, priceDate)) {
			priceDate = dm.decrementDate (priceDate);	
		}

		for (int i = 0; i < port.stocksInPortfolio.size(); i++) {
			String stockName = port.stocksInPortfolio.get(i);
			String stockID = stockName + "#" + priceDate;
			valueOfPortfolio += (dataPoints.get(stockID).close * port.valuesInPortfolio.get(i));
		}

		return valueOfPortfolio;
	}
			
}	
