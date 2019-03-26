package net.ammensa.parse;

import net.ammensa.entity.Course;
import net.ammensa.entity.Menu;
import net.ammensa.exception.MenuParseException;
import net.ammensa.parse.antlr.CourseExtractorVisitor;
import net.ammensa.parse.antlr.CourseGrammarLexer;
import net.ammensa.parse.antlr.CourseGrammarParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MenuAntlrParser {

    private static final Logger log = Logger.getLogger("debug");
    private static final String MENU_TXT_HEADER_SAMPLE = "MENU' - PRANZO";
    public static final String MENU_ITALIAN_DATE = "[a-zA-Z]+['Ì]?\\s+\\d{1,2}\\s+\\w+\\s+\\d+";
    private static final String MENU_REGEX_HEADER_END = "\\s*" + "(?<italianDate>" + MENU_ITALIAN_DATE + ")\\s+"
            + "[a-zA-Z]+\\s+\\d{1,2}\\s+\\w+\\s+\\d+" + "\\s+";
    private static final String MENU_TXT_ADISU_RECOMMENDATION_SAMPLE = "A.DI.SU.";
    private static final String MENU_REGEX_ADISU_RECOMMENDATION_END = "mediterranean diet." + "\\s+";
    private static final String MENU_REGEX_ADDITIONAL_INGREDIENT = "(?s)(?:\\n\\(\\d\\*\\))(.*?)(?=\\n\\(\\d\\*\\)|\\n\\s*\\B)";
    private static final DateTimeFormatter MENU_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EE dd MMMM y", Locale.ITALY);

    public Menu parseMenu(String menuText) throws MenuParseException {

        try {
            Menu menu = new Menu();

            menuText = cleanMenuText(menuText);

            Pattern pattern = Pattern.compile(MENU_REGEX_HEADER_END);

            Matcher matcher = pattern.matcher(menuText);

            if (matcher.find()) {

                String italianDate = matcher.group("italianDate");

                String[] split = italianDate.split("\\s+");

                split[0] = split[0].substring(0, 3);

                LocalDate menuLocalDate = LocalDate.parse(String.join(" ", split).toLowerCase(), MENU_DATE_TIME_FORMATTER);

                log.info("Date of the menu: " + menuLocalDate);

                menu.setTimestamp(menuLocalDate.atStartOfDay().atZone(ZoneId.of("Europe/Rome")).toInstant().toEpochMilli());
            }


            menuText = menuTextCleanup(menuText);

            List<Course> usualSecondCourses = findUsualSecondCourses(menuText);
            List<Course> usualSideCourses = findUsualSideCourses(menuText);
            Course usualFruitCourse = findUsualFruitCourse(menuText);
            Course takeAwayBasket = findTakeAwayBasket(menuText);

            menuText = removeUsualCoursesFromMenuText(usualSecondCourses, menuText);
            menuText = removeUsualCoursesFromMenuText(usualSideCourses, menuText);
            menuText = removeUsualCourseFromMenuText(usualFruitCourse, menuText);
            menuText = removeTakeAwayBasketFromMenuText(menuText);

            List<Course> dailyCoursesList = findDailyCourses(menuText);

            log.info("parsing complete!");

            List<Course> firstCourses = new ArrayList<>();
            List<Course> secondCourses = new ArrayList<>();
            List<Course> sideCourses = new ArrayList<>();

            firstCourses.addAll(findDailyFirstCourses(dailyCoursesList));

            secondCourses.addAll(findDailySecondCourses(dailyCoursesList));
            secondCourses.addAll(usualSecondCourses);

            sideCourses.addAll(usualSideCourses);
            sideCourses.addAll(findDailySideCourses(dailyCoursesList));

            menu.setFirstCourses(firstCourses);
            menu.setSecondCourses(secondCourses);
            menu.setSideCourses(sideCourses);

            menu.setFruitCourse(usualFruitCourse);
            menu.setTakeAwayBasketContent(takeAwayBasket);

            return menu;

        } catch (Exception ex) {
            MenuParseException mpex = new MenuParseException(
                    "an error occourred " + "while parsing the menu (" + ex.getMessage() + ")");
            mpex.initCause(ex);
            throw mpex;
        }
    }

    private String menuTextCleanup(String menuText) {
        menuText = removeMenuHeaders(menuText);
        menuText = removeMenuFooters(menuText);
        menuText = removeAdditionalIngredients(menuText);
        return menuText;
    }

    private String cleanMenuText(String menuToClean) {

        return menuToClean
                /* replacing double space - common mistake */
                .replace("  ", " ")
                /* uniform hyphens */
                .replace("–", "-")
                /* uniform thicks */
                .replace("’", "'");
    }

    private String removeMenuHeaders(String menuText) {

        if (menuText.contains(MENU_TXT_HEADER_SAMPLE)) {
            /* remove menu header */
            menuText = menuText.split(MENU_REGEX_HEADER_END)[1];
        } else if (menuText.contains(MENU_TXT_ADISU_RECOMMENDATION_SAMPLE)) {
            /* remove adisu recommendation */
            menuText = menuText.split(MENU_REGEX_ADISU_RECOMMENDATION_END)[1];
            /* shitty workaround - need to be fixed */
            if (Pattern.compile(MENU_REGEX_HEADER_END).matcher(menuText).find()) {
                menuText = menuText.split(MENU_REGEX_HEADER_END)[1];
            }
        }
        return menuText;
    }

    private String removeMenuFooters(String menuText) {

        return menuText.replaceFirst("I prodotti con.*\\n", "");
    }

    private String removeAdditionalIngredients(String menuText) {

        return menuText.replaceAll(MENU_REGEX_ADDITIONAL_INGREDIENT, "");
    }

    private List<Course> findUsualSecondCourses(String menu) {

        List<Course> usualSecondCourses = new ArrayList<Course>();

        if (menu.contains(UsualCoursesNames.MOZZARELLA_COURSE_NAME)) {
            usualSecondCourses.add(new Course(UsualCoursesNames.MOZZARELLA_COURSE_NAME));
        }
        if (menu.contains(UsualCoursesNames.FORMAGGI_COURSE_NAME)) {
            usualSecondCourses.add(new Course(UsualCoursesNames.FORMAGGI_COURSE_NAME));
        }
        if (menu.contains(UsualCoursesNames.PIATTI_FREDDI_COURSE_NAME)) {
            usualSecondCourses.add(new Course(UsualCoursesNames.PIATTI_FREDDI_COURSE_NAME));
        }
        return usualSecondCourses;
    }

    private List<Course> findUsualSideCourses(String menu) {

        List<Course> usualSideCourses = new ArrayList<Course>();

        // if (menu.contains(UsualCoursesNames.INSALATA_COURSE_NAME)) {
        // usualSideCourses.add(new
        // Course(UsualCoursesNames.INSALATA_COURSE_NAME));
        // }
        return usualSideCourses;
    }

    private Course findUsualFruitCourse(String menu) {

        Course fruitCourse = new Course();
        if (menu.contains(UsualCoursesNames.FRUTTA_COURSE_NAME)) {
            fruitCourse.setName(UsualCoursesNames.FRUTTA_COURSE_NAME);
        } else
            // course not found, adding a dummy course name
            fruitCourse.setName("Niente frutta oggi? Strano...");
        return fruitCourse;
    }

    private Course findTakeAwayBasket(String menu) {

        Course takeAwayBasket = new Course(UsualCoursesNames.CESTINO_COURSE_NAME);
        try {

            Pattern basketPattern = Pattern
                    .compile(UsualCoursesNames.CESTINO_COURSE_NAME + "\\s*(?:\\:|-|=)?\\s*(.*)\\s+"
                            + UsualCoursesNames.TAKE_AWAY_BASKET_COURSE_NAME + "\\s*(?:\\:|-|=)?\\s*(.*)");
            Matcher basketMatcher = basketPattern.matcher(menu);
            basketMatcher.find();

            takeAwayBasket.addIngredients(basketMatcher.group(1));
            takeAwayBasket.addIngredients(basketMatcher.group(2));
        } catch (Exception ex) {
            // course not found, adding a dummy course ingredient
            takeAwayBasket.addIngredients("chissà cosa ci metteranno oggi...");
            System.out.println("CESTINO ERROR: " + ex);
            ex.printStackTrace();
        }
        return takeAwayBasket;
    }

    private String removeUsualCoursesFromMenuText(List<Course> courses, String menuText) {
        for (Course c : courses) {
            menuText = removeUsualCourseFromMenuText(c, menuText);
        }
        return menuText;
    }

    private String removeUsualCourseFromMenuText(Course course, String menuText) {
        if (course != null) {
            return menuText.replaceFirst(course.getName() + "\\s+", "");
        } else
            return menuText;
    }

    private String removeTakeAwayBasketFromMenuText(String menuTxt) {
        return menuTxt.replaceFirst(UsualCoursesNames.CESTINO_COURSE_NAME + ".*\\n?", "")
                .replaceFirst(UsualCoursesNames.TAKE_AWAY_BASKET_COURSE_NAME + ".*\\n?", "");
    }

    private List<Course> findDailyCourses(String menu) {

        String[] splittedCourses = splitDailyCoursesFromMenuText(menu);

        List<Course> courses = new ArrayList<Course>();

        for (String courseString : splittedCourses) {
            courses.add(parseDailyCourse(courseString));
        }

        if (areSecondCoursesInLastPosition(menu)) {
            courses = reorderCourses(courses);
        }

        return courses;
    }

    private String[] splitDailyCoursesFromMenuText(String menu) {
        return menu.split("\\)\\s{2,}(?=\\w|\\*)");
    }

    private Course parseDailyCourse(String courseString) {
        Course parsedCourse = null;
        try {
            if (!courseString.matches("\\s+")) {
                parsedCourse = parseCourse(courseString);
            }
        } catch (Exception ex) {
            log.severe("ERROR PARSING DAILY COURSE: " + ex);
            parsedCourse = new Course("Pasto a sorpresa! :)");
        }
        return parsedCourse;
    }

    private Course parseCourse(String courseString) {

        CharStream input = CharStreams.fromString(courseString);
        CourseGrammarLexer lexer = new CourseGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CourseGrammarParser parser = new CourseGrammarParser(tokens);

        CourseExtractorVisitor visitor = new CourseExtractorVisitor(parser.course());
        return visitor.extractCourse();
    }

    private boolean areSecondCoursesInLastPosition(String menuText) {

        String[] dailyCoursesSplittedText = menuText.split("\\)\\s*(?=\\s\\w|\\*)");

        List<String> dailySecondCoursesText = Arrays.asList(dailyCoursesSplittedText).subList(
                dailyCoursesSplittedText.length - Menu.DAILY_SECOND_COURSES_NUM, dailyCoursesSplittedText.length);

        boolean secondLastCourseTextFirstChar = Character.isSpaceChar(dailySecondCoursesText.get(0).charAt(0));
        boolean lastSecondCourseTextFirstChar = Character.isSpaceChar(dailySecondCoursesText.get(1).charAt(0));

        return secondLastCourseTextFirstChar && lastSecondCourseTextFirstChar;
    }

    private List<Course> reorderCourses(List<Course> courses) {

        List<Course> secondCourses = courses.subList(courses.size() - Menu.DAILY_SECOND_COURSES_NUM, courses.size());

        courses.addAll(Menu.DAILY_FIRST_COURSES_NUM, secondCourses);

        courses.remove(courses.size() - 1);
        courses.remove(courses.size() - 1);

        return courses;
    }

    private List<Course> findDailyFirstCourses(List<Course> dailyCoursesList) {
        return dailyCoursesList.subList(0, Menu.DAILY_FIRST_COURSES_NUM);
    }

    private List<Course> findDailySecondCourses(List<Course> dailyCoursesList) {
        return dailyCoursesList.subList(Menu.DAILY_FIRST_COURSES_NUM,
                Menu.FIRST_COURSES_NUM + Menu.DAILY_SECOND_COURSES_NUM);
    }

    private List<Course> findDailySideCourses(List<Course> dailyCoursesList) {
        return dailyCoursesList.subList(Menu.DAILY_FIRST_COURSES_NUM + Menu.DAILY_SIDE_COURSES_NUM,
                Menu.DAILY_FIRST_COURSES_NUM + Menu.DAILY_SIDE_COURSES_NUM + Menu.DAILY_SIDE_COURSES_NUM);
    }
}