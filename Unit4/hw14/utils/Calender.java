package utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Calender {
    public static ArrayList<Integer> calculateDate(int days) { //根据天数计算年月日
        LocalDate today = LocalDate.of(2023,1,1).plusDays(days);
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();
        ArrayList<Integer> results = new ArrayList<>();
        results.add(year);
        results.add(month);
        results.add(day);
        return results;
    }

    public static int calculateDays(String year, String month, String day) { //根据年月日计算天数
        int inputYear = Integer.parseInt(year);
        int inputMonth = Integer.parseInt(month);
        int inputDay = Integer.parseInt(day);

        LocalDate inputDate = LocalDate.of(2023, 1, 1);
        LocalDate targetDate = LocalDate.of(inputYear, inputMonth, inputDay);
        long days = ChronoUnit.DAYS.between(inputDate, targetDate);

        return Integer.parseInt(String.valueOf(days));
    }
}
