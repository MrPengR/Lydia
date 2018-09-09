package org.proy.lydia;

import org.proy.lydia.review_assist.ReviewJobObj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class LydiaServer implements Runnable{
    private final String HOST = "127.0.0.1";
    private final int COMMAND_PORT = 6666;
    private final int REVIEW_ASSIST_PORT = 6667;

    private ServerSocket commandServer;
    private Socket commandClient;
    private PrintWriter commandOutput;
    private BufferedReader commandInput;
    // TODO: 27.08.18 there are other Queues waiting to be added

    public LydiaServer(BlockingQueue<ReviewJobObj> reviewJobBuffer, BlockingQueue<ReviewJobObj> toSendBuffer) throws IOException, InterruptedException {
        this.reviewJobBuffer = reviewJobBuffer;
        this.toSendBuffer = toSendBuffer;
        commandServer = new ServerSocket(COMMAND_PORT);
    }

    @Override
    public void run() {
        System.out.println("--> Listening now commands at port "+ COMMAND_PORT);
        try {
            commandClient = commandServer.accept();
            System.out.println("--> Command port successfully connected");
            commandOutput = new PrintWriter(commandClient.getOutputStream(), true);
            commandInput = new BufferedReader(new InputStreamReader(commandClient.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String input;
        while (!exit){
            try {
                input = commandInput.readLine();
                System.out.println("[Command Server got message " + input + "]" );
                commandOutput.println("--> Got command "+ input);
                if (input.equals("start")){
                    if (!reviewAssistServerRunning) {
                        new Thread(new ReviewAssistServer()).start();
                        reviewAssistServerRunning = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private volatile boolean exit = false;
    public void stopServer(){
        exit = true;
    }




    private BlockingQueue<ReviewJobObj> reviewJobBuffer = new LinkedBlockingQueue<ReviewJobObj>();
    private BlockingQueue<ReviewJobObj> toSendBuffer = new LinkedBlockingQueue<ReviewJobObj>();
    private boolean reviewAssistServerRunning = false;

    private class ReviewAssistServer implements Runnable{
        private ServerSocket reviewServer;
        private Socket reviewClient;
        private PrintWriter reviewOutput;
        private BufferedReader reviewInput;
        Sender sender = new Sender();

        public ReviewAssistServer() throws IOException {
            reviewServer = new ServerSocket(REVIEW_ASSIST_PORT);
        }

        @Override
        public void run() {
            String input;
            System.out.println("--> Listening now review jobs at port "+ REVIEW_ASSIST_PORT);
            try {
                reviewClient = reviewServer.accept();
                System.out.println("--> Review port successfully connected");
                new Thread(sender).start();
                reviewOutput = new PrintWriter(reviewClient.getOutputStream(), true);
                reviewInput = new BufferedReader(new InputStreamReader(reviewClient.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!exit){
                try {
                    input = reviewInput.readLine();
                    if (input != null){
                        String msgID = input.split("->")[0];
                        String filePath = input.split("->")[1];
                        ReviewJobObj reviewJobObj = new ReviewJobObj(msgID, filePath);
                        reviewJobBuffer.put(reviewJobObj);
//                    reviewOutput.println("--> Got new job " + input);
//                        System.out.println("[Review Server got message " + input + "]");
//                        System.out.println("--> job buffer now is " + reviewJobBuffer );
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private class Sender implements Runnable{
            @Override
            public void run() {
                while (!exit){
                    if (!toSendBuffer.isEmpty()){
                        try {
                            ReviewJobObj tmpObj = toSendBuffer.take();
                            reviewOutput.println("#" + tmpObj.getMsgID() + "@" + tmpObj.getFilePath());
                            reviewOutput.flush();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private volatile boolean exit = false;
        public void stopServer(){
            exit = true;
        }
    }
}
