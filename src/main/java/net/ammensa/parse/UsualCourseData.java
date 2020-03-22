package net.ammensa.parse;

public enum UsualCourseData {

    PASTA_IN_BIANCO_O_AL_POMODORO("Pasta in bianco o al pomodoro") {
        @Override
        public String regex() {
            return "(?i)Pasta\\s*(?:\\(\\s*glutine\\s*\\)\\s*)?in\\s*bianco.*?[\\(\\[](.+?)[\\)\\]].*?\\n\\s*(?:\\(|Boiled\\s*)?pasta\\s*(?:\\(\\s*gluten\\s*\\)\\s*)?.*?[\\(\\[](.+?)[\\)\\]].*?(?:\\n|\\s*$)";
        }
    },
    MOZZARELLA("Mozzarella di bufala - Fresh buffalo mozzarella") {
        @Override
        public String regex() {
            return "(?i)mozzarella\\s*di\\s*bufala\\s*(?:\\(\\s*latte\\s*\\)\\s*)?.*fresh\\s*buffalo\\s*mozzarella\\s+(?:\\(\\s*milk\\s*\\)\\s*)?";
        }
    },
    FORMAGGI_MISTI("Formaggi misti - Mixed cheeses") {
        @Override
        public String regex() {
            return "(?i)formaggi\\s*misti\\s*(?:\\(\\s*latte\\s*\\)\\s*)?.*mixed\\s*cheeses\\s+(?:\\(\\s*milk\\s*\\)\\s*)?";
        }
    },
    PIATTI_FREDDI("Piatti freddi - Cold plates"),
    INSALATA_MISTA("Insalata mista - Mixed salad") {
        @Override
        public String regex() {
            return "(?i)insalata\\s*mista\\s*\\(mixed salad\\)";
        }
    },
    FRUTTA_DI_STAGIONE("Frutta di stagione - Seasonal mix fruit"),
    CESTINO("Cestino - Take-away basket") {
        @Override
        public String regex() {
            return "(?i)cestino\\s*.\\s*\\b(.*?)\\s*\\n" +
                    "\\s*take\\s*away\\s*basket\\s*.\\s*(.*?)(?:\\n|\\s*$)";
        }
    };

    private String courseName;

    UsualCourseData(String courseName) {
        this.courseName = courseName;
    }

    public String regex() {
        String courseNameRegex = this.courseName.replace(" ", "\\s*");
        return "(?i)" + courseNameRegex;
    }

    public String courseName() {
        return courseName;
    }
}