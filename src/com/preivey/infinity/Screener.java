package com.preivey.infinity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * All API calls and screening tools happen here.
 */
public class Screener {
    private final String INTRINIOAPI = "https://api.intrinio.com/securities/search?conditions=";
    final static String ALPHA_KEY = "&apikey=Q8PRIY8M7DJWGNLT";
    private String[] tickers;
    private String[] companyNames;
    private String[] industries;
    private double[] prices;

    public String[] getCompanyNames () {
        return companyNames;
    }

    public String[] getIndustries () {
        return industries;
    }

    public String[] getTickers () {
        return tickers;
    }

    public double[] getPrices () {
        return prices;
    }

    private static String readAll (Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder ();
        int cp;
        while ((cp = rd.read ()) != -1) {
            sb.append ((char) cp);
        }
        return sb.toString ();
    }

    /**
     * Calls the IEX Quote API to get updated information.
     */
    private void updateDetails () {
        companyNames = new String[tickers.length];
        industries = new String[tickers.length];
        prices = new double[tickers.length];
        try {
            for (int i = 0; i < tickers.length; i++) {
                JSONObject stock = readJsonFromUrl ("https://api.iextrading.com/1.0/stock/" + tickers[i] + "/quote");
                companyNames[i] = stock.get ("companyName").toString ();
                industries[i] = stock.get ("sector").toString ();
                prices[i] = Double.parseDouble (stock.get ("open").toString ());
            }
        }
        catch (IOException e) {
            System.out.println (e.getMessage ());
        }
    }

    /**
     * JSON reader with authentication.
     *
     * @param url
     *
     * @return
     *
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject readJsonFromUrl (String url) throws IOException, JSONException {
        try (InputStream is = new URL (url).openStream ()) {
            BufferedReader rd = new BufferedReader (new InputStreamReader (is, Charset.forName ("UTF-8")));
            String jsonText = Screener.readAll (rd);
            return new JSONObject (jsonText);
        }
    }

    public Screener (String[] tickers) {
        this.tickers = tickers;
        for (String s : tickers) {
            s.replace ("$", "\\x24");
        }
        updateDetails ();
    }

    /**
     * Screens for stocks according to the criteria.
     *
     * @param conditions
     */
    public Screener (String conditions) {
        // CALLS THE INTRINIO API FOR SCREENING
        String apiCall = INTRINIOAPI + conditions + "&order_column=marketcap&order_direction=desc&primary_only=true";

        // AUTHENTICATOR WITH USER AND PASS FOR API CALL
        Authenticator.setDefault (new Authenticator () {
            @Override
            protected PasswordAuthentication getPasswordAuthentication () {
                return new PasswordAuthentication (
                        "a469fa8b1d04958baa20826a55643489", "533c068c08a1bb198cd6940f1b6aaec1".toCharArray ());
            }
        });
        JSONObject securities = new JSONObject ();
        try {
            securities = readJsonFromUrl (apiCall);
//            System.out.println (securities);
            if (Integer.parseInt (securities.get ("result_count").toString ()) == 0) {
                return;
            }
        }
        catch (IOException e) {
            System.out.println (e.getMessage ());
        }

        JSONArray list = securities.getJSONArray ("data");
        String[] tickers = new String[list.length ()];

        for (int i = 0; i < list.length (); i++) {
            tickers[i] = list.getJSONObject (i).getString ("ticker");
        }

        this.tickers = tickers;

        // FOR DEVELOPMENT THIS IS THE STOCK SELECTION
//        tickers = new String[4];
//        tickers[0] = "MSFT";
//        tickers[1] = "MU";
//        tickers[2] = "AMZN";
//        tickers[3] = "GOOGL";

        updateDetails ();
    }

    /**
     * Returns a map of dates and corresponding closing prices. In the format K = Date, V = Double.
     *
     * @param ticker
     *
     * @return
     */
    public static Map<Date, Double> getHistory (String ticker) {
        String apiCall = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + ticker + "&output_size=full" +
                                 ALPHA_KEY;
        JSONObject history = new JSONObject ();
        try {
            history = readJsonFromUrl (apiCall).getJSONObject ("Time Series (Daily)");
        }
        catch (IOException e) {
            System.out.println (e.getMessage ());
        }
        Map<Date, Double> data = new HashMap<> ();
        Iterator<String> dates = history.keys ();
        try {
            while (dates.hasNext ()) {
                String date = dates.next ();
                data.put (new SimpleDateFormat ("yyyy-MM-dd").parse (date), history.getJSONObject (date).getDouble ("5. adjusted close"));
            }
        }
        catch (ParseException e) {
            System.out.println (e.getMessage ());
        }
        return data;
    }

    /**
     * Returns array of current prices indexed according to parameter array of tickers.
     *
     * @param tickers
     *
     * @return
     */
    public static Double[] getCurrentPrices (String[] tickers) {
        Double[] prices = new Double[tickers.length];
        Calendar c = Calendar.getInstance ();
        c.add (Calendar.YEAR, -1);

        String s = new SimpleDateFormat ("yyyy-MM-dd").format (c.getTime ());
        try {
            Date d = new SimpleDateFormat ("yyyy-MM-dd").parse ("2017-10-30");
            for (int i = 0; i < tickers.length; i++) {
                prices[i] = Double.parseDouble (getHistory (tickers[i]).get (d).toString ());
            }
        }
        catch (ParseException e) {

        }
        return prices;
    }

    /**
     * Actual current price returner
     *
     * @param tickers
     *
     * @return
     */
    public static Double[] getCurrentPrices1 (String[] tickers) {
        Double[] prices = new Double[tickers.length];
        try {
            for (int i = 0; i < tickers.length; i++) {
                prices[i] = Double.parseDouble (readJsonFromUrl ("https://api.iextrading.com/1.0/stock/" + tickers[i] + "/quote").get ("close").toString ());
            }
        }
        catch (IOException e) {
            System.out.println (e.getMessage ());
        }
        return prices;
    }

    /**
     * Test harness.
     *
     * @param args
     *         unused
     */
    public static void main (String[] args) {
        String[] test = {"JPM$A"};
        Screener testScreen = new Screener ("epsgrowth~gt~0.10");
    }
}