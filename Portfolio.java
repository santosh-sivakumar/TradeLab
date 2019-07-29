import java.util.*;

public class Portfolio {

	String name;
	List<String> stocksInPortfolio = new ArrayList<String>();
	List<Double> valuesInPortfolio = new ArrayList<Double>();
	Double cash;
	
	Portfolio (String inputName, Double inputCash, 
		List<String> inputStocksInPortfolio, List<Double> inputValuesInPortfolio) {
	// public class to create a new portfolio of stocks
	// contains list of stock symbols/amount in each stock
	// indeces of stock name/amount match in respective lists
	// portfolio has cash instance variable, constantly updating value of loose cash to invest
		name = inputName;
		stocksInPortfolio = inputStocksInPortfolio;
		valuesInPortfolio = inputValuesInPortfolio;
		cash = inputCash;
	}

}
