package main.java.ru.cti.omiliaapp.actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Response {

    public Response() {
    }

    public static JsonObject returnJSONResponse (HttpsURLConnection con) throws IOException {
        int responseCode = con.getResponseCode();
//        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JsonElement jsonElement = new JsonParser().parse(response.toString());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return jsonObject;
    }
}
