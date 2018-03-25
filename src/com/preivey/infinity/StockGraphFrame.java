package com.preivey.infinity;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StockGraphFrame extends ApplicationFrame {
    private final ChartPanel panel;

    /**
     * To implement this into a JFrame.
     *
     * @return the ChartPanel, to be used in add () methods
     */
    public ChartPanel getPanel () {
        return panel;
    }

    public StockGraphFrame (Portfolio port) {
        super (port.getStrategyName ());
        XYDataset data = populateData (port);
        JFreeChart chart = ChartFactory.createTimeSeriesChart (port.getStrategyName (), "Date", "Return to Date %", data,false,false,false);
        ChartPanel panel = new ChartPanel (chart);
        panel.setPreferredSize (new Dimension (480, 240));
        panel.setMouseZoomable (true, false);
        this.panel = panel;
        setContentPane (panel);
    }

    private XYDataset populateData (Portfolio port) {
        TimeSeries series = new TimeSeries (getName ());
        Map<Date, Double> tempMap; // DATE / PRICE OF A STOCK
        Map<Date, Double> percents = new HashMap<> (); // DATE / OVERALL RETURN
        // FOR EVERY STOCK
        for (int i = 0; i < port.getTickers ().length; i++) {
            tempMap = Screener.getHistory (port.getTickers ()[i]); // GET HISTORICAL PRICES
            double buyPrice = port.getCostBasis ()[i]; // GET BUY PRICE

            // FOR EVERY DAY FOR THIS STOCK
            for (Map.Entry<Date, Double> entry : tempMap.entrySet ()) {
                double percentChange = (entry.getValue () - buyPrice) / buyPrice; // CALCULATE PERCENTAGE CHANGE
                percents.merge (entry.getKey (), percentChange, Double:: sum); // ADD PERCENTAGE CHANGE OF THE DAY TO THE MAP
            }
        }
        for (Map.Entry<Date, Double> entry : percents.entrySet ()) {
            series.add (new Day (entry.getKey ()), 100.00 * (entry.getValue () / port.getTickers ().length));
        }
        return new TimeSeriesCollection (series);
    }

    public static void main (String[] args) {
        StockGraphFrame test = new StockGraphFrame (FileTools.read ("test"));
        test.pack ();
        test.setVisible (true);
//        new StockGraphFrame (FileTools.read ("test"));
//        Screener.getHistory ("MSFT");
    }
}
