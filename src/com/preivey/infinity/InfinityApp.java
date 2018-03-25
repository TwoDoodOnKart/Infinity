package com.preivey.infinity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class InfinityApp extends JFrame implements ActionListener {

    InfinityProject inf = new InfinityProject();
    JMenuItem helpItem = new JMenuItem("Help"), quitItem = new JMenuItem("Quit"), peItem = new JMenuItem("P/E Ratio"), pbItem = new JMenuItem("P/B Ratio"), pegItem = new JMenuItem("PEG Ratio"), deItem = new JMenuItem("D/E Ratio"), eItem = new JMenuItem("Sales Growth"), feItem = new JMenuItem("Forward P/E Ratio"), pItem = new JMenuItem("EPS Growth"), roeItem = new JMenuItem("Return on Equity (ROE)");

    public InfinityApp() {
        super("Infinity: Improving Financial Literacy");

        JMenu fileMenu = new JMenu ("File");
        JMenu metricMenu = new JMenu("About Screening Metrics");
        JMenuBar myMenus = new JMenuBar();
        fileMenu.add(helpItem);
        fileMenu.add(quitItem);
        metricMenu.add(peItem);
        metricMenu.add(pbItem);
        metricMenu.add(pegItem);
        metricMenu.add(deItem);
        metricMenu.add(eItem);
        metricMenu.add(feItem);
        metricMenu.add(pItem);
        metricMenu.add(roeItem);
        quitItem.addActionListener(this);
        helpItem.addActionListener(this);
        peItem.addActionListener(this);
        pbItem.addActionListener(this);
        pegItem.addActionListener(this);
        deItem.addActionListener(this);
        eItem.addActionListener(this);
        feItem.addActionListener(this);
        pItem.addActionListener(this);
        roeItem.addActionListener(this);
        myMenus.add(fileMenu);
        myMenus.add(metricMenu);
        setJMenuBar(myMenus);
        setVisible(true);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(640,600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        add (inf);
    }

    public void actionPerformed (ActionEvent ae) {
        if (ae.getActionCommand().equals("Quit"))
            System.exit(0);
        if (ae.getActionCommand().equals("Help")){
            JOptionPane.showMessageDialog(null, "Infinity was developed at HackNYU 2018 to help teach budding investors about the various investment techniques they can use to screen for stocks. \nThe project allows prospective investors to learn about the theories behind the work, screen for stocks according to one of the techniques or by \nadjusting and designing their own criteria, and then build a portfolio of stocks from those selected and track its performance over time. \n\n(c) Infinity Team 2018", "About Infinity", JOptionPane.INFORMATION_MESSAGE);
        }
        if (ae.getActionCommand().equals("P/E Ratio")){
            JOptionPane.showMessageDialog(null, "The Price to Earnings ratio, calculated as market price per share divided by earnings per share. Essentially the amount an investor would need to invest in order to receive 1 dollar of a company’s earnings.  \nValue investors look for very low P/E ratios, whereas growth investors look for extremely high P/E ratios.", "About The P/E Ratio",JOptionPane.INFORMATION_MESSAGE);
        }
        if (ae.getActionCommand().equals("P/B Ratio")){
            JOptionPane.showMessageDialog(null, "The Price to Book Value ratio, calculated as market price per share divided by book value per share. A lower P/B ratio can signify that a company is undervalued, but could potentially also mean there is a fundamental problem with the company.", "About The P/B Ratio",JOptionPane.INFORMATION_MESSAGE);
        }
        if (ae.getActionCommand().equals("PEG Ratio")){
            JOptionPane.showMessageDialog(null, "The Price/Earnings to Growth ratio, calculated as the P/E ratio divided by the company earnings growth rate. A low PEG ratio (i.e. below 1) can indicate an undervalued stock. ", "About The PEG Ratio",JOptionPane.INFORMATION_MESSAGE);
        }
        if (ae.getActionCommand().equals("D/E Ratio")){
            JOptionPane.showMessageDialog(null, "The Debt/Equity ratio, calculated as total liabilities divided by shareholders’ equity. This ratio is meant to measure a company’s financial leverage, while indicating how much debt a company is using to finance their assets, \nrelative to the shareholders’ equity. A higher debt/equity ratio indicates higher risk, but could also potentially result in higher earnings.", "About The D/E Ratio",JOptionPane.INFORMATION_MESSAGE);
        }
        if (ae.getActionCommand().equals("Sales Growth")){
            JOptionPane.showMessageDialog(null, "The annual rate of growth of sales.", "About Sales Growth",JOptionPane.INFORMATION_MESSAGE);
        }
        if (ae.getActionCommand().equals("Forward P/E Ratio")){
            JOptionPane.showMessageDialog(null, "An estimated value for the projected future P/E ratio of a company.", "About The Forward P/E Ratio",JOptionPane.INFORMATION_MESSAGE);
        }
        if (ae.getActionCommand().equals("EPS Growth")){
            JOptionPane.showMessageDialog(null, "A company’s earnings divided by shares outstanding. Growth investors pay close attention to this statistic, because higher earnings indicates a more profitable company.", "About EPS Growth",JOptionPane.INFORMATION_MESSAGE);
        }
        if (ae.getActionCommand().equals("Return on Equity (ROE)")){
            JOptionPane.showMessageDialog(null, "Return on Equity; the amount of net income expressed as a percentage of shareholders’ equity, calculated as net income divided by shareholders’ equity. This statistic indicates how much profit a company generates using the money \nshareholders have invested. Growth investors look for this statistic to be very high, in order to find profitable companies.", "About The Return on Equity (ROE)",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args){
        InfinityApp iApp = new InfinityApp();
    }
}

