package com.nhnacademy;

import org.json.simple.JSONObject;

public class JsonData {

    public String parseJson(String message) {
        JSONObject jsonObject = new JSONObject();

        message = message.replaceAll("\n", "");

        jsonObject.put("args", new JSONObject());
        jsonObject.put("headers", parseHost(message));
        // FIXME : ORIGIN IP로 바꿔야 함
        jsonObject.put("origin", "127.0.0.1");
        jsonObject.put("url", parseUrl(message));
        String ld = jsonObject.toJSONString();
        System.out.println(ld);
        return jsonObject.toJSONString();

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
