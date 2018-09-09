package org.proy.lydia.review_assist;

import org.proy.lydia.LydiaClient;
import org.proy.lydia.LydiaServer;
import org.proy.lydia.review_assist.ReviewAssistant;
import org.proy.lydia.review_assist.ReviewJobObj;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ReviewTest {
    public static void main(String[] args) throws InterruptedException, IOException {
        BlockingQueue<ReviewJobObj> reviewJobBuffer = new LinkedBlockingQueue<>();
        BlockingQueue<ReviewJobObj> toSendBuffer = new LinkedBlockingQueue<>();

        reviewJobBuffer.put(new ReviewJobObj("12345", "asdfasdfasddf"));
        Thread LydiaServer = new Thread(new LydiaServer(reviewJobBuffer, toSendBuffer));
        Thread reviewAssistant = new Thread(new ReviewAssistant(reviewJobBuffer, toSendBuffer));
        LydiaServer.start();
        reviewAssistant.start();

        TimeUnit.MILLISECONDS.sleep(500);
        LydiaClient commandClient = new LydiaClient(LydiaClient.COMMAND_PORT);
        commandClient.startClient();
        //LydiaClient reviewClient = new LydiaClient(LydiaClient.REVIEW_ASSIST_PORT);

        commandClient.send("start");

//        toSendBuffer.put(new ReviewJobObj("88888","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("99999","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("99999","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("99999","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("99999","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("00000","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("00000","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("00000","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("00000","asdfxcvqwer"));
//        TimeUnit.SECONDS.sleep(1);
//        reviewClient.startClient();
//        toSendBuffer.put(new ReviewJobObj("88888","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("88888","asdfxcvqwer"));
//        toSendBuffer.put(new ReviewJobObj("88888","asdfxcvqwer"));
    }
}
