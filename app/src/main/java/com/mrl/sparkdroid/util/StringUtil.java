package com.mrl.sparkdroid.util;

import android.widget.Toast;

import com.mrl.sparkdroid.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static boolean regxPort(String port){
        Pattern pattern = Pattern.compile("([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])");
        Matcher matcher = pattern.matcher(port);
        if(matcher.matches()){
            return true;
        }else {

            return false;
        }
    }
    public static boolean regxIp(String ip){
        Pattern pattern = Pattern.compile("(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])");
        Matcher matcher = pattern.matcher(ip);
        if(matcher.matches()){
            return true;
        }else {
            return false;
        }
    }
}
