package org.proy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChangeIP {
    // TODO: 03.09.18 curl ipinfo to check if succed
//    static final String[] HK = new String[]{"HK", "HK"};
//    static final String[] US_M = new String[]{"US-M", "Miami"};
//    static final String[] US_SA = new String[]{"US-SA", "Seattle"};
//    static final String[] US_H = new String[]{"US-H", "Houston"};

    private List<String> VPNList = new ArrayList<String>(Arrays.asList(
            "HK", "US-M", "TW-T", "US-H", "TW-TY", "AS-SN", "US-SA", "SG"
    ));

    public ChangeIP() {
       // VPNList.add(HK)
    }

    public static boolean changeIP(){
        String s = null;
        Process p;
        BufferedReader br;
        String command;
        ChangeIP changeIP = new ChangeIP();
        boolean res = false;

        System.out.println("--> changing IP");
        for (String ele : changeIP.VPNList){
            try {
                command = "sudo pon "+ ele;
                //command = "purevpn -c " + ele;
                System.out.println(command);
                p = Runtime.getRuntime().exec(command);
                p.waitFor();
                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((s = br.readLine()) != null)
                    System.out.println(s);
                br.close();
                p.destroy();

                TimeUnit.SECONDS.sleep(3);
                p = Runtime.getRuntime().exec("curl ipinfo.io");
                p.waitFor();
                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((s = br.readLine()) != null){
                    System.out.println(s);
                    if (s.contains("country")){
                        if (!s.contains("DE")){
                            res = true;
                            break;
                        }
                    }
                }
                br.close();
                p.destroy();
                if (res){
                    break;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (res){
            assert s != null;
            System.out.println("--> Successfully change IP, location now is " + s.charAt(14) + s.charAt(15));
        } else {
            System.out.println("--> Failed to change IP, all the hosts are not available");
        }
        return res;
    }
}
