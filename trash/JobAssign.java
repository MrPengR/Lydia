import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class JobAssign implements Runnable {
    private BlockingQueue jobBuffer = new LinkedBlockingQueue();
    String result = null;
    private boolean ocrJobAlive = false;
    private OcrJob ocrJob = new OcrJob();


    public JobAssign(BlockingQueue jobBuffer) {
        this.jobBuffer = jobBuffer;
    }

    @Override
    public void run() {
        int two_times_empty = 0;
        System.out.println("AddJob started");
        while (true){
            if (!jobBuffer.isEmpty()) {
                if (ocrJobAlive){
                    Thread ocrJobThread = new Thread(ocrJob);
                    System.out.println("jobBuffer is not empty, start up OcrJob");
                    ocrJobThread.start();
                    ocrJobAlive = true;
                }
            }

            if(jobBuffer.isEmpty() && ocrJobAlive){
                two_times_empty ++;
                if (two_times_empty == 2){
                    System.out.println("jobBuffer is empty, shut down");
                    two_times_empty = 0;
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

//    public class AddJob implements Runnable {
//        private OcrJob ocrJob = new OcrJob();
////        private Thread ocrJobThread = new Thread(ocrJob);
//
//        public AddJob() throws InterruptedException {
//            Thread addJobThread = new Thread(this);
//            addJobThread.start();
//        }
//
//
//    }


    public class OcrJob implements Runnable {
        @Override
        public void run() {
            System.out.println("OcrJob started");
            while (ocrJobAlive) {
                if (!jobBuffer.isEmpty()) {
                    System.out.println(jobBuffer);
                    jobBuffer.remove(0);
                }
            }
        }
    }
//
//    private SocketServer socketServer = new SocketServer();
//    private String checkJob() {
//
//        return socketServer.getResult();
//    }
//
//    private String job;
//    public void setJobs(String job) {
//        this.job = job;
//    }


}
