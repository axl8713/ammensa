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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MenuParser {

    private static final Logger LOGGER = Logger.getLogger(MenuParser.class.getName());
    private static final String MENU_TXT_HEADER_SAMPLE = "MENU' - PRANZO";
    private static final String MENU_ITALIAN_DATE = "[a-zA-Z]+(?:.|Ì)\\s*\\d{1,2}\\s*[a-z]+\\s*\\d+";
    private static final String MENU_REGEX_HEADER_END = "(?is).*menu.*\\b(?<italianDate>" + MENU_ITALIAN_DATE + ")\\s*\\n.*\\b[a-z]+\\s*\\d{1,2}\\s*[a-z]+\\s*\\d+";
    private static final String MENU_TXT_ADISU_RECOMMENDATION_SAMPLE = "A.DI.SU.";
    private static final String MENU_REGEX_ADISU_RECOMMENDATION_END = "mediterranean diet." + "\\s+";
    private static final String MENU_REGEX_ADDITIONAL_INGREDIENT = "(?s)(?:\\n\\(\\d\\*\\))(.*?)(?=\\n\\(\\d\\*\\)|\\n\\s*\\B)";
    private static final DateTimeFormatter MENU_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EE dd MMMM y", Locale.ITALY);

    public Menu parseMenu(String menuText) throws MenuParseException {
        try {
            Menu menu = new Menu();

            menu.setDate(findMenuLocalDate(menuText).orElseThrow());

            menuText = menuTextCleanup(menuText);

            List<Course> dailyCourses = findDailyCourses(menuText);

            menu.setFirstCourses(findFirstCourses(dailyCourses, menuText));
            menu.setSecondCourses(findSecondCourses(dailyCourses, menuText));
            menu.setSideCourses(findSideCourses(dailyCourses, menuText));
            menu.setFruitCourse(findUsualFruitCourse(menuText));
            menu.setTakeAwayBasketContent(findTakeAwayBasket(menuText));

            LOGGER.info("parsing complete!");

            return menu;

        } catch (Exception ex) {
            MenuParseException mpex = new MenuParseException(
                    "an error occourred while parsing the menu (" + ex.getMessage() + ")");
            mpex.initCause(ex);
            throw mpex;
        }
    }

    private Optional<LocalDate> findMenuLocalDate(String menuText) {

        Matcher matcher = Pattern.compile(MENU_REGEX_HEADER_END)
                .matcher(menuText);

        if (matcher.find()) {
            LocalDate menuLocalDate = parseMenuDate(matcher);
            LOGGER.info("Date of the menu: " + menuLocalDate);
            return Optional.of(menuLocalDate);
        } else {
            return Optional.empty();
        }
    }

    private String menuTextCleanup(String menuText) {
        return Stream.of(menuText)
                .map(this::cleanMenuText)
                .map(this::removeMenuHeaders)
                .map(this::removeMenuFooters)
                .map(this::removeAdditionalIngredients)
                .collect(Collectors.joining());
    }

    private String cleanMenuText(String menuToClean) {
        return menuToClean
                /* replacing double space - common mistake */
                .replace("  ", " ")
                /* uniform hyphens */
                .replace("–", "-")
                /* uniform thicks */
                .replace("’", "'")
                /* TODO: fix in the antlr grammar */
                .replaceAll("\\(+", "\\(")
                .replaceAll("\\)+", "\\)");
    }

    private String removeMenuHeaders(String menuText) {

        if (menuText.contains(MENU_TXT_HEADER_SAMPLE)) {
            /* remove menu header */
            menuText = menuText.split(MENU_REGEX_HEADER_END)[1];
        } else if (menuText.contains(MENU_TXT_ADISU_RECOMMENDATION_SAMPLE)) {
            /* remove adisu recommendation */
            menuText = menuText.split(MENU_REGEX_ADISU_RECOMMENDATION_END)[1];
            /* TODO: shitty workaround - need to be fixed */
            if (Pattern.compile(MENU_REGEX_HEADER_END).matcher(menuText).find()) {
                menuText = menuText.split(MENU_REGEX_HEADER_END)[1];
            }
        }
        return menuText;
    }

    private String removeMenuFooters(String menuText) {
        return menuText
                .replaceFirst("I prodotti con.*", "")
                .replaceFirst("I prodotti sottolineati.*", "");
    }

    private String removeAdditionalIngredients(String menuText) {
        return menuText.replaceAll(MENU_REGEX_ADDITIONAL_INGREDIENT, "");
    }

    private LocalDate parseMenuDate(Matcher matcher) {
        String italianDate = matcher.group("italianDate");
        String[] split = italianDate.split("\\s+");
        split[0] = split[0].substring(0, 3);
        return LocalDate.parse(String.join(" ", split).toLowerCase(), MENU_DATE_TIME_FORMATTER);
    }

    private List<Course> findDailyCourses(String menuText) {

        String menu = removeUsualCoursesFromMenuText(menuText);

        String[] splittedCourses = splitDailyCoursesFromMenuText(menu);

        List<Course> courses = Arrays.stream(splittedCourses)
                .map(this::parseDailyCourse)
                .collect(Collectors.toList());

        if (areSecondCoursesInLastPosition(menu)) {
            reorderCourses(courses);
        }

        return courses;
    }

    private String removeUsualCoursesFromMenuText(String menuText) {
        for (UsualCourseData ucd : UsualCourseData.values()) {
            menuText = menuText.replaceFirst(ucd.regex(), "");
        }
        return menuText;
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
            LOGGER.severe("ERROR PARSING DAILY COURSE: " + ex);
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

    private void reorderCourses(List<Course> courses) {

        List<Course> secondCourses = courses.subList(courses.size() - Menu.DAILY_SECOND_COURSES_NUM, courses.size());

        courses.addAll(Menu.DAILY_FIRST_COURSES_NUM, secondCourses);

        courses.remove(courses.size() - 1);
        courses.remove(courses.size() - 1);
    }

    private List<Course> findFirstCourses(List<Course> dailyCourses, String menuText) {
        return Stream.of(findDailyFirstCourses(dailyCourses), findUsualFirstCourses(menuText))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Course> findDailyFirstCourses(List<Course> dailyCoursesList) {
        return dailyCoursesList.subList(0, Menu.DAILY_FIRST_COURSES_NUM);
    }

    private List<Course> findUsualFirstCourses(String menu) {

        if (menuContainsUsualCourse(menu, UsualCourseData.PASTA_IN_BIANCO_O_AL_POMODORO)) {

            Course pasta = Course.fromUsualCourse(UsualCourseData.PASTA_IN_BIANCO_O_AL_POMODORO);

            Matcher pastaMatcher = Pattern.compile(".*" + UsualCourseData.PASTA_IN_BIANCO_O_AL_POMODORO.regex() + ".*").matcher(menu);
            if (pastaMatcher.find()) {
                pasta.addIngredients(pastaMatcher.group(1));
                pasta.addIngredients(pastaMatcher.group(2));
            }
            return Collections.singletonList(pasta);
        }

        return Collections.emptyList();
    }

    private List<Course> findSecondCourses(List<Course> dailyCourses, String menuText) {
        return Stream.of(findDailySecondCourses(dailyCourses), findUsualSecondCourses(menuText))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Course> findDailySecondCourses(List<Course> dailyCoursesList) {
        return dailyCoursesList.subList(Menu.DAILY_FIRST_COURSES_NUM,
                Menu.FIRST_COURSES_NUM + Menu.DAILY_SECOND_COURSES_NUM);
    }

    private List<Course> findUsualSecondCourses(String menu) {
        return Stream.of(UsualCourseData.MOZZARELLA, UsualCourseData.FORMAGGI_MISTI, UsualCourseData.PIATTI_FREDDI)
                .filter(ucd -> menuContainsUsualCourse(menu, ucd))
                .map(Course::fromUsualCourse)
                .collect(Collectors.toList());
    }

    private List<Course> findSideCourses(List<Course> dailyCourses, String menuText) {
        return Stream.of(findDailySideCourses(dailyCourses), findUsualSideCourses(menuText))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Course> findDailySideCourses(List<Course> dailyCoursesList) {
        return Collections.singletonList(dailyCoursesList.get(dailyCoursesList.size() - 1));
    }

    private List<Course> findUsualSideCourses(String menu) {

        if (menuContainsUsualCourse(menu, UsualCourseData.INSALATA_MISTA)) {
            return Collections.singletonList(Course.fromUsualCourse(UsualCourseData.INSALATA_MISTA));
        }
        return Collections.emptyList();
    }

    private Course findUsualFruitCourse(String menu) {

        if (menuContainsUsualCourse(menu, UsualCourseData.FRUTTA_DI_STAGIONE)) {
            return Course.fromUsualCourse(UsualCourseData.FRUTTA_DI_STAGIONE);
        }

        Course dummyFruitCourse = new Course();
        dummyFruitCourse.setName("Niente frutta oggi? Strano...");
        return dummyFruitCourse;
    }

    private boolean menuContainsUsualCourse(String menu, UsualCourseData usualCourseData) {
        return menu.matches("(?s).*" + usualCourseData.regex() + ".*");
    }

    private Course findTakeAwayBasket(String menu) {

        Course takeAwayBasket = Course.fromUsualCourse(UsualCourseData.CESTINO);

        Matcher basketMatcher = Pattern.compile(".*" + UsualCourseData.CESTINO.regex() + ".*").matcher(menu);

        if (basketMatcher.find()) {
            takeAwayBasket.addIngredients(basketMatcher.group(1));
            takeAwayBasket.addIngredients(basketMatcher.group(2));
        } else {
            // course not found, adding a dummy course ingredient
            takeAwayBasket.addIngredients("chissà cosa ci metteranno oggi...");
        }

        return takeAwayBasket;
    }
}