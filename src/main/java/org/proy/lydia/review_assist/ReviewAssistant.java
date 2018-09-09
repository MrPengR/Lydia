package org.proy.lydia.review_assist;

import org.proy.lydia.BaiduUtils.TTS;
import org.proy.lydia.BaiduUtils.OCR;
import org.proy.utils.ChangeIP;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ReviewAssistant implements Runnable{
    private OCR ocr;
    private TTS tts;
    private BlockingQueue<ReviewJobObj> jobBuffer;
    private BlockingQueue<ReviewJobObj> toSendBuffer;
    private boolean ocrJobAlive = false;
    private ReviewAssistant.OcrJob ocrJob;

    public ReviewAssistant(BlockingQueue<ReviewJobObj> jobBuffer, BlockingQueue<ReviewJobObj> toSendBuffer) {
        this.jobBuffer = jobBuffer;
        this.toSendBuffer = toSendBuffer;
        ocr = new OCR();
        tts = new TTS();
        ocrJob = new ReviewAssistant.OcrJob();

        neededKeyWords = new ArrayList<String>();
        neededKeyWords.add("免评");
        neededKeyWords.add("兔评");
        neededKeyWords.add("no review");
        neededKeyWords.add("noreview");
        neededKeyWords.add("no eview");
        neededKeyWords.add("no rview");
        neededKeyWords.add("no revie");
        neededKeyWords.add("NOrview");
        neededKeyWords.add("no need review");
    }

    @Override
    public void run() {
        int always_empty = 0;
        System.out.println("--> AddJob started");
        while (!exit){
            //System.out.println("--> job buffer now is " + jobBuffer );
            if (!jobBuffer.isEmpty()) {
                if (!ocrJobAlive){
                    Thread ocrJobThread = new Thread(ocrJob);
                    System.out.println("--> jobBuffer is not empty, start up OcrJob");
                    ocrJobThread.start();
                    ocrJobAlive = true;
                }
            }

            if(jobBuffer.isEmpty() && ocrJobAlive){
                always_empty ++;
                if (always_empty == 100){
                    System.out.println("--> jobBuffer is empty, shut down");
                    always_empty = 0;
                    ocrJobAlive = false;
                }
            }

            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class OcrJob implements Runnable {
        private volatile boolean exit = false;
        private List<String> ocrResult;
        ReviewJobObj reviewJobObj;
        int appInfochanged = 0;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        @Override
        public void run() {
            System.out.println("--> OcrJob started");
            while (ocrJobAlive) {
                if (!jobBuffer.isEmpty()) {
                    try {
                        reviewJobObj = jobBuffer.take();
                        if (checkFileExist(reviewJobObj.getFilePath())) {
                            ocrResult = ocr.getResult(reviewJobObj.getFilePath());
                            if (!ocrResult.contains("error_code")){
                                System.out.println("<----- Got result ----->");
                                System.out.println("--> at time "+ sdf.format(cal.getTime()) );
                                System.out.println(ocrResult);
                                if (checkNeeded(ocrResult)) {
                                    System.out.println("--> We need this picture, the ID of which is " + reviewJobObj.getMsgID());
                                    speakItOut(reviewJobObj);
                                    try {
                                        toSendBuffer.put(reviewJobObj);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    removeUseless(reviewJobObj.getFilePath());
                                }
                            } else {
                                if (ocrResult.get(1).equals("SDK108")){
                                    boolean succeed = ChangeIP.changeIP();
                                    if (!succeed){
                                        System.out.println("!!! IP change failed, stop the program");
                                    }
                                } else {
                                    System.out.println("!!! unhandled error code: "+ ocrResult.get(1) + " !!!");
                                }
                            } if (ocrResult.contains("unknown error")){
                                System.out.println("!!! unknown error !!!");
                            }

                        } else System.out.println("--> File doesn't exist");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void stop(){
            exit = true;
        }
    }

    private void removeUseless(String filePath) {
        File toRemoveFile = new File(filePath);
        if (toRemoveFile.exists()){
            if (toRemoveFile.delete()){
                System.out.println("--> successfully removed the useless picture ");
            } else {
                System.out.println("!!! failed to removed the useless picture ");
            }

        } else {
            System.out.println("!!! didn't find the picture during trying to remove the useless picture");
        }

    }

    private void speakItOut(ReviewJobObj reviewJobObj) {
        String s = "爸爸，我们收到一条有免评信息的图片";
        if (!checkVoiceExist(s)){
            tts.generateVoice(s);
        }
//        try {
//            FileInputStream fis = new FileInputStream(("./voices/" + s + ".mp3"));
//            Player player = new Player(fis);
//            player.play();
//        } catch (FileNotFoundException | JavaLayerException e) {
//            e.printStackTrace();
//        }
    }

    private boolean checkVoiceExist(String s) {
        File tmpFile = new File(("./voices/" + s + ".mp3") );
        return tmpFile.exists();
    }

    private List<String> neededKeyWords;
    private boolean checkNeeded(List<String> ocrResult) {
        boolean need = false;
        for (String element : ocrResult){
            for (String neededElement : neededKeyWords){
                if (element.toLowerCase().contains(neededElement.toLowerCase())){
                    need = true;
                    break;
                }
            }
        }
        return need;
    }

    private boolean checkFileExist(String currentFilePath) {
        File tmpFile = new File(currentFilePath);
        return tmpFile.exists();
    }


    private volatile boolean exit = false;
    public void stop(){
        exit = true;
    }
}
