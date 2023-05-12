package net.ammensa.parse.antlr;

import net.ammensa.entity.Course;
import net.ammensa.parse.antlr.CourseGrammarParser.IngredientsContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class CourseExtractorVisitor extends net.ammensa.parse.antlr.CourseGrammarBaseVisitor<String> {

    private ParseTree tree;
    private final Course extractedCourse = new Course();

    public CourseExtractorVisitor(ParseTree tree) {
        this.tree = tree;
    }

    public Course extractCourse() {
        this.visit(tree);
        return extractedCourse;
    }

    @Override
    public String visitCourse(net.ammensa.parse.antlr.CourseGrammarParser.CourseContext ctx) {

        extractedCourse.setName(ctx.name.getText().trim());

        final List<IngredientsContext> ingredientsContexts = ctx.ingredients();

        for (IngredientsContext ingredientsContext : ingredientsContexts) {
            if (visit(ingredientsContext) != null) {
                extractedCourse.addIngredients(visit(ingredientsContext).trim());
            }
        }
        return null;
    }

    @Override
    public String visitIngredients(net.ammensa.parse.antlr.CourseGrammarParser.IngredientsContext ctx) {

        String text = "";
        final List<IngredientsContext> ingredientsContexts = ctx.ingredients();

        if (!ingredientsContexts.isEmpty()) {
            for (IngredientsContext ingredientsContext : ingredientsContexts) {
                text = text.concat(visit(ingredientsContext));
            }
        } else {
            text = visit(ctx.content());
        }
        return text;
    }

    @Override
    public String visitContent(net.ammensa.parse.antlr.CourseGrammarParser.ContentContext ctx) {

        final StringBuilder text = new StringBuilder();

        for (TerminalNode node : ctx.TEXT()) {
            text.append(node.getText());
        }
        return text.toString();
    }
}