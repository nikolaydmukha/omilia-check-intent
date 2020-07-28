package main.java.ru.cti.omiliaapp.actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class GetURL {

    public GetURL() {
    }

    public static String getURL(String urlIdentificator, String propFileName) {
        FileInputStream fis = null;
        Properties property = new Properties();
        String url = null;

        try {
            fis = new FileInputStream("src/resources/" + propFileName);
        } catch (FileNotFoundException fileNotFoundException) {
            url = fileNotFoundException.getMessage();
            return url;
        }
        try {
            property.load(fis);
            if (property.containsKey(urlIdentificator))
                return property.getProperty(urlIdentificator);
            else
                return "Ключ " + urlIdentificator + " не найден в " + propFileName;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            url = ioException.getMessage();
            return url;
        }
//        return url;
    }
}
