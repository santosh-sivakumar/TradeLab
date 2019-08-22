import java.io.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
 
@WebServlet("/testing")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class TestingServlet extends HttpServlet{
   boolean readData;
   TestClasses tc;
   DataCollection dc;
   ArrayList<String> vectors;

   public TestingServlet () {
      readData = true;
      tc = new TestClasses ();
      dc = new DataCollection ("dc");
      vectors = new ArrayList<String>();
   }

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException {
      
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      String stockFileName = "all_stocks.txt";
      String dateFileName = "trading_days.txt";
      
      if (readData) {
         try {
            dc.readData (stockFileName, dateFileName);
            System.out.println("read data");
            vectors = dc.vectorNamesList(stockFileName);
            readData = false;
         } catch (Exception e) {
            System.out.println("something went wrong");
         }
      }

      DateModifications dm = new DateModifications ();
      int granularity = 4;
      PortfolioMaker pm = new PortfolioMaker(granularity);
      double [] results = new double [4];
      String strategy = request.getParameter("strategy");
      String portfolio = request.getParameter("portfolio");
      String strategyStartDate = request.getParameter("strategyStartDate");
      String strategyEndDate = request.getParameter("strategyEndDate");
      double cash = Double.parseDouble(request.getParameter("cash"));
      
      ArrayList<String> stocksInPortfolio = new ArrayList<String>();
      ArrayList<Double> valuesInPortfolio = new ArrayList<Double>();
      Portfolio port = new Portfolio ("port", cash, stocksInPortfolio, valuesInPortfolio);
      String diversity = "";
      
      if (portfolio.equals("k-Means")) {
         int clusterSize = Integer.parseInt(request.getParameter("numStocks"));
         diversity = request.getParameter("diversity");
         String portStartDate = request.getParameter("portStartDate");
         String portEndDate = request.getParameter("portEndDate");
      
         if (diversity.equals("diverse")) {

            port = pm.diversekMeans (stockFileName, clusterSize, portStartDate,
            cash, portEndDate, strategyStartDate, strategyEndDate, dc, dm, vectors);

         }

         else {
            assert (diversity.equals("uniform"));
            
            port = pm.uniformkMeans (stockFileName, clusterSize, portStartDate,
            cash, portEndDate, strategyStartDate, strategyEndDate, dc, dm, vectors);
         }
      }

      else {
      
         assert (portfolio.equals("userInput"));

         String stockNames = request.getParameter("listOfStocks");
         String [] stocks = stockNames.split(",");
         ArrayList<String> listOfStocks = new ArrayList<String>();
         
         for (int i = 0; i < stocks.length; i++) {
            listOfStocks.add(stocks[i]);
         }

         String stockValues = request.getParameter("listOfNumbers");
         String [] values = stockValues.split(",");
         ArrayList<Double> listOfValues = new ArrayList<Double>();
         
         for (int j = 0; j < values.length; j++) {
            listOfValues.add(Double.parseDouble(values[j]));
         }

         port = pm.userInputPortfolio(listOfStocks,listOfValues, cash, strategyStartDate, strategyEndDate, 
                  dc, dm);
      }
      
      

      if (strategy.equals("Threshold")) {
         double thresholdPercentage = Double.parseDouble(request.getParameter("thresholdPercentage"));
         double mrVolatilePercentage = Double.parseDouble(request.getParameter("MRvolatilePercentage"));

         try {
            results = tc.executeMeanReversion (port, strategyStartDate, strategyEndDate, 
                thresholdPercentage, mrVolatilePercentage, dc, dm);
            } catch (Exception e) {}
      }

      else if (strategy.equals("MDA")) {
         int longRunFrame = Integer.parseInt(request.getParameter("longRunFrame"));
         int shortRunFrame = Integer.parseInt(request.getParameter("shortRunFrame"));
         double mdaVolatilePercentage = Double.parseDouble(request.getParameter("MDAvolatilePercentage"));

         try {
            results = tc.executeMDA (port, strategyStartDate, strategyEndDate, 
               mdaVolatilePercentage, longRunFrame, shortRunFrame, dc, dm);
            System.out.println(results.length);
         } catch (Exception e) {}
      }

      else if (strategy.equals("Cluster")) {
         double thresholdnumSD = Double.parseDouble(request.getParameter("thresholdNumSD"));
         double ctVolatilePercentage = Double.parseDouble(request.getParameter("CTvolatilePercentage"));

         try {
            results = tc.executeCluster (port, strategyStartDate, strategyEndDate, 
               ctVolatilePercentage,thresholdnumSD, dc, dm);
         } catch (Exception e) {}
      }

      try { 
         printHeader(out);
      } catch (Exception e) {
      }  

      String portfolioInfo = diversity + " " + portfolio;

      resultsToPrint (out, strategy, port, portfolioInfo, strategyStartDate, strategyEndDate, results[0], results[1], results[2], results[3]);

      try { 
         printFooter(out);
      } catch (Exception e) {
      } 
   }


   public void printHeader (PrintWriter out) throws Exception {
      File header = new File ("testingresultsheader.html");
      BufferedReader br = new BufferedReader(new FileReader(header)); 
      String line; 

      while ((line = br.readLine()) != null) {
         out.println(line);
      }
   }

   public void resultsToPrint (PrintWriter out, String strategy, Portfolio port, String portfolio, String strategyStartDate, String strategyEndDate, double initialValue, double initialCash, double finalValue, double finalCash) {

      out.println("<h4>" + "</h4>");
      out.println("<div class=" + "display-t display-t2 text-center" + ">");
      out.println("<div class=" + "display-tc display-tc2" + ">");
      out.println("<div class=" + "container" + ">");
      out.println("<div class=" + "row" + ">");
      out.println("<div class="+ "col-md-12" + ">");
      out.println("<div class="+ "contact-info-wrap-flex" + ">");
      out.println("<div class="+ "con-info" + ">");
      out.println("<p> <strong>Strategy: </strong>" + strategy + "</p>");
      out.println("</div>");
      out.println("<div class="+ "con-info" + ">");
      out.println("<p> <strong>Portfolio: </strong>" + portfolio + "</p>");
      out.println("</div>");
      out.println("<div class="+ "con-info" + ">");
      out.println("<p> <strong>Start Date: </strong>" + strategyStartDate + "</p>");
      out.println("</div>");
      out.println("<div class="+ "con-info" + ">");
      out.println("<p> <strong>End Date: </strong>" + strategyEndDate + "</p>");
      out.println("</div>");
      out.println("<div class="+ "con-info" + ">");
      out.println("<p><strong>Stocks in Portfolio: </strong>" + port.stocksInPortfolio + "</p>");
      out.println("</div>");
      out.println("<div class="+ "con-info" + ">");
      out.println("<p><strong>If the portfolio was untouched, the value would be </strong>$" + initialValue);
      out.println("<strong> and your cash holdings would be </strong>$" + initialCash + "</p>");
      out.println("</div>");
      out.println("<div class="+ "con-info" + ">");
      out.println("<p><strong>With strategy, the value would be </strong>$" + finalValue);
      out.println("<strong>and your cash holdings would be </strong>$" + initialCash + "</p>");
      out.println("</div>");
      out.println("</div>");
      out.println("</div>");
      out.println("</div>");
      out.println("</div>");
      out.println("</div>");
      out.println("</div>");

   }

   public void printFooter (PrintWriter out) throws Exception {
      File footer = new File ("testingresultsfooter.html");
      BufferedReader br = new BufferedReader(new FileReader(footer)); 
      String line; 

      while ((line = br.readLine()) != null) {
         out.println(line);
      }
  
   }

}