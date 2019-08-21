// public class with methods to execute trading strategy/return result metrics
// each method executes specified trading strategy
// computes + returns value of portfolio with trading strategy implemented
public class TestClasses {
	
	//execute MeanReversion Strategy
	public double [] executeMeanReversion (Portfolio port, String strategyStartDate, String strategyEndDate, double thresholdPercentage, double volatilePercentage, DataCollection dc, DateModifications dm){


		double [] results = new double [2];
		double initialValue = dc.valueOfPortfolio (port, strategyEndDate, dm);
		results[0] = initialValue;
		
		try {
			MeanReversionStrategy mrs = new MeanReversionStrategy("mrs", thresholdPercentage, volatilePercentage);
			mrs.initialize (port, dc, dm, strategyStartDate);
			mrs.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);
		}
		catch (Exception e) {
			System.out.println("Strategy could not be executed");
		}

		
		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, dm);
		results[1] = finalValue;

		return results;
	}
	
	//execute Moving-Day-Average Strategy on given portfolio
	public double [] executeMDA (Portfolio port, String strategyStartDate, String strategyEndDate, double volatilePercentage, int longRunFrame, int shortRunFrame, DataCollection dc, DateModifications dm) {
		
		double [] results = new double [2];
		double initialValue = dc.valueOfPortfolio (port, strategyEndDate, dm);
		results[0] = initialValue;

		try {
			MDAStrategy mda = new MDAStrategy("mda", longRunFrame, shortRunFrame, volatilePercentage);
			mda.initialize (port, dc, strategyStartDate, dm);
			mda.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);
		}
		catch (Exception e) {
			System.out.println("Strategy could not be executed");
		}

		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, dm);
		results[1] = finalValue;

		return results;
	}
	
	//execute generalized Pairs-Trading Strategy for cluster-based portfolio
	public double [] executeCluster (Portfolio port, String strategyStartDate, String strategyEndDate, double volatilePercentage, double thresholdNumSD, DataCollection dc, DateModifications dm) {

		double [] results = new double [2];
		double initialValue = dc.valueOfPortfolio (port, strategyEndDate, dm);
		results[0] = initialValue;

		try {
			ClusterTradingStrategy cts = new ClusterTradingStrategy("cts", thresholdNumSD, volatilePercentage);
			cts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);
		}
		catch (Exception e) {
			System.out.println ("Strategy could not be executed");
		}
			
		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, dm);
		results[1] = finalValue;

		return results;
	}
	
	//execute Pairs-Trading Strategy
	public double [] executePairs(Portfolio port, String strategyStartDate, String strategyEndDate, String portfolioStartDate, String portfolioEndDate, double volatilePercentage, double thresholdPercentage, DataCollection dc, DateModifications dm) {

		double [] results = new double [2];
		double initialValue = dc.valueOfPortfolio (port, strategyEndDate, dm);
		results[0] = initialValue;

		try {
			PairTradingStrategy pts = new PairTradingStrategy(thresholdPercentage, volatilePercentage);
			pts.initialize (port, dc, dm, portfolioStartDate, portfolioEndDate);
			pts.allUpdates (port, dc, dm, strategyStartDate, strategyEndDate);
		}
		catch (Exception e) {
			System.out.println ("Strategy could not be executed");
		}
		
		double finalValue = dc.valueOfPortfolio (port, strategyEndDate, dm);
		results[1] = finalValue;

		return results;
	}
}
