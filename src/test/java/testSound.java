import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class testSound {
    public static void main(String[] args) throws FileNotFoundException, JavaLayerException {
//        SoundPlayer soundPlayer = new SoundPlayer();
//        soundPlayer.playAudio("./voices/爸爸，我们收到一条有免评信息的图片.mp3");

        FileInputStream fis = new FileInputStream("./voices/爸爸，我们收到一条有免评信息的图片.mp3");
        Player player = new Player(fis);

        player.play();
    }

}
