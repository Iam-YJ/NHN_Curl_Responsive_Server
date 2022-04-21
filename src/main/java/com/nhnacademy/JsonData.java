package com.nhnacademy;

import org.json.simple.JSONObject;

public class JsonData {


    // 목적 : json 형식의 출력 데이터 만들기

    //GET / HTTP/1.1
    //Host: 127.0.0.1
    //User-Agent: curl/7.79.1
    //Accept: */*

    // {
    //  "args": {},
    //  "headers": {
    //    "Accept": "*/*",
    //    "Host": "test-vm.com",
    //    "User-Agent": "curl/7.64.1"
    //  },
    //  "origin": "103.243.200.16",
    //  "url": "http://test-vm.com/get"
    //}

    public void parseJson(String message) {
        JSONObject jsonObject = new JSONObject();

        message = message.replaceAll("\n", "");

        jsonObject.put("args", new JSONObject()+"\\r\\n");

        jsonObject.put("headers", parseHost(message)+"\\r\\n");
        // TODO ORIGIN IP로 바꿔야 함
        jsonObject.put("origin", "127.0.0.1"+"\\r\\n");
        jsonObject.put("url", parseUrl(message)+"\\r\\n");

        System.out.println(jsonObject.toJSONString());

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
