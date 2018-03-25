package com.preivey.infinity;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Portfolios must be built using this tool.
 */
public class FileTools {

    /**
     * Reads and creates Portfolio object from a specified file name.
     *
     * @param name
     *         name of the file
     *
     * @return Portfolio
     */
    public static Portfolio read (String name) {
        ArrayList<String> tickers = new ArrayList<> ();
        ArrayList<String> names = new ArrayList<> ();
        ArrayList<String> sectors = new ArrayList<> ();
        ArrayList<Double> prices = new ArrayList<> ();
        Date date = new Date ();
        try (BufferedReader in = new BufferedReader (new FileReader (name))) {
            date = new SimpleDateFormat ("yyyy-MM-dd").parse (in.readLine ());
            String temp = in.readLine ();
            String[] tempArr;
            while (temp != null) {
                tempArr = temp.split (" \\| ");
                tickers.add (tempArr[0]);
                names.add (tempArr[1]);
                sectors.add (tempArr[2]);
                prices.add (Double.parseDouble (tempArr[3].substring (1)));
                temp = in.readLine ();
            }
        }
        catch (IOException e) {
            System.out.println (e.getMessage ());
        }
        catch (ParseException e) {
            System.out.println (e.getMessage ());
        }
        return new Portfolio (name.substring(0, name.length()-4), tickers.toArray (new String[0]), names.toArray (new String[0]), sectors.toArray (new String[0]),
                prices.toArray (new Double[0]), date);
    }

    public static void main (String[] args) {
        FileTools.read ("test");
    }
}
