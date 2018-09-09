package org.proy.lydia.BaiduUtils;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class TTS {

    private AipSpeech tts;
    private HashMap<String, Object> options = new HashMap<String, Object>();
    BaiduAppInfo appInfo = new BaiduAppInfo();

    public TTS() {
        tts = new AipSpeech(appInfo.APP_ID, appInfo.API_KEY, appInfo.SECRET_KEY);
        options.put("per", "4");
        tts.setConnectionTimeoutInMillis(5000);
        tts.setSocketTimeoutInMillis(60000);
        new File("./voices").mkdirs();
    }

//    public TTS(BaiduAppInfo appInfo) {
//        tts = new AipSpeech(appInfo.APP_ID, appInfo.API_KEY, appInfo.SECRET_KEY);
//        options.put("per", "4");
//        tts.setConnectionTimeoutInMillis(5000);
//        tts.setSocketTimeoutInMillis(60000);
//        new File("./voices").mkdirs();
//    }

    public void generateVoice(String s){
        TtsResponse res = tts.synthesis(s, "zh", 1, options);
        byte[] data = res.getData();
        if (data != null){
            try {
                Util.writeBytesToFileSystem(data, ("./voices/" + s + ".mp3"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void changeAppInfo(){
        tts = new AipSpeech(appInfo.APP_ID, appInfo.API_KEY, appInfo.SECRET_KEY);
        options.put("per", "4");
        tts.setConnectionTimeoutInMillis(5000);
        tts.setSocketTimeoutInMillis(60000);
    }
}
