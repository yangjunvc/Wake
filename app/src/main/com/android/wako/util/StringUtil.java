package com.android.wako.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static String Emoji_Pattern = "^[0-9a-zA-Z\\u0024-\\uFFFF #!]+$";
    private static String Phone_Pattern ="0?(13|14|15|18|17)[0-9]{9}$";
    private static String Password_Pattern ="^[0-9a-zA-Z@#%*&$';<>+-.,_]{6,24}$";
    private static String NickName_Pattern = "^[0-9a-zA-Z\\u4E00-\\u9FA5.]{2,12}$";

    public static boolean isEmpty(String str){
        if(str == null || str.length() <= 0)return true;
        return false;
    }

    /**
     * 判断是否phone
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone){
        if(!StringUtil.isEmpty(phone)){
            Pattern pattern = Pattern.compile(Phone_Pattern);
            Matcher matcher = pattern.matcher(phone);
            if (matcher.matches()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断是否password
     * @param password
     * @return
     */
    public static boolean isPassword(String password){
        if(!StringUtil.isEmpty(password)){
            Pattern pattern = Pattern.compile(Password_Pattern);
            Matcher matcher = pattern.matcher(password);
            if (matcher.matches()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断是否nickname
     * @param name
     * @return
     */
    public static boolean isNickName(String name){
        if(!StringUtil.isEmpty(name)){
            Pattern pattern = Pattern.compile(NickName_Pattern);
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 验证是否是emoji表情 true是不包含，false是包含
     *
     * @param content
     * @return
     */
    public static boolean containEmoji(String content) {
        if (isEmpty(content)) {
            return true;
        }
        content = content.replace("\n", "").replace("\r", "");
        Pattern pattern = Pattern.compile(Emoji_Pattern);
        boolean flag = false;
        if (!StringUtil.isEmpty(content)) {
            Matcher matcher = pattern.matcher(content.trim());
            flag = matcher.matches();
        }
        return flag;
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    static int[] codeArray = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
    static Map<Integer,String> checkCodeDic = new HashMap<Integer,String>();
    static{
        checkCodeDic.put(0, "1");
        checkCodeDic.put(1, "0");
        checkCodeDic.put(2, "X");
        checkCodeDic.put(3, "9");
        checkCodeDic.put(4, "8");
        checkCodeDic.put(5, "7");
        checkCodeDic.put(6, "6");
        checkCodeDic.put(7, "5");
        checkCodeDic.put(8, "4");
        checkCodeDic.put(9, "3");
        checkCodeDic.put(10, "2");
    }

    /**
     *
     * @param inStr
     * @return
     */
    public static String getMD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

}
