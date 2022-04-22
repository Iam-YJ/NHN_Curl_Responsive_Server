package com.nhnacademy;

import java.net.InetSocketAddress;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;

// FIXME 1. POST - 추가한 JSON 데이터 거꾸로 나옴
// FIXME 2. GET - 추가한 데이터 거꾸로 나옴
public class JsonData {
    private final InetSocketAddress isa;
    private String message;


    public JsonData(InetSocketAddress isa, String message) {
        this.isa = isa;
        this.message = message.replace("\r\n", "");
    }


    //POST /post HTTP/1.1
    //Host: 127.0.0.1
    //User-Agent: curl/7.79.1
    //Accept: */*
    //Content-Length: 34
    //Content-Type: application/x-www-form-urlencoded
    //
    //{ "msg1": "hello","msg2":"world" }

    public String parseContentTypeFromArgs(String message) {
        return message.split("Content-Type: ")[1].split("\\{")[0];
    }

    public String parseDataFromArgs(String message) {
        return "{" + message.split("\\{")[1];
    }


    public String date() {
        ZonedDateTime now = ZonedDateTime.now();
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
    }

    public int size(String str) {
        return str.length();
    }

    public String responseBody(String message) {
        String url = message.replace("\r\n", "");

        // FIXME: 200 400 300 이것들도 조건문 하기
        // FIXME: FAIL 떴을 때 예외처리로 넘어가서 서버 끄는 식으로 ..
        String result = "";
        result += "HTTP/1.1 200 OK\n";
        result += date() + "\n";
        result += "Content-Type: application/json\n";
        result += "Content-Length: +" + size(url) + "\n";
        result += "Connection: keep-alive\n";
        result += "Server: gunicorn/19.9.0\n";
        result += "Access-Control-Allow-Origin: *\n";
        result += "Access-Control-Allow-Credentials: true\n";

        return result;
    }

    public JSONObject parseJson(String message) {
        JSONObject jsonObject = new JSONObject();

        message = message.replaceAll("\r", "").replaceAll("\n", "");

        if (message.contains("GET ")) {
            String request = message.split("GET \\/")[1];
            if (message.split("GET \\/")[1].contains("?")) {
                jsonObject.put("args", parseArg(request));
            } else {
                jsonObject.put("args", new JSONObject());
            }

        } else if (message.contains("POST ")) {
            String request = message.split("Content-Type: ")[1];
            if (message.contains("\\{")) {
                jsonObject.put("args", parseArg(request));
            } else {
                jsonObject.put("args", new JSONObject());
            }
        }

        jsonObject.put("headers", parseHost(message));
        if (message.contains("POST ")) {
            jsonObject.put("json", parseData(message));
        }
        jsonObject.put("origin", isa.getHostName());
        jsonObject.put("url", parseUrl(message));

        return jsonObject;
    }

    public String parseContentType(String message) {
        return parseContentTypeFromArgs(message);
    }

    public JSONObject parseData(String message) {
        String[] rawData =
            parseDataFromArgs(message).split("\\{ ")[1].split("\\ }")[0].replace("\"", "")
                .split(",");
        JSONObject jsonObject = new JSONObject();

        for (int i = 0; i < rawData.length; i++) {
            jsonObject.put(rawData[i].split(":")[0].replace("\"", "")
                , rawData[i].split(":")[1].replace("\"", ""));
        }
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
        if (message.contains("GET ")) {
            //   "headers": {
            //    "Accept": "*/*",
            //    "Host": "test-vm.com",
            //    "User-Agent": "curl/7.64.1"
            //  },
            header.put("Accept", "*/*");
            header.put("Host", host);
            header.put("User-Agent", "curl/7.64.1");

        } else if (message.contains("POST ")) {
            //  "headers": {
            //    "Accept": "*/*",
            //    "Content-Length": "36",
            //    "Content-Type": "application/json",
            //    "Host": "test-vm.com",
            //    "User-Agent": "curl/7.64.1"
            //  },
            header.put("Accept", "*/*");
            header.put("Host", host);
            header.put("User-Agent", "curl/7.64.1");
            header.put("Content-Type", parseContentType(message));
            header.put("Content-Length", size(parseDataFromArgs(message)));
        }
        return header;
    }

    public String parseUrl(String message) {
        String url = message.split("Host:")[1].split("User-Agent")[0].split("\r")[0];
        url = "http://" + url;
        return url;
    }
}
