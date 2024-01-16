package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import model.RequestHeader;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            RequestHeader requestHeader = new RequestHeader();
            ParsingService parsingService = new ParsingService(br,requestHeader);

            // 특정 유용한 request header만 추출
            logger.debug("====useful RequestHeader====");
            logger.debug("{} ", requestHeader.getGeneralHeader());
            logger.debug("{} ", requestHeader.getAccpet());
            logger.debug("{} ", requestHeader.getAccept_encoding());
            logger.debug("{} ", requestHeader.getAccept_language());
            logger.debug("{} ", requestHeader.getUpgrade_insecure_requests());
            logger.debug("===========================");

            // Path추출
            String path = requestHeader.getPath();
            String HTTP_method = requestHeader.getHTTP_method();

            logger.debug("------>  {} ", requestHeader.getPath());
            logger.debug("------>  {} ", requestHeader.getHTTP_method());

            // localhost:8080 호출하더라도 index.html로 이어짐
            if(path.equals("/")) path = "/index.html";

            
            //회원 정보 추출
            if(path.contains("/create")){
                String temp = path.split("\\?")[1];
                String[] temp2 = temp.split("&");

                String[] imformation = new String[4];

                for(int i=0; i< temp2.length; i++){
                    imformation[i] = temp2[i].split("=")[1];
                }
                User user = new User(imformation[0],imformation[1],imformation[2],imformation[3]);
                logger.debug(">> user : {} ", user);

                // 메인페이지로 이어지도록 설정
                path = "/index.html";
            }

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./src/main/resources/templates" + path).toPath());

            //응답헤더
            response200Header(dos, body.length);
            //본문
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
