package com.reeuse.location.utils;

import android.text.TextUtils;
import java.util.regex.Pattern;

/**
 * GeoCoordinatesValidatorUtils.java
 * Utils function to validate the latitude,longitude values.
 * Created by Rajiv M on 30/07/15.
 */
public class GeoCoordinatesValidatorUtils {

  /**
   * To check the given value is in Latitude range or not
   * @param value latitude value
   * @return true if matched the range else false.
   */
  public static boolean isValidLatitude(String value){
    // Latitude range is from -90 to +90
    Pattern mPattern = Pattern.compile("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$");
    if(TextUtils.isEmpty(value))
      return false;
    if(mPattern.matcher(value).matches())
      return  true;
    else
      return false;
  }

  /**
   * To check the given value is in Longitude range or not
   * @param value longitude value
   * @return true if matched the range else false.
   */
  public static boolean isValidLongitude(String value){
    //Longitude from -180 to +180
    Pattern mPattern = Pattern.compile("^[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$");
    if(TextUtils.isEmpty(value))
      return false;
    if(mPattern.matcher(value).matches())
      return  true;
    else
      return false;
  }


}
