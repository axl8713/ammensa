package net.ammensa.utils;

import java.util.Calendar;

public abstract class Mensa {

	public static boolean isOpenDay(Calendar dateToCheck) {

		int dateDay = dateToCheck.get(Calendar.DAY_OF_MONTH);
		int dateMonth = dateToCheck.get(Calendar.MONTH);
		int dateDayOfWeek = dateToCheck.get(Calendar.DAY_OF_WEEK);

		if (dateDayOfWeek == Calendar.SATURDAY || dateDayOfWeek == Calendar.SUNDAY)
			return false;
		else if (dateDay == 25 && dateMonth == Calendar.DECEMBER) {
			// Christmas
			System.out.println("Merry Christmas!!!");
			return false;
		} else if ((dateDay == 1 || dateDay == 6) && dateMonth == Calendar.JANUARY) {
			// new year's day or epiphany
			System.out.println("Happy new year!!!");
			return false;
		} else if (dateDay == 1 && dateMonth == Calendar.MAY) {
			// worker's day
			return false;
		} else if (dateDay == 25 && dateMonth == Calendar.APRIL) {
			// Italy liberation day
			return false;
		} else if (dateDay == 2 && dateMonth == Calendar.JUNE) {
			// Italian armed forces' day
			return false;
		} else
			return true;
	}
}