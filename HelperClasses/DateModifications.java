public class DateModifications {
// public class to make modifications to a given date

	private int dateYear (String date) {
		int year = Integer.parseInt(date.substring(0,4));
		return year;
	// returns year of a given date (input format = String)
	}
	
	private int dateMonth (String date) {
		int month = Integer.parseInt(date.substring(5,7));
		return month;
	// returns month of a given date (input format = String)
	}
	
	private int dateDay (String date) {
		int day = Integer.parseInt(date.substring(8,10));
		return day;	
	// returns day of a given date (input format = String)
	}
	
	private boolean isLeapYear (String date) {
		int year = dateYear(date);
		if ((year % 4) == 0) {
			if ((year % 100) == 0) {
				if ((year % 400) != 0) {
					return false;
				}
				return true;
			}
			return true;
		}
		return false;
	// returns whether date is within a leap year, as a boolean value
	}
	
	private boolean isMonthThirtyDays (String date) {
		int month = dateMonth(date);
		if (month == 4 || month == 6 || month == 9 || month == 1) {
			return true;
		}
		return false;
	// for incrementing purposes, determines number of days in month
	// returns True if month is 30 days, false elsewhere [incl. Feb]
	}
		
	public String incrementDate (String date) {
	// main public method to forward increment a date (given as a String)
	// runs through possible scenarios (different times in the year)
	// updates date as necessary, returns incremented date as a String

		if (dateDay(date) == 28 && dateMonth(date) == 2 && (!isLeapYear(date))) {
			String year = Integer.toString(dateYear(date));
			String month = "03";
			String day = "01";
			date = (year + "-" + month + "-" + day);
		}
		else if (dateDay(date) == 29 && dateMonth(date) == 2) {
			String year = Integer.toString(dateYear(date));
			String month = "03";
			String day = "01";
			date = (year + "-" + month + "-" + day);
		}
		else if (dateDay(date) == 30 && isMonthThirtyDays(date)) {
			String year = Integer.toString(dateYear(date));
			int currMonth = (dateMonth(date) + 1);
			String month = Integer.toString(currMonth);
			if (currMonth < 10) {
				 month = ("0" + month);
			}			
			String day = "01";
			date = (year + "-" + month + "-" + day);
		}
		else if (dateDay(date) == 31) {
			if (dateMonth(date) == 12) {
				String year = Integer.toString(dateYear(date) + 1);
				String month = "01";
				String day = "01";
				date = (year + "-" + month + "-" + day);
			}
			else {
				String year = Integer.toString(dateYear(date));
				int currMonth = (dateMonth(date) + 1);
				String month = Integer.toString(currMonth);
				if (currMonth < 10) {
					month = ("0" + month);
				}					
				String day = "01";
				date = (year + "-" + month + "-" + day);
			}
		}	
		else {
			String year = Integer.toString(dateYear(date));
			int currMonth = (dateMonth(date));
			String month = Integer.toString(currMonth);
			if (currMonth < 10) {
				month = ("0" + month);
			}
			int currDay = (dateDay(date) + 1);
			String day = Integer.toString(currDay);
			if (currDay < 10) {
				 day = ("0" + day);
			}	
			date = (year + "-" + month + "-" + day);
		}
		return date;
	}

	public String decrementDate (String date) {
	// main public method to backward decrement a date (given as a String)
	// runs through possible scenarios (different times in the year)
	// updates date as necessary, returns decremented date as a String


		if (dateDay(date) == 3 && dateMonth(date) == 1 && (!isLeapYear(date))) {
			String year = Integer.toString(dateYear(date));
			String month = "02";
			String day = "28";
			date = (year + "-" + month + "-" + day);
		}
		else if (dateDay(date) == 3 && dateMonth(date) == 1 && isLeapYear(date)) {
			String year = Integer.toString(dateYear(date));
			String month = "02";
			String day = "29";
			date = (year + "-" + month + "-" + day);
		}
		else if (dateDay(date) == 1 && dateMonth(date) == 1) {
			String year = Integer.toString(dateYear(date) - 1);
			String month = "12";
			String day = "31";
			date = (year + "-" + month + "-" + day);
		}

		else if (dateDay(date) == 1 && isMonthThirtyDays(date)) {

			String year = Integer.toString(dateYear(date));
			int currMonth = (dateMonth(date) - 1);
			String month = Integer.toString(currMonth);
			if (currMonth < 10) {
				 month = ("0" + month);
			}			
			String day = "30";
			date = (year + "-" + month + "-" + day);
		}
		else if (dateDay(date) == 1 && (!isMonthThirtyDays(date))) {
			String year = Integer.toString(dateYear(date));
			int currMonth = (dateMonth(date) - 1);
			String month = Integer.toString(currMonth);
			if (currMonth < 10) {
				 month = ("0" + month);
			}			
			String day = "30";
			date = (year + "-" + month + "-" + day);
		}
		else {
			String year = Integer.toString(dateYear(date));
			int currMonth = (dateMonth(date));
			String month = Integer.toString(currMonth);
			if (currMonth < 10) {
				month = ("0" + month);
			}
			int currDay = (dateDay(date) - 1);
			String day = Integer.toString(currDay);
			if (currDay < 10) {
				 day = ("0" + day);
			}	
			date = (year + "-" + month + "-" + day);
		}
		return date;
	}

}

