package net.ammensa.entity;

import net.ammensa.parse.UsualCourseData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {

    private String name;
    private List<String> ingredients;

    public Course(String name) {
        this();
        this.setName(name);
    }

    public Course() {
        /* usually there are two lines of ingredients */
        ingredients = new ArrayList<>(2);
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = new ArrayList<>(ingredients);
    }

    public void addIngredients(String ingredients) {
        this.ingredients.add(ingredients);
    }

    public void displayCourse() {
        System.out.println(toString());
    }

    public static Course fromUsualCourse(UsualCourseData usualCourse) {
        Course course = new Course();
        course.setName(usualCourse.courseName());
        return course;
    }

    @Override
    public String toString() {
        StringBuilder courseString = new StringBuilder(this.getName() + "\n");
        for (String s : this.getIngredients()) {
            courseString.append(" - ").append(s).append("\n");
        }
        return courseString.toString();
    }
}