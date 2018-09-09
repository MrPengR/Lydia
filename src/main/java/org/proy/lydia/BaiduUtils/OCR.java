package org.proy.lydia.BaiduUtils;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import org.proy.lydia.review_assist.ReviewAssistException;

public class OCR {
    private AipOcr ocr;
    private HashMap<String, String> options;
    private BaiduAppInfo appInfo = new BaiduAppInfo();
    public OCR() {
        ocr = new AipOcr(appInfo.APP_ID, appInfo.API_KEY, appInfo.SECRET_KEY);
        options = new HashMap<String, String>();
        ocr.setConnectionTimeoutInMillis(10000);
        ocr.setSocketTimeoutInMillis(60000);
    }

//    public OCR(BaiduAppInfo appInfo){
//        ocr = new AipOcr(appInfo.APP_ID, appInfo.API_KEY, appInfo.SECRET_KEY);
//        options = new HashMap<String, String>();
//        ocr.setConnectionTimeoutInMillis(5000);
//        ocr.setSocketTimeoutInMillis(60000);
//    }

    public ArrayList<String> getResult(String path) {
        JSONObject res = ocr.basicGeneral(path, options);
//        System.out.println(res.toString());
        return parseJSON(res);
    }

    private ArrayList<String> parseJSON(JSONObject json) {
        ArrayList<String> wordsResult = new ArrayList<String>();
        try {
            JSONArray words_result = json.getJSONArray("words_result");
            for (int i = 0; i<words_result.length(); i++){
                JSONObject word = words_result.getJSONObject(i);
                wordsResult.add(word.getString("words"));
            }
        } catch (org.json.JSONException e){
            System.out.println("!!! didn't get correct response!!!");
            if (json.has("error_code")){
                try{
                    String error_code = json.getString("error_code");
                    wordsResult.add("error_code");
                    wordsResult.add(error_code);
                } catch (org.json.JSONException e1){
                    int error_code = json.getInt("error_code");
                    wordsResult.add("error_code");
                    wordsResult.add(String.valueOf(error_code));
                }
            } else {
                wordsResult.add("unknown error");
            }
            //throw new ReviewAssistException("!!! didn't get correct response!!!");
        }
        return wordsResult;
    }

    public void changeAppInfo(){
        System.out.println("--> changing app infos");
        appInfo.useLydia1();
        ocr = new AipOcr(appInfo.APP_ID, appInfo.API_KEY, appInfo.SECRET_KEY);
        options = new HashMap<String, String>();
        ocr.setConnectionTimeoutInMillis(5000);
        ocr.setSocketTimeoutInMillis(60000);
    }
}
