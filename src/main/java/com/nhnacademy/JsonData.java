package com.nhnacademy;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

public class JsonData {
 	public String date() {
        ZonedDateTime now = ZonedDateTime.now();
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
    }

    public int size(String str) {
        return str.length();
    }

    public String body(String message){
        String url = message.replace("\r\n","");

        String result ="";
        result += "{\n";
        result += "\"args\": {},\n";
        result += "  \"headers\": {\n";
        result += " \"Accept\": \"*/*\",";
        result += "\"Host\": \"test-vm.com\",\n";
        result += "\"User-Agent\": \"curl/7.64.1\"\n";
        result += " },\n";
        result += " \"origin\": \"103.243.200.16\",\n";
        result += " \"url\": \"http://test-vm.com/get\"\n";
        result += "}\n";

//        splitLine[2] + " 200 OK\n" + "Date: " + server.date() +"\n" + "Content-Type: application/json\n" +
//            "Content-Length: " + server.size(server.bodyMake(splitLine[1])) + "\n" +
//            "Connection: keep-alive\n" +
//            "Server: gunicorn/19.9.0\n" +
//            "Access-Control-Allow-Origin: *\n" +
//            "Access-Control-Allow-Credentials: true\n\n" + server.bodyMake(splitLine[1])+"\n";


        return result;
    }

    public String parseJson(String message) {
        JSONObject jsonObject = new JSONObject();

        message = message.replaceAll("\n", "");
        String method = message.split("GET /")[1];

        if (method.contains("?")) {
            jsonObject.put("args", parseArg(method));
        } else {
            jsonObject.put("args", new JSONObject() + "\\r\\n");
        }

        jsonObject.put("headers", parseHost(message));
        // FIXME : ORIGIN IP로 바꿔야 함
        jsonObject.put("origin", "127.0.0.1");
        jsonObject.put("url", parseUrl(message));

        String ld = jsonObject.toJSONString();
        System.out.println(ld);

        return jsonObject.toJSONString();
    }

    public JSONObject parseArg(String method) {
        JSONObject args = new JSONObject();
        String[] url = method.split("get\\?")[1].split(" HTTP")[0].split("&");

        for (int i = 0; i < url.length; i++) {
            String key = url[i].split("=")[0];
            String value = url[i].split("=")[1].split(" ")[0];
            args.put(key, value);
        }
        return args;
    }

    public JSONObject parseHost(String message) {
        JSONObject header = new JSONObject();
        String host = message.split("Host:")[1].split("User-Agent")[0];

        header.put("Accept", "*/*");
        header.put("Host", host);
        header.put("User-Agent", "curl/7.64.1");

        return header;
    }

    public String parseUrl(String message) {
        String url = message.split("Host:")[1].split("User-Agent")[0];
        url = "http://" + url;
        return url;
    }

}
