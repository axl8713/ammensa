package net.ammensa.parse;

import net.ammensa.entity.Menu;
import net.ammensa.pdf.PdfConversion;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MenuParserTest {

    @Test
    void menuParsingTest() throws Exception {

        PdfConversion pdfConversion = new PdfConversion();

        List<Path> menusFiles = Files.list(new ClassPathResource("menu").getFile().toPath())
                .collect(Collectors.toList());

        for (Path menuFile : menusFiles) {

            String menuText = pdfConversion.toText(Files.readAllBytes(menuFile));

            MenuParser menuParser = new MenuParser();
            Menu menu = menuParser.parseMenu(menuText);

            assertEquals(3, menu.getFirstCourses().size());
            assertEquals(5, menu.getSecondCourses().size());
            assertEquals(2, menu.getSideCourses().size());
            assertNotNull(menu.getFruitCourse());
            assertNotNull(menu.getTakeAwayBasketContent());
        }
    }

    @Test
    public void parseMenuWithoutAnUsualCourseTest() throws Exception {

        String menuText = new PdfConversion()
                .toText(Files.readAllBytes(new ClassPathResource("menu/MARTEDI' 19 NOVEMBRE 2019  PRANZO.pdf")
                        .getFile().toPath()));

        String regex = UsualCourseData.MOZZARELLA.regex();
        menuText = menuText.replaceFirst(regex, "");

        Menu menu = new MenuParser().parseMenu(menuText);

        assertEquals(3, menu.getFirstCourses().size());
        assertEquals(4, menu.getSecondCourses().size());
        assertEquals(2, menu.getSideCourses().size());
        assertNotNull(menu.getFruitCourse());
        assertNotNull(menu.getTakeAwayBasketContent());
    }

    @Test
    public void pastaRegexTest() {

        /* GIOVEDI' 18 APRILE 2019 PRANZO.pdf */
        final String pasta_1 = " Pasta in bianco o al pomodoro (aglio, basilico, olio extra vergine d'oliva, sale) / riso in bianco \n"
                + " Pasta in white or tomato sauce (garlic, basil, extra virgin olive oil, salt) / white rice ";

        /* VENERDI' 01 MARZO 2019 PRANZO.pdf */
        final String pasta_2 = " Pasta in bianco o al pomodoro[aglio, basilico, olio extra vergine d'oliva, sale] / riso in bianco \n"
                + "(Pasta with extra virgin olive oil /or with tomato[garlic, basil, extra virgin olive oil, salt] sauce / boiled rice, salt) ";

        /* MARTEDI' 19 NOVEMBRE 2019  PRANZO.pdf */
        final String pasta_3 = "Pasta (glutine) in bianco o al pomodoro (aglio, basilico, olio extra vergine d'oliva, sale) / riso in bianco \n" +
                "Boiled pasta (gluten) or with tomato sauce (garlic, basil, extra virgin olive oil, salt) / boiled rice ";

        String regex = UsualCourseData.PASTA_IN_BIANCO_O_AL_POMODORO.regex();
        Pattern pattern = Pattern.compile(".*" + regex + ".*");

        assertTrue(pattern.matcher(pasta_1).matches());
        assertTrue(pattern.matcher(pasta_2).matches());
        assertTrue(pattern.matcher(pasta_3).matches());
    }

    @Test
    public void mozzarellaRegexTest() {

        /* GIOVEDI' 18 APRILE 2019 PRANZO.pdf */
        final String mozzarella_1 = "Mozzarella di bufala - Fresh buffalo mozzarella ";
        /* MARTEDI' 19 NOVEMBRE 2019  PRANZO.pdf */
        final String mozzarella_2 = "Mozzarella di Bufala (latte) - Fresh Buffalo Mozzarella (milk) ";

        String regex = UsualCourseData.MOZZARELLA.regex();
        Pattern pattern = Pattern.compile(".*" + regex + ".*");

        assertTrue(pattern.matcher(mozzarella_1).matches());
        assertTrue(pattern.matcher(mozzarella_2).matches());
    }

    @Test
    public void formaggiMistiRegexTest() {

        /* GIOVEDI' 18 APRILE 2019 PRANZO.pdf */
        final String formaggi_1 = "Formaggi misti     - Mixed cheeses ";
        /* MARTEDI' 19 NOVEMBRE 2019  PRANZO.pdf */
        final String formaggi_2 = "Formaggi Misti (latte)     - Mixed Cheeses (milk) ";

        String regex = UsualCourseData.FORMAGGI_MISTI.regex();
        Pattern pattern = Pattern.compile(".*" + regex + ".*");

        assertTrue(pattern.matcher(formaggi_1).matches());
        assertTrue(pattern.matcher(formaggi_2).matches());
    }
}