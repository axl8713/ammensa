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

        final List<IngredientsContext> ctxs = ctx.ingredients();

        for (int i = 0; i < ctxs.size(); i++) {
            if (visit(ctxs.get(i)) != null) {
                extractedCourse.addIngredients(visit(ctxs.get(i)).trim());
            }
        }
        return null;
    }

    @Override
    public String visitIngredients(net.ammensa.parse.antlr.CourseGrammarParser.IngredientsContext ctx) {

        String text = new String();
        final List<IngredientsContext> ctxs = ctx.ingredients();

        if (ctxs.size() != 0) {
            for (int i = 0; i < ctxs.size(); i++) {
                text = text.concat(visit(ctxs.get(i)));
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