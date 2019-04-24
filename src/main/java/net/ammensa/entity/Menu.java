package net.ammensa.entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;

public class Menu implements Serializable {

    public final static int DAILY_FIRST_COURSES_NUM = 3;
    public final static int DAILY_SECOND_COURSES_NUM = 2;
    public final static int DAILY_SIDE_COURSES_NUM = 2;
    public final static int FIRST_COURSES_NUM = DAILY_FIRST_COURSES_NUM;
    public final static int SECOND_COURSES_NUM = DAILY_SECOND_COURSES_NUM + 3;
    public final static int SIDE_COURSES_NUM = DAILY_SIDE_COURSES_NUM + 1;

    private long timestamp;
    private String url;
    private List<Course> firstCourses;
    private List<Course> secondCourses;
    private List<Course> sideCourses;
    private Course fruitCourse;
    private Course takeAwayBasketContent;
    private boolean isTodayMenu;
    private String itLocaleDateText;

    public Menu() {
        firstCourses = new ArrayList<>(FIRST_COURSES_NUM);
        secondCourses = new ArrayList<>(SECOND_COURSES_NUM);
        sideCourses = new ArrayList<>(SIDE_COURSES_NUM);
        fruitCourse = new Course();
        takeAwayBasketContent = new Course();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUrl() {
        return url;
    }

    public List<Course> getFirstCourses() {
        return firstCourses;
    }

    public List<Course> getSecondCourses() {
        return secondCourses;
    }

    public List<Course> getSideCourses() {
        return sideCourses;
    }

    public Course getFruitCourse() {
        return fruitCourse;
    }

    public Course getTakeAwayBasketContent() {
        return takeAwayBasketContent;
    }

    public boolean isTodayMenu() {
        return isTodayMenu;
    }

    public String getItLocaleDateText() {
        return itLocaleDateText;
    }

    /**
     * TODO: refactor to use java.time
     * Has the side effect of setting <code>isTodayMenu</code> field.
     */
    public void setTimestamp(long millis) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
        Calendar calToSet = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
        calToSet.setTimeInMillis(millis);
        this.isTodayMenu = now.get(Calendar.YEAR) == calToSet.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == calToSet.get(Calendar.DAY_OF_YEAR);
        this.timestamp = millis;
        setITLocaleDateText(new Date(this.timestamp));
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFirstCourses(List<Course> firstCourses) {
        this.firstCourses = new ArrayList<>(firstCourses);
    }

    public void setSecondCourses(List<Course> secondCourses) {
        this.secondCourses = new ArrayList<>(secondCourses);
    }

    public void setSideCourses(List<Course> sideCourses) {
        this.sideCourses = new ArrayList<>(sideCourses);
    }

    public void setFruitCourse(Course fruitCourse) {
        this.fruitCourse = fruitCourse;
    }

    public void setTakeAwayBasketContent(Course takeAwayBasketContent) {
        this.takeAwayBasketContent = takeAwayBasketContent;
    }

    private void setITLocaleDateText(Date date) {

        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, Locale.ITALY);
        df.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY));
        this.itLocaleDateText = df.format(date);
    }

    public void displayMenu() {
        System.out.println(this.toString());
    }

    private String printCourse(Course c) {

        String courseString = "";

        if (c.getName() != null) {
            courseString = "+ " + c.getName() + "\n";
            for (String s : c.getIngredients()) {
                courseString = courseString + " - " + s + "\n";
            }
        }
        return courseString;
    }

    @Override
    public String toString() {

        StringBuilder menuStringBuilder = new StringBuilder();

        menuStringBuilder.append("------------PRIMI\n");
        for (Course c : this.getFirstCourses()) {
            menuStringBuilder.append(printCourse(c));
        }
        menuStringBuilder.append("------------SECONDI\n");
        for (Course c : this.getSecondCourses()) {
            menuStringBuilder.append(printCourse(c));
        }
        menuStringBuilder.append("------------CONTORNI\n");
        for (Course c : this.getSideCourses()) {
            menuStringBuilder.append(printCourse(c));
        }
        menuStringBuilder.append("------------FRUTTA\n");
        if (this.fruitCourse != null) {
            menuStringBuilder.append(printCourse(this.fruitCourse));
        }
        menuStringBuilder.append("------------CESTINO\n");
        if (this.takeAwayBasketContent != null) {
            menuStringBuilder.append(printCourse(this.takeAwayBasketContent));
        }

        return menuStringBuilder.toString();
    }
}
