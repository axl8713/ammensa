package net.ammensa.handler;

import net.ammensa.entity.Menu;
import net.ammensa.entity.MenuStatus;
import net.ammensa.parse.MenuParser;
import net.ammensa.pdf.PdfConversion;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.file.Files;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuRenderTest {

    private TemplateEngine templateEngine = createTemplateEngine();

    private static SpringTemplateEngine createTemplateEngine() {

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setTemplateMode("HTML");

        templateEngine.setTemplateResolver(templateResolver);

        templateEngine.setEnableSpringELCompiler(true);

        return templateEngine;
    }

    @Test
    void menuDateRenderTest() throws Exception {

        String menuText = new PdfConversion()
                .toText(Files.readAllBytes(new ClassPathResource("menu/VENERDI' 01 MARZO 2019 PRANZO.pdf")
                        .getFile().toPath()));

        Menu menu = new MenuParser().parseMenu(menuText);

        Context context = new Context();
        context.setLocale(Locale.ITALY);
        context.setVariable("menu", menu);
        context.setVariable("status", MenuStatus.OK);

        String process = templateEngine.process("menu.html", context);

        assertTrue(process.contains("venerdì 1 marzo 2019"));
    }
}