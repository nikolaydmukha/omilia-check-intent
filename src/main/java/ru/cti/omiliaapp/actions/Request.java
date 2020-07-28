package main.java.ru.cti.omiliaapp.actions;

import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public class Request {

    public Request() {
    }
    /***************************************************************************/
    // Если ругается на отсутствие сертификатов, то можно игнорировать все провекри на SSL сертификаты
    // Но этот способ не очень правильный Лучше добавить нужный сертификат в keystore
    public static JsonObject makeRequest(String url, String utterance, String dialogId) throws Exception {
        Response response = new Response();
        String jsonParams = null;

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
        }
        /*****************************************************************************/

        //JSON sending params
        jsonParams = "{\"application_id\":\"HCFB\",\"source\":\"chat\"}";
        if (dialogId != null) {
            url += dialogId.replace("\"", "");
            jsonParams = "{\"application_id\":\"HCFB\",\"utterance\":\"" + utterance + "\"}";
        }

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        //HEADER
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Charset", "UTF-8");
        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

        //Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(jsonParams);
        wr.write(jsonParams.getBytes("UTF-8"));      //текст латиницей помещается в один байт, а кириллица - нет. Поэтому
                                                                 //так передавать!
        wr.flush();
        wr.close();

        // Вернуть ответ в формате JSON
        if ("[hup]".equals(utterance)){
            response.returnJSONResponse(con);
            return null;
        }
        else {
            JsonObject responseJSON = response.returnJSONResponse(con);
            return responseJSON;
        }
    }

}
