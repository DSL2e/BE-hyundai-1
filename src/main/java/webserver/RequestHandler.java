package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;
import db.Database;
import model.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.*;

public class RequestHandler implements Runnable {
    private Socket connection;
    private final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final HashMap<String, HTTPMethod> httpMethods = initializeHttpMethods();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            RequestHeader requestHeader = new RequestHeader();

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            ParsingService parsingService = new ParsingService(br, requestHeader);
            String path = requestHeader.getPath();
            String method = requestHeader.getHttpMethod();
            logger.debug(">>  {} >> {}", method, path);

            HTTPMethod httpMethod =  httpMethods.get(method);
            httpMethod.process(path, requestHeader);

            DataOutputStream dos = new DataOutputStream(out);
            String detailPath = httpMethod.getDetailPath();
            String contentType = httpMethod.getContentType();


            // 상태별로 처리 분류 => method가 추가되면 추가
            if (detailPath.equals("404")) {
                String errorPageContent = "<html><head><title>404 Not Found</title></head><body><h1>404 Not Found</h1></body></html>";
                byte[] body = errorPageContent.getBytes(StandardCharsets.UTF_8);
                response400Header(dos, body.length);
                responseBody(dos, body);
            } else {
                byte[] body = Files.readAllBytes(new File(detailPath).toPath());
                response200Header(dos, body.length, contentType);
                responseBody(dos, body);
            }

        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static HashMap<String, HTTPMethod> initializeHttpMethods() {
        HashMap<String, HTTPMethod> actions = new HashMap<>();
        actions.put("GET", new GetService());
        //actions.put("POST", new PostService());
        return actions;
    }

    private void response400Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String type) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+type+"\r\n");
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
