package com.nove1120.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaUtil {

  public static String getSha256(String inputString){
      StringBuffer sb = new StringBuffer();
      try {
          byte[] input = inputString.getBytes();
          MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
          byte[] result = mDigest.digest(input);
          for (int i = 0; i < result.length; i++) {
              sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
          }

      }catch (NoSuchAlgorithmException nsae){
          nsae.printStackTrace();
      }
      return sb.toString();
  }
}