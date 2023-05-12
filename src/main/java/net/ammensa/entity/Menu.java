package net.ammensa.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Menu implements Serializable {

    public static final int DAILY_FIRST_COURSES_NUM = 2;
    public static final int DAILY_SECOND_COURSES_NUM = 2;
    public static final int DAILY_SIDE_COURSES_NUM = 1;
    public static final int FIRST_COURSES_NUM = DAILY_FIRST_COURSES_NUM;
    public static final int SECOND_COURSES_NUM = DAILY_SECOND_COURSES_NUM + 3;
    public static final int SIDE_COURSES_NUM = DAILY_SIDE_COURSES_NUM + 1;

    private LocalDate date;
    private String url;
    private List<Course> firstCourses = new ArrayList<>(FIRST_COURSES_NUM);
    private List<Course> secondCourses = new ArrayList<>(SECOND_COURSES_NUM);
    private List<Course> sideCourses = new ArrayList<>(SIDE_COURSES_NUM);
    private Course fruitCourse = new Course();
    private Course takeAwayBasketContent = new Course();

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Course> getFirstCourses() {
        return firstCourses;
    }

    public void setFirstCourses(List<Course> firstCourses) {
        this.firstCourses = firstCourses;
    }

    public void setSecondCourses(List<Course> secondCourses) {
        this.secondCourses = secondCourses;
    }

    public List<Course> getSecondCourses() {
        return secondCourses;
    }

    public List<Course> getSideCourses() {
        return sideCourses;
    }

    public void setSideCourses(List<Course> sideCourses) {
        this.sideCourses = sideCourses;
    }

    public Course getFruitCourse() {
        return fruitCourse;
    }

    public void setFruitCourse(Course fruitCourse) {
        this.fruitCourse = fruitCourse;
    }

    public Course getTakeAwayBasketContent() {
        return takeAwayBasketContent;
    }

    public void setTakeAwayBasketContent(Course takeAwayBasketContent) {
        this.takeAwayBasketContent = takeAwayBasketContent;
    }

    public long getTimestamp() {
        return date.atStartOfDay().atZone(ZoneId.of("Europe/Rome")).toInstant().toEpochMilli();
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

    private String printCourse(Course c) {

        StringBuilder courseString = new StringBuilder();

        if (c.getName() != null) {
            courseString = new StringBuilder("+ " + c.getName() + "\n");
            for (String s : c.getIngredients()) {
                courseString.append(" - ").append(s).append("\n");
            }
        }
        return courseString.toString();
    }
}