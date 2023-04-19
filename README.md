# AMMensa

This project enables an easy way to get the daily cafeteria menu in the Salerno University campus.

Usually, to know what's on the menu, the diner of the cafeteria needs to rummage every day through the institutional
website, download a PDF and open it on his/her device. Not to mention when the current day menu is not available yet:
you'll find yourself downloading an old menu with no way to noticing it.  
To me as a student, this was just a clear waste of time and disk space.

AMMensa checks every morning for the cafeteria PDF menu, downloads it, parses it, and finally makes it accessible
through a Web page.

The project is hosted on Render: https://ammensa.onrender.com/

### Built with

- Java 11
- Docker
- Spring Boot, Spring WebFlux
- JUnit 5
- [ANTLR](https://www.antlr.org/)
- [iTextPDF 5](https://github.com/itext/itextpdf)
- [jsoup](https://www.jsoup.org/)
- [Thymeleaf](https://www.thymeleaf.org/)
- [Nitrite Database](https://www.dizitart.org/nitrite-database.html)
- [Holidays](https://github.com/gdepourtales/holidays)
- [Mockito](https://site.mockito.org)