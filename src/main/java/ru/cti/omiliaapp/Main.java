package main.java.ru.cti.omiliaapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.java.ru.cti.omiliaapp.actions.GetURL;
import main.java.ru.cti.omiliaapp.actions.Request;

import javax.security.auth.callback.TextInputCallback;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        final String CONNECTION_URL = "connection_url";
        final String PROCESS_URL = "process_url";
        final String PROPERTIES_FILE = "app.properties";
        String utterance = null;
        Scanner scanner = new Scanner(System.in);
        String dialogId = null;

        //Читаем содержимое файла из resources
        ArrayList<String> sb = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("src/resources/testSet.txt"))) {

            String line;
            while ((line = br.readLine()) != null) {
                sb.add(line);
                sb.add(System.lineSeparator());
            }
        }

        for (int i=0; i < sb.size(); i+=1){
            GetURL appProperties = new GetURL();
            String connectionURL = appProperties.getURL(CONNECTION_URL, PROPERTIES_FILE);
            String processURL = appProperties.getURL(PROCESS_URL, PROPERTIES_FILE);
            //Сделать запрос "Start new dialog"
            Request request = new Request();

            //Вывести на экран сообщения бота после Start new dialog
            JsonObject response = request.makeRequest(connectionURL, utterance, dialogId);
            showBotMessages(response, utterance);
            dialogId = getDialogId(response);

            utterance = sb.get(i).trim();

            response = request.makeRequest(processURL, utterance, dialogId);
            showBotMessages(response, utterance);

            //закрыть чатик
            JsonElement actionType =  response.getAsJsonObject("action");
            if (!actionType.getAsJsonObject().get("type").toString().replaceAll("\"", "").equals("TRANSFER"))
                request.makeRequest(processURL, "[hup]", dialogId);
            dialogId = null;
            utterance = null;
        }
    }

    private static String getDialogId(JsonObject response) {
        return response.get("dialogId").toString();
    }

    private static void showBotMessages(JsonObject response, String utterance) throws IOException {
        System.out.println(response.toString());
        if (response.has("fields") == true) {
            ArrayList<String> listOfFieldsName = new ArrayList<>();
            for(int i = 0; i < response.getAsJsonArray("fields").size(); i++) {
                JsonObject intentData = (JsonObject) response.getAsJsonArray("fields").get(i);
                listOfFieldsName.add(intentData.get("name").toString().replaceAll("\"",""));
            }
            if (listOfFieldsName.contains("Intent")) {
                for (int i = 0; i < response.getAsJsonArray("fields").size(); i++) {
                    JsonObject intentData = (JsonObject) response.getAsJsonArray("fields").get(i);
                    JsonObject instances = (JsonObject) intentData.getAsJsonArray("instances").get(0);
                    if (intentData.get("name").toString().replaceAll("\"","").equals("Intent"))
                        System.out.println(utterance + " ---> intent  " + instances.get("value"));

                }
            }
            else if (utterance != null && !utterance.isEmpty())
                System.out.println(utterance + " ---> NO MATCH intent  " );
        }
    }
}
