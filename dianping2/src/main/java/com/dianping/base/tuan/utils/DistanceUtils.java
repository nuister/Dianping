package com.dianping.base.tuan.utils;

import com.dianping.configservice.impl.ConfigHelper;
import com.dianping.model.GPSCoordinate;

public class DistanceUtils
{
  protected static double calculateDistance(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    double d = ConfigHelper.configDistanceFactor;
    if (d <= 0.0D)
      return 0.0D;
    if ((paramDouble3 == 0.0D) || (paramDouble4 == 0.0D))
      return 0.0D;
    if ((paramDouble1 == 0.0D) || (paramDouble2 == 0.0D))
      return 0.0D;
    paramDouble1 = new GPSCoordinate(paramDouble3, paramDouble4).distanceTo(new GPSCoordinate(paramDouble1, paramDouble2)) * d;
    if (Double.isNaN(paramDouble1))
      return 0.0D;
    return paramDouble1;
  }

  public static String getDistance(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    paramDouble3 = calculateDistance(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    if (paramDouble3 <= 0.0D)
      return null;
    return getDistanceText(paramDouble1, paramDouble2, (int)Math.round(paramDouble3 / 10.0D) * 10);
  }

  protected static String getDistanceText(double paramDouble1, double paramDouble2, long paramLong)
  {
    if ((paramDouble1 == 0.0D) && (paramDouble2 == 0.0D))
      return "";
    String str;
    if (paramLong <= 100L)
      str = "<100m";
    while (true)
    {
      return str;
      if (paramLong < 1000L)
      {
        str = paramLong + "m";
        continue;
      }
      if (paramLong < 10000L)
      {
        paramLong /= 100L;
        str = paramLong / 10L + "." + paramLong % 10L + "km";
        continue;
      }
      if (paramLong < 100000L)
      {
        str = paramLong / 1000L + "km";
        continue;
      }
      str = "";
    }
  }
}

/* Location:           C:\Users\xuetong\Desktop\dazhongdianping7.9.6\ProjectSrc\classes-dex2jar.jar
 * Qualified Name:     com.dianping.base.tuan.utils.DistanceUtils
 * JD-Core Version:    0.6.0
 */