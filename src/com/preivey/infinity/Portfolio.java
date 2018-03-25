package com.preivey.infinity;

import java.util.Date;

/**
 * Class does the math and calulates portfolio return assuming equal weighting of all stocks in portfolio.
 */
public class Portfolio {
    private String strategyName;
    private String[] tickers;
    private String[] names;
    private String[] sectors;
    private Double[] costBasis;
    private Double[] currentPrices;
    private double[] percentChanges;
    private double portfolioReturn;
    private Date creationDate;

    public String getStrategyName () {
        return strategyName;
    }

    public String[] getTickers () {
        return tickers;
    }

    public String[] getNames () {
        return names;
    }

    public String[] getSectors () {
        return sectors;
    }

    public Double[] getCostBasis () {
        return costBasis;
    }

    /**
     * Get current prices for each stock.
     *
     * @return
     */
    public Double[] getCurrentPrices () {
        return currentPrices;
    }

    /**
     * Get percentage changes for each stock.
     *
     * @return
     */
    public double[] getPercentChanges () {
        return percentChanges;
    }

    /**
     * Get total portfolio return.
     *
     * @return
     */
    public double getPortfolioReturn () {
        return portfolioReturn;
    }

    /**
     * Portfolio is created.
     *
     * @param strategyName
     * @param tickers
     * @param costBasis
     */
    Portfolio (String strategyName, String[] tickers, String[] names, String[] sectors, Double[] costBasis, Date creationDate) {
        this.strategyName = strategyName;
        this.tickers = tickers;
        this.names = names;
        this.sectors = sectors;
        this.costBasis = Screener.getCurrentPrices (tickers);
        this.currentPrices = Screener.getCurrentPrices1 (tickers);
        this.percentChanges = calculatePercents (this.costBasis, currentPrices);
        this.portfolioReturn = percentChanges[percentChanges.length - 1];
        this.creationDate = creationDate;
    }

    /**
     * Calculates individual stock percentage changes and total portfolio change.
     *
     * @param costBasis
     *         array of stock prices at time of purchase
     * @param currentPrices
     *         array of stock prices
     *
     * @return array where the last element is overall return, and all other elements are individual stock returns
     */
    private double[] calculatePercents (Double costBasis[], Double currentPrices[]) {
        double[] percentChanges = new double[costBasis.length + 1];
        double overallPercentChange = 0;
        for (int i = 0; i < costBasis.length; i++) {
            double currPercentChange = (currentPrices[i] - costBasis[i]) / costBasis[i];
            percentChanges[i] = currPercentChange;
            overallPercentChange += currPercentChange;
        }
        percentChanges[costBasis.length] = overallPercentChange / costBasis.length;
        return percentChanges;
    }
}