package com.nhnacademy;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;

public class JsonData {
 	public String date() {
        ZonedDateTime now = ZonedDateTime.now();
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
    }

    public int size(String str) {
        return str.length();
    }

    public String responseBody(String message){
        String url = message.replace("\r\n","");

        // FIXME: 200 400 300 이것들도 조건문 하기
        // FIXME: FAIL 떴을 때 예외처리로 넘어가서 서버 끄는 식으로 ..
        String result ="";
        result += "HTTP/1.1 200 OK\n";
        result += date() + "\n";
        result += "Content-Type: application/json\n";
        result += "Content-Length: +"+size(url)+"\n";
        result += "Connection: keep-alive\n";
        result += "Server: gunicorn/19.9.0\n";
        result += "Access-Control-Allow-Origin: *\n";
        result += "Access-Control-Allow-Credentials: true\n";

        return result;
    }

    public JSONObject parseJson(String message) {
        JSONObject jsonObject = new JSONObject();

        message = message.replaceAll("\n", "");
        String method = message.split("GET /")[1];

        if (method.contains("?")) {
            jsonObject.put("args", parseArg(method));
        } else {
            jsonObject.put("args", new JSONObject());
        }

        jsonObject.put("headers", parseHost(message));
        // FIXME : ORIGIN IP로 바꿔야 함
        jsonObject.put("origin", "127.0.0.1");
        jsonObject.put("url", parseUrl(message));

        String ld = jsonObject.toJSONString();

        return jsonObject;
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
        String host = message.split("Host:")[1].split("User-Agent")[0].split("\r")[0];

        header.put("Accept", "*/*");
        header.put("Host", host);
        header.put("User-Agent", "curl/7.64.1");

        return header;
    }

    public String parseUrl(String message) {
        String url = message.split("Host:")[1].split("User-Agent")[0].split("\r")[0];
        url = "http://" + url;
        return url;
    }

}
