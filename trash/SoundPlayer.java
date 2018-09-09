import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {
    private AudioFormat getFormat(){
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void playAudio(String path){
        File audioFile = new File(path);
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat outFormat = getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
            SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(outFormat);
            line.start();

            Runnable runner = new Runnable() {
                int bufferSize = (int) outFormat.getSampleRate() * outFormat.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                @Override
                public void run() {
                    try {
                        int count;
                        while ((count = in.read(
                                buffer, 0, buffer.length)) != -1) {
                            if (count > 0) {
                                line.write(buffer, 0, count);
                            }
                        }
//                        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)){
//                            line.write(buffer, 0, n);
//                        }
                        line.drain();
                        line.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread playThread = new Thread(runner);
            playThread.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

    }
}
