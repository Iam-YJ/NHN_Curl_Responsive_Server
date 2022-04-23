package com.nhnacademy;

import java.net.InetSocketAddress;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class JsonData {
    private final InetSocketAddress isa;
    private final String message;
    private final String body;

    public JsonData(InetSocketAddress isa, String message, String body) {
        this.isa = isa;
        this.message = message.replace("\r\n", "");
        this.body = body;
    }

    public InetSocketAddress getIsa() {
        return this.isa;
    }

    public String getMessage() {
        return this.message;
    }

    public String getBody() {
        return this.body;
    }

    public static String date() {
        ZonedDateTime now = ZonedDateTime.now();
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
    }

    public static int size(String str) {
        return str.length();
    }
}
