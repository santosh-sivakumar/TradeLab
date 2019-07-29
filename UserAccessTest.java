import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class UserAccessTest {

	public static void main (String[] args)  throws Exception {
	//public main method to offer choices to user for backtesting experience
	//user selects trading stategy, portfolio initialization method
	//given specific trading strategy choice, user is offered other choices
	//based on combo of portfolio/strategy, methods within TestClasses called correspondingly

		TestClasses tc = new TestClasses ();

		System.out.println("Which trading strategy would you like to use?");
		System.out.println("Options: 'Threshold', 'MDA', 'Cluster'");
		System.out.println("Please indicate your choice here: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String strategy = br.readLine();
		
		if (strategy.equals("Threshold")){

			System.out.println("Type in strategy start date: ");
			String strategyStartDate = br.readLine();

			System.out.println("Type in strategy end date: ");
			String strategyEndDate = br.readLine();

			System.out.println("Type in file name: ");
			String filename = br.readLine();

			System.out.println("Type in cash amount: ");
			double cash = Double.parseDouble(br.readLine());

			System.out.println("Type in granularity: ");
			int granularity = Integer.parseInt(br.readLine());				

			System.out.println("Type in Threshold %: ");
			double thresholdPercentage = Double.parseDouble(br.readLine());
				
			System.out.println("Type in Volatile %: ");
			double volatilePercentage = Double.parseDouble(br.readLine());

			System.out.println("Would you like a computer-generated input? Type 'Y' or 'N': ");
			String input = br.readLine();

			if (input.equals("N")) {
				List<String> listOfStocks = new ArrayList<String>();
				List<Double> listOfValues = new ArrayList<Double>();

				userInputClusterNames(listOfStocks);
				userInputClusterValues(listOfValues);


				tc.userInputWithThreshold (filename, listOfStocks, listOfValues, strategyStartDate, 
					strategyEndDate, cash, granularity, 
					thresholdPercentage, volatilePercentage);
			}

			if (input.equals("Y")) {
				System.out.println("How many stocks do you want in your portfolio?");
				int numStocks = Integer.parseInt(br.readLine());

				System.out.println("Type in portfolio initialization start date: ");
				String startDate = br.readLine();

				System.out.println("Type in portfolio initialization end date: ");
				String endDate = br.readLine();

				System.out.println("Do you want to use HAC or K-Means clustering?: ");
				String clustering = br.readLine();

				if (clustering.equals("HAC")) {
					System.out.println("By what factor are you willing to distort portfolio size?: ");
					Double factor = Double.parseDouble(br.readLine());
					System.out.println("Do you want a uniform or diversified portfolio?: ");
					String portfoliotype = br.readLine();

					if (portfoliotype.equals("uniform")) {
						tc.uniformHACWithThreshold (filename, numStocks, factor, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						thresholdPercentage, volatilePercentage);
					}

					if (portfoliotype.equals("diversified")) {
						tc.diverseHACWithThreshold (filename, numStocks, factor, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						thresholdPercentage, volatilePercentage);
					}

				}

				if (clustering.equals("K-Means")) {
					System.out.println("Do you want a uniform or diversified portfolio?: ");
					String portfoliotype = br.readLine();

					if (portfoliotype.equals("uniform")) {
						tc.uniformKMeansWithThreshold (filename, numStocks, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						thresholdPercentage, volatilePercentage);
					}

					if (portfoliotype.equals("diversified")) {
						tc.diverseKMeansWithThreshold (filename, numStocks, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						thresholdPercentage, volatilePercentage);
					}
				}


			}

		}

		if (strategy.equals("MDA")){

			System.out.println("Type in strategy start date: ");
			String strategyStartDate = br.readLine();

			System.out.println("Type in strategy end date: ");
			String strategyEndDate = br.readLine();

			System.out.println("Type in file name: ");
			String filename = br.readLine();

			System.out.println("Type in cash amount: ");
			double cash = Double.parseDouble(br.readLine());

			System.out.println("Type in granularity: ");
			int granularity = Integer.parseInt(br.readLine());				
				
			System.out.println("Type in Volatile %: ");
			double volatilePercentage = Double.parseDouble(br.readLine());

			System.out.println("Type in long-run frame: ");
			int longRunFrame = Integer.parseInt(br.readLine());		

			System.out.println("Type in short-run frame: ");
			int shortRunFrame = Integer.parseInt(br.readLine());		



			System.out.println("Would you like a computer-generated input? Type 'Y' or 'N': ");
			String input = br.readLine();

			if (input.equals("N")) {
				List<String> listOfStocks = new ArrayList<String>();
				List<Double> listOfValues = new ArrayList<Double>();

				userInputClusterNames(listOfStocks);
				userInputClusterValues(listOfValues);


				tc.userInputWithMDA (filename, listOfStocks, listOfValues, 
					strategyStartDate, strategyEndDate, cash, granularity, volatilePercentage,
					longRunFrame, shortRunFrame);
			}

			if (input.equals("Y")) {
				System.out.println("How many stocks do you want in your portfolio?");
				int numStocks = Integer.parseInt(br.readLine());

				System.out.println("Type in portfolio initialization start date: ");
				String startDate = br.readLine();

				System.out.println("Type in portfolio initialization end date: ");
				String endDate = br.readLine();

				System.out.println("Do you want to use HAC or K-Means clustering?: ");
				String clustering = br.readLine();

				if (clustering.equals("HAC")) {
					System.out.println("By what factor are you willing to distort portfolio size?: ");
					Double factor = Double.parseDouble(br.readLine());
					System.out.println("Do you want a uniform or diversified portfolio?: ");
					String portfoliotype = br.readLine();

					if (portfoliotype.equals("uniform")) {
						tc.uniformHACWithMDA (filename, numStocks, factor, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						volatilePercentage, longRunFrame, shortRunFrame);
					}

					if (portfoliotype.equals("diversified")) {
						tc.diverseHACWithMDA (filename, numStocks, factor, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						volatilePercentage, longRunFrame, shortRunFrame);
					}

				}

				if (clustering.equals("K-Means")) {
					System.out.println("Do you want a uniform or diversified portfolio?: ");
					String portfoliotype = br.readLine();

					if (portfoliotype.equals("uniform")) {
						tc.uniformKMeansWithMDA (filename, numStocks, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						volatilePercentage, longRunFrame, shortRunFrame);
					}

					if (portfoliotype.equals("diversified")) {
						tc.diverseKMeansWithMDA (filename, numStocks, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						volatilePercentage, longRunFrame, shortRunFrame);
					}
				}

			}

		}

		if (strategy.equals("Cluster")){
			System.out.println("Type in strategy start date: ");
			String strategyStartDate = br.readLine();

			System.out.println("Type in strategy end date: ");
			String strategyEndDate = br.readLine();

			System.out.println("Type in file name: ");
			String filename = br.readLine();

			System.out.println("Type in cash amount: ");
			double cash = Double.parseDouble(br.readLine());

			System.out.println("Type in granularity: ");
			int granularity = Integer.parseInt(br.readLine());				

			System.out.println("Type in Threshold Number of Standard Deviations: ");
			double thresholdNumSD = Double.parseDouble(br.readLine());
				
			System.out.println("Type in Volatile %: ");
			double volatilePercentage = Double.parseDouble(br.readLine());

			System.out.println("Would you like a computer-generated input? Type 'Y' or 'N': ");
			String input = br.readLine();

			if (input.equals("N")) {
				List<String> listOfStocks = new ArrayList<String>();
				List<Double> listOfValues = new ArrayList<Double>();

				userInputClusterNames(listOfStocks);
				userInputClusterValues(listOfValues);


				tc.userInputWithCluster (filename, listOfStocks, listOfValues, strategyStartDate, 
					strategyEndDate, cash, granularity, 
					volatilePercentage, thresholdNumSD);
			}

			if (input.equals("Y")) {
				System.out.println("How many stocks do you want in your portfolio?");
				int numStocks = Integer.parseInt(br.readLine());

				System.out.println("Type in portfolio initialization start date: ");
				String startDate = br.readLine();

				System.out.println("Type in portfolio initialization end date: ");
				String endDate = br.readLine();

				System.out.println("Do you want to use HAC or K-Means clustering?: ");
				String clustering = br.readLine();

				if (clustering.equals("HAC")) {
					System.out.println("By what factor are you willing to distort portfolio size?: ");
					Double factor = Double.parseDouble(br.readLine());
					System.out.println("Do you want a uniform or diversified portfolio?: ");
					String portfoliotype = br.readLine();

					if (portfoliotype.equals("uniform")) {
						tc.uniformHACWithCluster (filename, numStocks, factor, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						volatilePercentage, thresholdNumSD);
					}

					if (portfoliotype.equals("diversified")) {
						tc.diverseHACWithCluster (filename, numStocks, factor, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						volatilePercentage, thresholdNumSD);
					}

				}

				if (clustering.equals("K-Means")) {
					System.out.println("Do you want a uniform or diversified portfolio?: ");
					String portfoliotype = br.readLine();

					if (portfoliotype.equals("uniform")) {
						tc.uniformKMeansWithCluster (filename, numStocks, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						volatilePercentage, thresholdNumSD);
					}

					if (portfoliotype.equals("diversified")) {
						tc.diverseKMeansWithCluster (filename, numStocks, startDate, endDate,
						strategyStartDate, strategyEndDate, cash, granularity, 
						volatilePercentage, thresholdNumSD);
					}
				}
			}
		}
	}

	private static void userInputClusterNames (List<String> listOfStocks) throws Exception {
	//private method to parse stock names in user-inputted portfolio

		System.out.println("Please type your stock choices, separated by commas, here: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String stockLine = br.readLine();

		String[] stocks = stockLine.split(", ");
  		for (int index = 0; index < stocks.length; index++) {
  			listOfStocks.add(stocks[index]);
  		}
	}

	private static void userInputClusterValues (List<Double> listOfStockValues) throws Exception {
	//private method to parse stock initialized investments in user-inputted portfolio

		System.out.println("Please type your investment amounts, separated by commas, here: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String stockLine = br.readLine();

		String[] stockValues = stockLine.split(", ");
  		for (int index = 0; index < stockValues.length; index++) {
  			double currValue = Double.parseDouble(stockValues[index]);
  			listOfStockValues.add(currValue);
  		}
	}

}
