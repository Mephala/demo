package com.example.demo;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ParseMeb {

  enum Col {
    OKUL("OKUL/KURUM"), DERSLIK("DERSLİK"), OGRETMEN("ÖĞRETMEN"),
    OGRENCI("ÖĞRENCİ"), ILK_ORTA("İLKOKUL + ORTAOKUL"),
    MIDDLE("GENEL ORTAÖĞRETİM"), MESLEK_TEKNIK("MESLEKİ ve TEKNİK");

    private String colValue;

    Col(String colValue) {
      this.colValue = colValue;
    }

    public String getColValue() {
      return colValue;
    }

    public static Col getFromValue(String val) {
      Col[] cols = Col.values();
      for (Col col : cols) {
        if (col.colValue.equals(val)) {
          return col;
        }
      }
      return null;
    }
  }

  static class Location {

    String il;
    String ilce;
    String belde;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Location location = (Location) o;
      return il.equals(location.il) &&
          ilce.equals(location.ilce) &&
          belde.equals(location.belde);
    }

    @Override
    public int hashCode() {
      return Objects.hash(il, ilce, belde);
    }
  }

  public static void main(String[] args) {
    try {
      java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
      java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);
      System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
      System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
      System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
      System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
      System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");

      Map<Location, Map<Col, BigDecimal>> locationColValues = new HashMap<>();

      List<String> lines = FileUtils
          .readLines(new File("/home/gokhanozg/Desktop/anvil/mebs2.txt"), "utf-8");
      List<String> normalizedLines = new ArrayList<>();
      for (String line : lines) {
        for (int i = 0; i < 10; i++) {
          line = line.replaceAll("  ", " ");
          line = line.replaceAll("\t", " ");
        }
        normalizedLines.add(line);
      }

      Map<Location, String> locationMebAddress = new HashMap<>();

      for (String normalizedLine : normalizedLines) {
        String[] vals = normalizedLine.split(" ");
        Location location = new Location();
        location.il = vals[0];
        location.ilce = vals[1];
        location.belde = vals[2];
        String url = vals[3];

        locationMebAddress.put(location, url);
      }


      for (Entry<Location, String> locationStringEntry : locationMebAddress.entrySet()) {
        String url = locationStringEntry.getValue();
        Location location = locationStringEntry.getKey();
        System.out.println(String.format("Getting %s-%s-%s from url:%s",location.il,location.ilce,location.belde,url));

        Map<Col, BigDecimal> colValues = new HashMap<>();

        parseForUrl(url,colValues);
        if(colValues.size() > 0){
          System.out.println("Done.");
          locationColValues.put(location,colValues);
        }else{
          System.out.println("Error occured, skipping this location.");
        }
      }

      StringBuilder sb = new StringBuilder();
      sb.append("il,ilce,belde,");
      Col[] cols = Col.values();
      for (int i = 0; i < cols.length ; i++) {
        sb.append(cols[i].colValue);
        if(i!= cols.length - 1){
          sb.append(",");
        }
      }
      sb.append(System.lineSeparator());

      for (Entry<Location, Map<Col, BigDecimal>> locationMapEntry : locationColValues.entrySet()) {
        Location loc = locationMapEntry.getKey();
        Map<Col,BigDecimal> values = locationMapEntry.getValue();

        sb.append(loc.il).append(",").append(loc.ilce).append(",").append(loc.belde).append(",");

        for (int i = 0; i < cols.length ; i++) {
          sb.append(values.get(cols[i]));
          if(i!= cols.length - 1){
            sb.append(",");
          }
        }
        sb.append(System.lineSeparator());
      }

      String result = sb.toString();
      File output = new File("home/gokhanozg/Desktop/anvil/mebsOut.csv");
      FileUtils.writeStringToFile(output,result,"utf-8");

    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static void parseForUrl(String url,
      Map<Col, BigDecimal> colValues) {


    try{

      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(url);
      HttpResponse response =httpClient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      String result = EntityUtils.toString(entity);
      result = new String(result.getBytes("ISO-8859-9"),Charset.forName("utf-8"));


      final String nameStart = "<span class=\"statistic-name\">";
      final String spanEnd = "</span>";
      final String numberStart = "<span class=\"statistic-number\">";
      int i = result.indexOf(nameStart);
      while (i != -1) {

        result = result.substring(i + nameStart.length());
        i = result.indexOf(spanEnd);
        String statisticName = result.substring(0, i);
        statisticName = normalizeStatisticName(statisticName);

        if (!"x".equals(statisticName)) {
          result = result.substring(i);

          i = result.indexOf(numberStart);
          result = result.substring(i + numberStart.length());
          i = result.indexOf(spanEnd);

          BigDecimal number = new BigDecimal(result.substring(0, i).replaceAll("\\.",""));
          result = result.substring(i);

          colValues.put(Col.getFromValue(statisticName),number);
        }

        i = result.indexOf(nameStart);
      }
    }catch(Throwable t){
      System.err.println("Failed to parse url:"+ url);
      t.printStackTrace();
    }
  }

  private static String normalizeStatisticName(String statisticName) {
    if (statisticName.contains("<br>")) {
      return "x";
    } else {
      return statisticName;
    }
  }
}
