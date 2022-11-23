package net.ammensa.entity;


import com.fasterxml.jackson.annotation.JsonFormat;

/* https://www.baeldung.com/jackson-serialize-enums */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MenuStatus {

    ERROR(-1, "C'è stato un errore!"),
    MENSA_CLOSED(0, "Oggi la mensa è chiusa."),
    OK(1, ""),
    NOT_AVAILABLE(2, "Il menu non è ancora disponibile. Fame? :)"),
    STILL_NOT_AVAILABLE(2, "Il menu non è disponibile oggi... Pranzo a sorpresa :p"),
    TOO_EARLY(2, "Già pensi al pranzo?"),
    STILL_TOO_EARLY(2, "È ancora presto per andare a mensa. Non ti distrarre.");

    private int code;
    private String message;

    MenuStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}