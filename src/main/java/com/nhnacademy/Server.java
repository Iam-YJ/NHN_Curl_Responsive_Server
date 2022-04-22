//package com.nhnacademy;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintStream;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.time.LocalDateTime;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Server {
//    public String date() {
//        ZonedDateTime now = ZonedDateTime.now();
//        return DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
//    }
//    public int size(String str){
//        return str.length();
//    }
//    //    public String json(List <String> jsonData){
////        String temp ="";
////        for (String data:jsonData) {
////            temp +=
////        }
////        return "";
////    }
//    public String bodyMake(String path) {
//        String temp ="";
//        switch(path){
//            case "/ip":
//                String ip ="1.253.143.122";
//                temp ="{\"origin\": \""+ip+"\"\n }";
//                return temp;
//        }
//        return temp;
//    }
//
//    public String getMake(String path) {
//
//        System.out.println(path);
//        path = path.replace("\r\n","");
//        path.split()
//        // replace(), split(),
//
//        String result ="";
//        result += "{\n";
//        result += "\"args\": {},\n";
//        result += "  \"headers\": {\n";
//        result += " \"Accept\": \"*/*\",";
//        result += "\"Host\": \"test-vm.com\",\n";
//        result += "\"User-Agent\": \"curl/7.64.1\"\n";
//        result += " },\n";
//        result += " \"origin\": \"103.243.200.16\",\n";
//        result += " \"url\": \"http://test-vm.com/get\"\n";
//        result += "}\n";
//
//        return result;
//    }
//
//    public void connect() {
//        Server server = new Server();
//
//        try (ServerSocket serverSocket = new ServerSocket(80)) {
//            System.out.println("bye");
//            Socket socket = serverSocket.accept();
//            System.out.println("hi");
//            InputStream in = socket.getInputStream();
//            byte [] bytes = null;
//            String message = null;
//            bytes = new byte[100];
//            int readByteCount = in.read(bytes);
//            message = new String(bytes, 0 , readByteCount, "UTF-8");
//            String messageResult = getMake(message);
//
//
//
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            OutputStream out = socket.getOutputStream();
//            PrintStream ps = new PrintStream(out);
//
//            String line = null;
//            List<String> result = new ArrayList<>();
//            while ((line = br.readLine()) != null) {
//                result.add(line);
//                if (line.equals("")) break;
//
//
//
//            }
//            ////////////////////
//
//            String[] splitLine = result.get(0).split(" ");
//
//            String body =
//                splitLine[2] + " 200 OK\n" + "Date: " + server.date() +"\n" + "Content-Type: application/json\n" +
//                    "Content-Length: " + server.size(server.bodyMake(splitLine[1])) + "\n" +
//                    "Connection: keep-alive\n" +
//                    "Server: gunicorn/19.9.0\n" +
//                    "Access-Control-Allow-Origin: *\n" +
//                    "Access-Control-Allow-Credentials: true\n\n" + server.bodyMake(splitLine[1])+"\n";
//
//            System.out.println(body);
//
//            ps.print(body);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        Server s = new Server();
//        s.connect();
//    }
//}