import java.util.*;

public class TestClasses {
// public class with methods to create portfolio/execute trading strategy/print result metrics
// each method calls instance of portfolio maker with specified guidelines for portfolio creation
// computes + returns value of untouched portfolio
// executes specified trading strategy
// computes + returns value of portfolio with trading strategy implemented
	
	public void diverseHACWithThreshold (String filename, int clusterSize, double factor, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double thresholdPercentage, double volatilePercentage) throws Exception {
	//diversified portfolio using hierarchical agglomerative clustering, Threshold trading Strategy

		System.out.print("Portfolio: HAC, diversified, size ");
		System.out.print(clusterSize);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Threshold, TS percentage = ");
		System.out.print(thresholdPercentage);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.diverseHAC(filename, clusterSize, factor, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		ThresholdStrategy ts = new ThresholdStrategy("ts", thresholdPercentage, volatilePercentage);
		ts.initialize (port, dc, dm, startDate);
		ts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void diverseKMeansWithThreshold (String filename, int numClusters, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double thresholdPercentage, double volatilePercentage) throws Exception {
	//diversified portfolio using K-Means clustering, Threshold trading Strategy

		System.out.print("Portfolio: kMeans, diversified, length ");
		System.out.print(numClusters);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Threshold, TS percentage = ");
		System.out.print(thresholdPercentage);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.diversekMeans(filename, numClusters, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		ThresholdStrategy ts = new ThresholdStrategy("ts", thresholdPercentage, volatilePercentage);
		ts.initialize (port, dc, dm, startDate);
		ts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void uniformHACWithThreshold (String filename, int clusterSize, double factor, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double thresholdPercentage, double volatilePercentage) throws Exception {
	//uniform portfolio using hierarchical agglomerative clustering, Threshold trading Strategy

		System.out.print("Portfolio: HAC, diversified, size ");
		System.out.print(clusterSize);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Threshold, TS percentage = ");
		System.out.print(thresholdPercentage);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.uniformHAC(filename, clusterSize, factor, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		ThresholdStrategy ts = new ThresholdStrategy("ts", thresholdPercentage, volatilePercentage);
		ts.initialize (port, dc, dm, startDate);
		ts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void uniformKMeansWithThreshold (String filename, int numClusters, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double thresholdPercentage, double volatilePercentage) throws Exception {
	//uniform portfolio using K-Means clustering, Threshold trading Strategy

		System.out.print("Portfolio: kMeans, uniform, length ");
		System.out.print(numClusters);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Threshold, TS percentage = ");
		System.out.print(thresholdPercentage);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.uniformkMeans(filename, numClusters, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		ThresholdStrategy ts = new ThresholdStrategy("ts", thresholdPercentage, volatilePercentage);
		ts.initialize (port, dc, dm, startDate);
		ts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void diverseHACWithMDA (String filename, int clusterSize, double factor, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, int longRunFrame, int shortRunFrame) throws Exception {
	//diversified portfolio using hierarchical agglomerative clustering, Moving-Day-Average Strategy

		System.out.print("Portfolio: HAC, diversified, length ");
		System.out.print(clusterSize);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: MDA, long-run time frame = ");
		System.out.print(longRunFrame);
		System.out.print(", short-run time frame = ");
		System.out.print(shortRunFrame);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.diverseHAC(filename, clusterSize, factor, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		MDAStrategy mda = new MDAStrategy("mda", longRunFrame, shortRunFrame, volatilePercentage);
		mda.initialize (port, dc, startDate, dm);
		mda.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void diverseKMeansWithMDA (String filename, int numClusters, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, int longRunFrame, int shortRunFrame) throws Exception {
	//diversified portfolio using K-Means clustering, Moving-Day-Average Strategy

		System.out.print("Portfolio: kMeans, diversified, length ");
		System.out.print(numClusters);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: MDA, long-run time frame = ");
		System.out.print(longRunFrame);
		System.out.print(", short-run time frame = ");
		System.out.print(shortRunFrame);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.diversekMeans(filename, numClusters, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		MDAStrategy mda = new MDAStrategy("mda", longRunFrame, shortRunFrame, volatilePercentage);
		mda.initialize (port, dc, startDate, dm);
		mda.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void uniformHACWithMDA (String filename, int clusterSize, double factor, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, int longRunFrame, int shortRunFrame) throws Exception {
	//uniform portfolio using hierarchical agglomerative clustering, Moving-Day-Average Strategy

		System.out.print("Portfolio: HAC, uniform, length ");
		System.out.print(clusterSize);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: MDA, long-run time frame = ");
		System.out.print(longRunFrame);
		System.out.print(", short-run time frame = ");
		System.out.print(shortRunFrame);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.uniformHAC(filename, clusterSize, factor, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		MDAStrategy mda = new MDAStrategy("mda", longRunFrame, shortRunFrame, volatilePercentage);
		mda.initialize (port, dc, startDate, dm);
		mda.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void uniformKMeansWithMDA (String filename, int numClusters, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, int longRunFrame, int shortRunFrame) throws Exception {
	//uniform portfolio using K-Means clustering, Moving-Day-Average Strategy

		System.out.print("Portfolio: kMeans, uniform, length ");
		System.out.print(numClusters);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: MDA, long-run time frame = ");
		System.out.print(longRunFrame);
		System.out.print(", short-run time frame = ");
		System.out.print(shortRunFrame);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.uniformkMeans(filename, numClusters, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		MDAStrategy mda = new MDAStrategy("mda", longRunFrame, shortRunFrame, volatilePercentage);
		mda.initialize (port, dc, startDate, dm);
		mda.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void diverseHACWithCluster (String filename, int clusterSize, double factor, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, double thresholdNumSD) throws Exception {
	//diversified portfolio using hierarchical agglomerative clustering
	//generalized Pairs-Trading Strategy for cluster-based portfolio

		System.out.print("Portfolio: HAC, diversified, length ");
		System.out.print(clusterSize);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Cluster, threshold number of SD = ");
		System.out.print(thresholdNumSD);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.diverseHAC(filename, clusterSize, factor, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		ClusterTradingStrategy cts = new ClusterTradingStrategy("cts", thresholdNumSD, volatilePercentage);
		cts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void diverseKMeansWithCluster (String filename, int numClusters, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, double thresholdNumSD) throws Exception {
	//diversified portfolio using K-Means clustering
	//generalized Pairs-Trading Strategy for cluster-based portfolio

		System.out.print("Portfolio: kMeans, diversified, length ");
		System.out.print(numClusters);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Cluster, threshold number of SD = ");
		System.out.print(thresholdNumSD);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.diversekMeans(filename, numClusters, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		ClusterTradingStrategy cts = new ClusterTradingStrategy("cts", thresholdNumSD, volatilePercentage);
		cts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void uniformHACWithCluster (String filename, int clusterSize, double factor, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, double thresholdNumSD) throws Exception {
	//uniform portfolio using hierarchical agglomerative clustering
	//generalized Pairs-Trading Strategy for cluster-based portfolio

		System.out.print("Portfolio: HAC, uniform, length ");
		System.out.print(clusterSize);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Cluster, threshold number of SD = ");
		System.out.print(thresholdNumSD);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.uniformHAC(filename, clusterSize, factor, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		ClusterTradingStrategy cts = new ClusterTradingStrategy("cts", thresholdNumSD, volatilePercentage);
		cts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void uniformKMeansWithCluster (String filename, int numClusters, String startDate, 
		String endDate, String strategyStartDate, String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, double thresholdNumSD) throws Exception {
	//uniform portfolio using K-Means clustering
	//generalized Pairs-Trading Strategy for cluster-based portfolio

		System.out.print("Portfolio: kMeans, uniform, length ");
		System.out.print(numClusters);
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Cluster, threshold number of SD = ");
		System.out.print(thresholdNumSD);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.uniformkMeans(filename, numClusters, startDate, cash, 
			endDate, strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.println(".");

		ClusterTradingStrategy cts = new ClusterTradingStrategy("cts", thresholdNumSD, volatilePercentage);
		cts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.println(".");
		System.out.println("------------------------");
	}

	public void userInputWithThreshold (String filename, List<String> listOfStocks, 
		List<Double> listOfValues, String strategyStartDate, 
		String strategyEndDate, double cash, int granularity, 
		double thresholdPercentage, double volatilePercentage) throws Exception {
	//user-inputted portfolio, threshold trading strategy

		System.out.print("Portfolio: user input");
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Threshold, TS percentage = ");
		System.out.print(thresholdPercentage);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.userInputPortfolio(listOfStocks, listOfValues, cash, 
			strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.print(", and the final amount of cash you hold is");
		System.out.println(port.cash);
		System.out.println("------------------------");

		ThresholdStrategy ts = new ThresholdStrategy("ts", thresholdPercentage, volatilePercentage);
		ts.initialize (port, dc, dm, strategyStartDate);
		ts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.print(", and the final amount of cash you hold is");
		System.out.println(port.cash);
		System.out.println("------------------------");
	}

	public void userInputWithMDA (String filename, List<String> listOfStocks, 
		List<Double> listOfValues, String strategyStartDate, 
		String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, int longRunFrame, int shortRunFrame) throws Exception {
	//user-inputted portfolio, Moving-Day-Average strategy

		System.out.print("Portfolio: user input");
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: MDA, long-run time frame = ");
		System.out.print(longRunFrame);
		System.out.print(", short-run time frame = ");
		System.out.print(shortRunFrame);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.userInputPortfolio(listOfStocks, listOfValues, cash, 
			strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.print(", and the final amount of cash you hold is");
		System.out.println(port.cash);
		System.out.println("------------------------");

		MDAStrategy mda = new MDAStrategy("mda", longRunFrame, shortRunFrame, volatilePercentage);
		mda.initialize (port, dc, strategyStartDate, dm);
		mda.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.print(", and the final amount of cash you hold is");
		System.out.println(port.cash);
		System.out.println("------------------------");
	}

	public void userInputWithCluster (String filename, List<String> listOfStocks, 
		List<Double> listOfValues, String strategyStartDate, 
		String strategyEndDate, double cash, int granularity, 
		double volatilePercentage, double thresholdNumSD) throws Exception {
	//user-inputted portfolio, generalized Pairs-Trading Strategy for cluster-based portfolio
		System.out.print("Portfolio: user input");
		System.out.print(", cash invested: $");
		System.out.print(cash);
		System.out.println(".");
		System.out.print("Strategy: Cluster, threshold number of SD = ");
		System.out.print(thresholdNumSD);
		System.out.print(", Volatile Percentage = ");
		System.out.print(volatilePercentage);
		System.out.println(".");

		DataCollection dc = new DataCollection ("dc");
		dc.readDataFromFile (filename);

		DateModifications dm = new DateModifications ();

		PortfolioMaker pm = new PortfolioMaker (granularity);
		Portfolio port = pm.userInputPortfolio(listOfStocks, listOfValues, cash, 
			strategyStartDate, strategyEndDate, dc, dm);
		System.out.print("Stocks in Portfolio: ");
		System.out.println(port.stocksInPortfolio);

		double initialValue = dc.valueOfPortfolio (port, strategyStartDate, strategyEndDate, dm);
		System.out.print("If the Portfolio was untouched, the final value of the Portfolio would be $");
		System.out.print(initialValue);
		System.out.print(", and the final amount of cash you hold is");
		System.out.println(port.cash);
		System.out.println("------------------------");

		ClusterTradingStrategy cts = new ClusterTradingStrategy("cts", thresholdNumSD, volatilePercentage);
		cts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, strategyEndDate, dm);
		System.out.print("Using Strategy, the final value of the Portfolio is $");
		System.out.print(finalValue);
		System.out.print(", and the final amount of cash you hold is");
		System.out.println(port.cash);
		System.out.println("------------------------");
	}



}