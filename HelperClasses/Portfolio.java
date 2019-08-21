import java.util.ArrayList;

// public class to create a new portfolio of stocks
// contains list of stock symbols/amount in each stock
// indeces of stock name/amount match in respective lists
// portfolio has cash instance variable, constantly updating value of loose cash to invest
public class Portfolio {

	String name;
	ArrayList<String> stocksInPortfolio = new ArrayList<String>();
	ArrayList<Double> valuesInPortfolio = new ArrayList<Double>();
	Double cash;
	
	Portfolio (String inputName, Double inputCash, ArrayList<String> inputStocksInPortfolio, ArrayList<Double> inputValuesInPortfolio) {

		name = inputName;
		stocksInPortfolio = inputStocksInPortfolio;
		valuesInPortfolio = inputValuesInPortfolio;
		cash = inputCash;
	}

}
