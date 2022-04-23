package com.nhnacademy;

import org.json.simple.JSONObject;

public class Parser {
    private final JsonData jsonData;

    public Parser(JsonData jsonData) {
        this.jsonData = jsonData;
    }

    public String parseContentTypeFromArgs() {
        return jsonData.getMessage().split("Content-Type: ")[1].split("\\{")[0].split(" HTTP")[0];
    }

    public String parseDataFromArgs() {
        return jsonData.getMessage().split("\u0000")[0].split(" \\/")[1].split(" HTTP")[0];
    }

    public String responseBody() {
        String url = jsonData.getMessage().replace("\r\n", "");
        // FIXME: 3. 200 400 300 이것들도 조건문 하기
        // FIXME: 4. FAIL 떴을 때 예외처리로 넘어가서 서버 끄는 식으로 ..
        String result = "";
        result += "HTTP/1.1 200 OK\n";
        result += JsonData.date() + "\n";
        result += "Content-Type: application/json\n";
        result += "Content-Length: +" + JsonData.size(url) + "\n";
        result += "Connection: keep-alive\n";
        result += "Server: gunicorn/19.9.0\n";
        result += "Access-Control-Allow-Origin: *\n";
        result += "Access-Control-Allow-Credentials: true\n";

        return result;
    }

    public JSONObject parseJson() {
        JSONObject jsonObject = new JSONObject();
        String message = jsonData.getMessage();

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
            if (jsonData.getBody().isEmpty()) {
                jsonObject.put("args", new JSONObject());
                jsonObject.put("data", parseJsonFromArg(request).toJSONString());
                jsonObject.put("files", "");
                jsonObject.put("form", "");
            } else {
                jsonObject.put("args", new JSONObject());
                jsonObject.put("data", "");
                jsonObject.put("files", parseFile());
                jsonObject.put("form", "");
            }
        }

        jsonObject.put("headers", parseHeader());
        if (message.contains("POST ")) {
            if (message.contains("\\{")) {
                jsonObject.put("json", parseData());
            } else {
                jsonObject.put("json", "");
            }
        }
        jsonObject.put("origin", jsonData.getIsa().getHostName());
        jsonObject.put("url", parseUrl());

        return jsonObject;
    }

    public JSONObject parseData() {
        String[] rawData =
            parseDataFromArgs().split("\\{ ")[1].split("\\ }")[0].replace("\"", "")
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

    public JSONObject parseJsonFromArg(String method) {
        JSONObject args = new JSONObject();
        String[] url = method.split("\\{ ")[1].split("\\ }")[0].split(",");

        for (int i = 0; i < url.length; i++) {
            String key = url[i].split(":")[0];
            String value = url[i].split(":")[1];
            args.put(key, value);
        }
        return args;
    }

    public JSONObject parseHeader() {
        JSONObject header = new JSONObject();
        String message = jsonData.getMessage();
        String host = message.split("Host:")[1].split("User-Agent")[0].split("\r")[0];
        if (message.contains("GET ")) {
            header.put("Accept", "*/*");
            header.put("Host", host);
            header.put("User-Agent", "curl/7.64.1");

        } else if (message.contains("POST ")) {
            header.put("Accept", "*/*");
            header.put("Host", host);
            header.put("User-Agent", "curl/7.64.1");
            header.put("Content-Type", parseContentTypeFromArgs());
            if (message.contains("multipart/form-data")) {
                header.put("Content-Length", JsonData.size(jsonData.getBody()));
                header.put("Connection:", "keep-alive");
                header.put("Server: ", "gunicorn/19.9.0");
                header.put("Access-Control-Allow-Origin: ", "*");
                header.put("Access-Control-Allow-Credentials: ", "true");
            } else {
                header.put("Content-Length", JsonData.size(parseDataFromArgs()));
            }
        }
        return header;
    }

    public String parseUrl() {
        String url = jsonData.getMessage().split("Host:")[1].split("User-Agent")[0].split("\r")[0];
        return "http://" + url;
    }

    public JSONObject parseFile() {
        JSONObject uploadData = new JSONObject();
        String semiData = jsonData.getBody().split("\n")[2].split(":")[1];
        String result = jsonData.getBody()
            .split(semiData)[1].replace("\n", "")
            .replace(" ", "");
        uploadData.put("upload", result);
        return uploadData;
    }
}
