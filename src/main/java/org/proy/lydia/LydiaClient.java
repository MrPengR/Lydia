package org.proy.lydia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LydiaClient implements Runnable{
    //private Socket
    public static final String LOCAL_HOST = "127.0.0.1";
    public static final int COMMAND_PORT = 6666;
    public static final int REVIEW_ASSIST_PORT = 6667;

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private int port;
    private List<String> toSendBuffer = new ArrayList<String>();
    private List<String> responseBuffer = new ArrayList<String>();
    public LydiaClient(int port) {
        this.port = port;
    }
    public void startClient(){
        try {
            clientSocket = new Socket(LOCAL_HOST, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    public List getResponse(){
        return responseBuffer;
    }

    @Override
    public void run() {
        while (!exit) {
            if(!toSendBuffer.isEmpty()){
                out.println(toSendBuffer.get(0));
                System.out.println("[Sending from client " + toSendBuffer.get(0) + "]" );
                toSendBuffer.remove(0);
            }
            try {
                String input = in.readLine();
                if (input != null){
                    responseBuffer.add(input);
                    System.out.println("[Client got message " + input + "]");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void send(String text){
        toSendBuffer.add(text);
    }


    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    private volatile boolean exit = false;
    public void stopClient(){
        exit = true;
    }
}
