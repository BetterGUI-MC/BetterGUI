package me.hsgamer.bettergui.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtils {

  public static JSONObject getJSONFromURL(String address) throws IOException, ParseException {
    URL url = new URL(address);
    URLConnection openConnection = url.openConnection();
    openConnection.addRequestProperty("User-Agent",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
    openConnection.connect();
    BufferedReader rd = new BufferedReader(
        new InputStreamReader(openConnection.getInputStream()));
    return (JSONObject) new JSONParser().parse(rd);
  }
}
