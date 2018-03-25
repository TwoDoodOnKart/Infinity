package com.preivey.infinity;

import org.jfree.chart.ChartPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;


public class InfinityProject extends JPanel implements ActionListener {

    CardLayout cl;
    JList list = new JList();
    JScrollPane scrollablePane;
    JPanel introPage = new JPanel();
    JPanel strategyPage = new JPanel();
    JPanel stockPage = new JPanel();
    JPanel listPage = new JPanel();
    JLabel peLabel = new JLabel("P/E Ratio < 25");
    JLabel pbLabel = new JLabel("P/B Ratio < 1");
    JLabel pegLabel = new JLabel("PEG Ratio < 1");
    JLabel deLabel = new JLabel("D/E Ratio < 1");
    JLabel eLabel = new JLabel("Sales Growth > 30%");
    JLabel feLabel = new JLabel("Forward P/E Ratio < 25");
    JLabel pLabel = new JLabel("EPS Growth > 10%");
    JLabel roeLabel = new JLabel("ROE > 0%");
    SpringLayout stLayout = new SpringLayout();
    JTextField portfolioName;
    JCheckBox peCheck;
    JCheckBox pbCheck;
    JCheckBox pegCheck;
    JCheckBox deCheck;
    JCheckBox eCheck;
    JCheckBox feCheck;
    JCheckBox pCheck;
    JCheckBox roeCheck;
    JSlider peRatio;
    JSlider pbRatio;
    JSlider pegRatio;
    JSlider deRatio;
    JSlider eGrowth;
    JSlider feGrowth;
    JSlider pMargin;
    JSlider roeRatio;
    String[] stockTickers;
    String[] stockNames;
    String[] industries;
    double[] stockPrices;
    List<File> refinedList = new ArrayList<File>();
    ArrayList<Portfolio> portfolios;
    int currIndex = 0;
    String[] companiesInPortfolio;
    StockGraphFrame graph;
    ChartPanel panel;
    SpringLayout nLayout;
    ArrayList<String> names;
    JComboBox dropDown;

    public void savePortfolio() {
        try {
            PrintWriter writer = new PrintWriter(portfolioName.getText() + ".txt");
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            writer.println(date);
            List lines = list.getSelectedValuesList();
            for (int i = 0; i < lines.size(); i++) {
                writer.println((String) lines.get(i));
            }
            writer.close();
        } catch (IOException e) {
        }
        Portfolio newPortfolio = FileTools.read(portfolioName.getText() + ".txt");
        portfolios.add(newPortfolio);
        names.add(newPortfolio.getStrategyName());
        dropDown.setModel(new DefaultComboBoxModel(names.toArray()));

    }

    public void initializeStocks() {
        scrollablePane = new JScrollPane(list);
        scrollablePane.setPreferredSize(new Dimension(615, 460));
        stockPage.setBorder(BorderFactory.createTitledBorder("Infinity: Choose Your Portfolio"));
        stockPage.setLayout(stLayout);
        JLabel label3 = new JLabel("Select all stocks that you wish to add to this portfolio below! Your stocks will be weighted equally.");
        stLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, label3, 0, SpringLayout.HORIZONTAL_CENTER, stockPage);
        stLayout.putConstraint(SpringLayout.NORTH, scrollablePane, 10, SpringLayout.SOUTH, label3);
        scrollablePane.setBorder(BorderFactory.createEtchedBorder());
        stockPage.add(scrollablePane);
        stockPage.add(label3);
        portfolioName = new JTextField("My Portfolio", 10);
        JLabel label = new JLabel("Name Your Portfolio:");
        JButton butt = new JButton("Save");
        JButton butt2 = new JButton("Go Back");
        stockPage.add(label);
        stockPage.add(portfolioName);
        stockPage.add(butt);
        butt.setPreferredSize(new Dimension(70, 20));
        butt2.setPreferredSize(new Dimension(100, 20));
        butt2.addActionListener(this);
        stockPage.add(butt2);
        butt.addActionListener(this);
        stLayout.putConstraint(SpringLayout.EAST, butt, 0, SpringLayout.EAST, stockPage);
        stLayout.putConstraint(SpringLayout.SOUTH, butt, 0, SpringLayout.SOUTH, stockPage);
        stLayout.putConstraint(SpringLayout.SOUTH, portfolioName, 0, SpringLayout.SOUTH, stockPage);
        stLayout.putConstraint(SpringLayout.EAST, portfolioName, -5, SpringLayout.WEST, butt);
        stLayout.putConstraint(SpringLayout.SOUTH, label, -2, SpringLayout.SOUTH, stockPage);
        stLayout.putConstraint(SpringLayout.EAST, label, -5, SpringLayout.WEST, portfolioName);
        stLayout.putConstraint(SpringLayout.WEST, butt2, 0, SpringLayout.WEST, stockPage);
        stLayout.putConstraint(SpringLayout.SOUTH, butt2, 0, SpringLayout.SOUTH, stockPage);
    }

    public void doScreen() {
        String cond = "";
        if (peCheck.isSelected())
            cond += "pricetoearnings~lte~" + peRatio.getValue() + ",";
        if (pbCheck.isSelected())
            cond += "pricetobook~lte~" + pbRatio.getValue() / 100.0 + ",";
        if (deCheck.isSelected())
            cond += "debttoequity~lte~" + deRatio.getValue() / 100.0 + ",";
        if (eCheck.isSelected())
            cond += "revenuegrowth~gte~" + eGrowth.getValue()/100.0 + ",";
        if (feCheck.isSelected())
            cond += "pricetonextyearearnings~lte~" + feGrowth.getValue() + ",";
        if (pCheck.isSelected())
            cond += "epsgrowth~gte~" + pMargin.getValue() + ",";
        if (roeCheck.isSelected())
            cond += "roe~gte~" + roeRatio.getValue()/100.0 + ",";
        cond = cond.substring(0, cond.length() - 1);
        Screener screen = new Screener(cond);

        stockTickers = screen.getTickers();
        stockNames = screen.getCompanyNames();
        industries = screen.getIndustries();
        stockPrices = screen.getPrices();

        if (stockTickers == null) {
            JOptionPane.showMessageDialog(null, "ERROR: No stocks meet your metric criteria!", "ERROR", JOptionPane.ERROR_MESSAGE);
        } else {
            for (int i = 0; i < stockPrices.length; i++) {
                String temp = " | " + stockNames[i] + " | " + industries[i] + " | $" + stockPrices[i];
                if (temp.endsWith(".0") || temp.endsWith(".1") || temp.endsWith(".2") || temp.endsWith(".3") || temp.endsWith(".4") || temp.endsWith(".5") || temp.endsWith("0.6") || temp.endsWith("0.7") || temp.endsWith(".8") || temp.endsWith(".9")) {
                    temp += "0";
                }
                stockTickers[i] += temp;
            }
            list.setModel(new DefaultListModel());
            list.setListData(stockTickers);
            cl.show(this, "Stocks");
        }
    }

    public void resetSliders() {
        peRatio.setValue(50);
        pbRatio.setValue(500);
        pegRatio.setValue(500);
        deRatio.setValue(400);
        eGrowth.setValue(0);
        feGrowth.setValue(50);
        pMargin.setValue(0);
        roeRatio.setValue(0);
        peCheck.setSelected(true);
        pbCheck.setSelected(true);
        pegCheck.setSelected(true);
        deCheck.setSelected(true);
        eCheck.setSelected(true);
        feCheck.setSelected(true);
        pCheck.setSelected(true);
        roeCheck.setSelected(true);
    }

    public void initializeSliders() {
        peRatio = new JSlider(0, 50, 25);
        peRatio.setMajorTickSpacing(5);
        peRatio.setMinorTickSpacing(1);
        peRatio.setPaintTicks(true);
        peRatio.setPaintLabels(true);
        peRatio.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                peLabel.setText("P/E Ratio < " + source.getValue());
            }
        });
        pbRatio = new JSlider(0, 500, 100);
        pbRatio.setMajorTickSpacing(100);
        pbRatio.setMinorTickSpacing(10);
        Hashtable labelTable = new Hashtable();
        labelTable.put(0, new JLabel("0"));
        labelTable.put(100, new JLabel("1"));
        labelTable.put(200, new JLabel("2"));
        labelTable.put(300, new JLabel("3"));
        labelTable.put(400, new JLabel("4"));
        labelTable.put(500, new JLabel("5"));
        pbRatio.setLabelTable(labelTable);
        pbRatio.setPaintLabels(true);
        pbRatio.setPaintTicks(true);
        pbRatio.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                pbLabel.setText("P/B Ratio < " + source.getValue() / 100.0);
            }
        });
        pegRatio = new JSlider(0, 500, 100); //VERY IMPORTANT!!!!!! THIS IS 100x THE ACTUAL VALUE
        pegRatio.setMajorTickSpacing(100);
        pegRatio.setMinorTickSpacing(10);
        Hashtable labelTable1 = new Hashtable();
        labelTable1.put(0, new JLabel("0"));
        labelTable1.put(100, new JLabel("1"));
        labelTable1.put(200, new JLabel("2"));
        labelTable1.put(300, new JLabel("3"));
        labelTable1.put(400, new JLabel("4"));
        labelTable1.put(500, new JLabel("5"));
        pegRatio.setLabelTable(labelTable1);
        pegRatio.setPaintLabels(true);
        pegRatio.setPaintTicks(true);
        pegRatio.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                pegLabel.setText("PEG Ratio < " + source.getValue() / 100.0);
            }
        });
        deRatio = new JSlider(0, 400, 100); //VERY IMPORTANT!!!!!! THIS IS 100x THE ACTUAL VALUE
        deRatio.setMajorTickSpacing(100);
        deRatio.setMinorTickSpacing(10);
        Hashtable labelTable2 = new Hashtable();
        labelTable2.put(0, new JLabel("0"));
        labelTable2.put(100, new JLabel("1"));
        labelTable2.put(200, new JLabel("2"));
        labelTable2.put(300, new JLabel("3"));
        labelTable2.put(400, new JLabel("4"));
        deRatio.setLabelTable(labelTable2);
        deRatio.setPaintLabels(true);
        deRatio.setPaintTicks(true);
        deRatio.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                deLabel.setText("D/E Ratio < " + source.getValue() / 100.0);
            }
        });
        eGrowth = new JSlider(0, 200, 30);
        eGrowth.setMajorTickSpacing(50);
        eGrowth.setMinorTickSpacing(10);
        eGrowth.setPaintLabels(true);
        eGrowth.setPaintTicks(true);
        eGrowth.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                eLabel.setText("Sales Growth > " + source.getValue() + "%");
            }
        });
        feGrowth = new JSlider(0, 50, 25);
        feGrowth.setMajorTickSpacing(5);
        feGrowth.setMinorTickSpacing(1);
        feGrowth.setPaintLabels(true);
        feGrowth.setPaintTicks(true);
        feGrowth.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                feLabel.setText("Forward P/E Ratio < " + source.getValue());
            }
        });
        pMargin = new JSlider(0, 100, 10);
        pMargin.setMajorTickSpacing(10);
        pMargin.setMinorTickSpacing(1);
        pMargin.setPaintTicks(true);
        pMargin.setPaintLabels(true);
        pMargin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                pLabel.setText("EPS Growth > " + source.getValue() + "%");
            }
        });
        roeRatio = new JSlider(-100, 100, 0);
        roeRatio.setMajorTickSpacing(25);
        roeRatio.setMinorTickSpacing(1);
        roeRatio.setPaintTicks(true);
        roeRatio.setPaintLabels(true);
        roeRatio.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                roeLabel.setText("ROE > " + source.getValue() + "%");
            }
        });
        peCheck = new JCheckBox("", true);
        peCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    peRatio.setEnabled(false);
                }
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    peRatio.setEnabled(true);
                }
            }
        });
        pbCheck = new JCheckBox("", true);
        pbCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    pbRatio.setEnabled(false);
                }
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    pbRatio.setEnabled(true);
                }
            }
        });
        pegCheck = new JCheckBox("", true);
        pegCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    pegRatio.setEnabled(false);
                }
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    pegRatio.setEnabled(true);
                }
            }
        });
        deCheck = new JCheckBox("", true);
        deCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    deRatio.setEnabled(false);
                }
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    deRatio.setEnabled(true);
                }
            }
        });
        eCheck = new JCheckBox("", true);
        eCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    eGrowth.setEnabled(false);
                }
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    eGrowth.setEnabled(true);
                }
            }
        });
        feCheck = new JCheckBox("", true);
        feCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    feGrowth.setEnabled(false);
                }
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    feGrowth.setEnabled(true);
                }
            }
        });
        pCheck = new JCheckBox("", true);
        pCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    pMargin.setEnabled(false);
                }
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    pMargin.setEnabled(true);
                }
            }
        });
        roeCheck = new JCheckBox("", true);
        roeCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    roeRatio.setEnabled(false);
                }
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    roeRatio.setEnabled(true);
                }
            }
        });
    }

    public void initializeStrategy() {
        strategyPage.setBorder(BorderFactory.createTitledBorder("Infinity: Create Your Strategy"));
        SpringLayout sLayout = new SpringLayout();
        strategyPage.setLayout(sLayout);
        JButton back = new JButton("Back");
        JButton submit = new JButton("Submit");
        JButton valuePreset = new JButton("Value Investing Preset");
        JButton garpPreset = new JButton("GARP Investing Preset");
        JButton growthPreset = new JButton("Growth Investing Preset");
        JButton valueQ = new JButton("?");
        JButton garpQ = new JButton("?");
        JButton growthQ = new JButton("?");
        JButton customStrat = new JButton("Enable All");
        customStrat.setPreferredSize(new Dimension(615, 25));
        submit.setPreferredSize(new Dimension(615, 40));
        back.setPreferredSize(new Dimension(615, 25));
        valuePreset.setPreferredSize(new Dimension(150, 25));
        valuePreset.setFont(new Font("Arial", Font.BOLD, 10));
        garpPreset.setPreferredSize(new Dimension(150, 25));
        garpPreset.setFont(new Font("Arial", Font.BOLD, 10));
        growthPreset.setPreferredSize(new Dimension(155, 25));
        growthPreset.setFont(new Font("Arial", Font.BOLD, 10));
        valueQ.setPreferredSize(new Dimension(40, 25));
        growthQ.setPreferredSize(new Dimension(41, 25));
        garpQ.setPreferredSize(new Dimension(40, 25));
        valueQ.setFont(new Font("Arial", Font.BOLD, 10));
        growthQ.setFont(new Font("Arial", Font.BOLD, 10));
        garpQ.setFont(new Font("Arial", Font.BOLD, 10));
        customStrat.setFont(new Font("Arial", Font.BOLD, 10));
        back.addActionListener(this);
        submit.addActionListener(this);
        valuePreset.addActionListener(this);
        garpPreset.addActionListener(this);
        growthPreset.addActionListener(this);
        customStrat.addActionListener(this);
        valueQ.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(null, "Value Investing: A style of investing created by Benjamin Graham, that involves \npurchasing stocks that are trading at less than their intrinsic value. As the market \ncorrects itself, the market price of the stock will rise to its intrinsic value, leading to \nprofit for the investor. This style was popularized by one of Graham’s favourite \npupils, Warren Buffett. Determining intrinsic value requires more in-depth analysis, \nincluding Discounted Cash Flow (DCF) modelling or Comparable Companies analysis.", "About Value Investing", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        garpQ.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(null, "Growth at Reasonable Price (GARP): GARP investing serves as a medium between \nvalue investing and growth investing: attempting to find somewhat undervalued \nstocks with solid potential for growth. When conducting quantitative screening, \nGARP investors use the same metrics that growth and value investors look for, but \non a slightly lesser scale. GARP investors still place a high importance on earnings \ngrowth, but they take company valuation into account as well. ", "About GARP Investing", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        growthQ.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(null, "Growth Investing: Popularized by Thomas Rowe Price Jr., growth investing focuses on \ncapital appreciation; investing in companies that show signs of above-average \ngrowth. As the company grows, the market price will continue to appreciate. Though \nthere are quantitative screening processes for growth investing, there is no real \nformula in evaluating a company’s potential for growth. Growth investors care less \nabout a company’s actual valuation, because they believe the stock will grow \nregardless of the market or intrinsic value.", "About Growth Investing", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        JLabel note = new JLabel("NOTE: Unselect a screening metric by checking the adjacent box. Only selected metrics will be used for screening!");
        note.setFont(new Font("Arial", Font.BOLD, 10));
        strategyPage.add(back);
        strategyPage.add(submit);
        strategyPage.add(valuePreset);
        strategyPage.add(garpPreset);
        strategyPage.add(growthPreset);
        strategyPage.add(valueQ);
        strategyPage.add(garpQ);
        strategyPage.add(growthQ);
        strategyPage.add(customStrat);
        strategyPage.add(note);
        strategyPage.add(peRatio);
        strategyPage.add(peCheck);
        strategyPage.add(peLabel);
        strategyPage.add(pbLabel);
        strategyPage.add(pbCheck);
        strategyPage.add(pbRatio);
        strategyPage.add(pegLabel);
        strategyPage.add(pegCheck);
        strategyPage.add(pegRatio);
        strategyPage.add(deLabel);
        strategyPage.add(deCheck);
        strategyPage.add(deRatio);
        strategyPage.add(eGrowth);
        strategyPage.add(eCheck);
        strategyPage.add(eLabel);
        strategyPage.add(feGrowth);
        strategyPage.add(feCheck);
        strategyPage.add(feLabel);
        strategyPage.add(pMargin);
        strategyPage.add(pLabel);
        strategyPage.add(pCheck);
        strategyPage.add(roeCheck);
        strategyPage.add(roeLabel);
        strategyPage.add(roeRatio);
        sLayout.putConstraint(SpringLayout.SOUTH, back, 0, SpringLayout.NORTH, submit);
        sLayout.putConstraint(SpringLayout.SOUTH, submit, 0, SpringLayout.SOUTH, strategyPage);
        sLayout.putConstraint(SpringLayout.WEST, valueQ, 3, SpringLayout.EAST, valuePreset);
        sLayout.putConstraint(SpringLayout.WEST, garpPreset, 15, SpringLayout.EAST, valueQ);
        sLayout.putConstraint(SpringLayout.WEST, garpQ, 3, SpringLayout.EAST, garpPreset);
        sLayout.putConstraint(SpringLayout.WEST, growthPreset, 15, SpringLayout.EAST, garpQ);
        sLayout.putConstraint(SpringLayout.WEST, growthQ, 3, SpringLayout.EAST, growthPreset);
        sLayout.putConstraint(SpringLayout.NORTH, customStrat, 3, SpringLayout.SOUTH, valueQ);
        sLayout.putConstraint(SpringLayout.NORTH, note, 3, SpringLayout.SOUTH, customStrat);
        sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, note, 0, SpringLayout.HORIZONTAL_CENTER, strategyPage);
        sLayout.putConstraint(SpringLayout.NORTH, peCheck, 45, SpringLayout.SOUTH, customStrat);
        sLayout.putConstraint(SpringLayout.NORTH, peLabel, 30, SpringLayout.SOUTH, customStrat);
        sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, peLabel, 0, SpringLayout.HORIZONTAL_CENTER, peRatio);
        sLayout.putConstraint(SpringLayout.NORTH, peRatio, 3, SpringLayout.SOUTH, peLabel);
        sLayout.putConstraint(SpringLayout.WEST, peRatio, 3, SpringLayout.EAST, peCheck);
        //
        sLayout.putConstraint(SpringLayout.NORTH, pbCheck, 45, SpringLayout.SOUTH, customStrat);
        sLayout.putConstraint(SpringLayout.WEST, pbCheck, 18, SpringLayout.EAST, peRatio);
        sLayout.putConstraint(SpringLayout.NORTH, pbLabel, 30, SpringLayout.SOUTH, customStrat);
        sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pbLabel, 0, SpringLayout.HORIZONTAL_CENTER, pbRatio);
        sLayout.putConstraint(SpringLayout.NORTH, pbRatio, 3, SpringLayout.SOUTH, pbLabel);
        sLayout.putConstraint(SpringLayout.WEST, pbRatio, 3, SpringLayout.EAST, pbCheck);
        pbRatio.setPreferredSize(new Dimension(150, 50));
        //
        sLayout.putConstraint(SpringLayout.NORTH, pegCheck, 45, SpringLayout.SOUTH, customStrat);
        sLayout.putConstraint(SpringLayout.WEST, pegCheck, 18, SpringLayout.EAST, pbRatio);
        sLayout.putConstraint(SpringLayout.NORTH, pegLabel, 30, SpringLayout.SOUTH, customStrat);
        sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pegLabel, 0, SpringLayout.HORIZONTAL_CENTER, pegRatio);
        sLayout.putConstraint(SpringLayout.NORTH, pegRatio, 3, SpringLayout.SOUTH, pegLabel);
        sLayout.putConstraint(SpringLayout.WEST, pegRatio, 3, SpringLayout.EAST, pegCheck);
        pegRatio.setPreferredSize(new Dimension(150, 50));
        //
        sLayout.putConstraint(SpringLayout.NORTH, deCheck, 45, SpringLayout.SOUTH, peRatio);
        sLayout.putConstraint(SpringLayout.NORTH, deLabel, 30, SpringLayout.SOUTH, peRatio);
        sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, deLabel, 0, SpringLayout.HORIZONTAL_CENTER, deRatio);
        sLayout.putConstraint(SpringLayout.NORTH, deRatio, 3, SpringLayout.SOUTH, deLabel);
        sLayout.putConstraint(SpringLayout.WEST, deRatio, 3, SpringLayout.EAST, deCheck);
        //
        sLayout.putConstraint(SpringLayout.NORTH, eCheck, 45, SpringLayout.SOUTH, peRatio);
        sLayout.putConstraint(SpringLayout.WEST, eCheck, 18, SpringLayout.EAST, deRatio);
        sLayout.putConstraint(SpringLayout.NORTH, eLabel, 30, SpringLayout.SOUTH, peRatio);
        sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, eLabel, 0, SpringLayout.HORIZONTAL_CENTER, eGrowth);
        sLayout.putConstraint(SpringLayout.NORTH, eGrowth, 3, SpringLayout.SOUTH, eLabel);
        sLayout.putConstraint(SpringLayout.WEST, eGrowth, 3, SpringLayout.EAST, eCheck);
        eGrowth.setPreferredSize(new Dimension(150, 50));
        //
        sLayout.putConstraint(SpringLayout.NORTH, feCheck, 45, SpringLayout.SOUTH, peRatio);
        sLayout.putConstraint(SpringLayout.WEST, feCheck, 18, SpringLayout.EAST, eGrowth);
        sLayout.putConstraint(SpringLayout.NORTH, feLabel, 30, SpringLayout.SOUTH, peRatio);
        sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, feLabel, 0, SpringLayout.HORIZONTAL_CENTER, feGrowth);
        sLayout.putConstraint(SpringLayout.NORTH, feGrowth, 3, SpringLayout.SOUTH, feLabel);
        sLayout.putConstraint(SpringLayout.WEST, feGrowth, 3, SpringLayout.EAST, feCheck);
        feGrowth.setPreferredSize(new Dimension(150, 50));
        //
        sLayout.putConstraint(SpringLayout.NORTH, pCheck, 45, SpringLayout.SOUTH, deRatio);
        sLayout.putConstraint(SpringLayout.NORTH, pLabel, 30, SpringLayout.SOUTH, deRatio);
        sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pLabel, 0, SpringLayout.HORIZONTAL_CENTER, pMargin);
        sLayout.putConstraint(SpringLayout.NORTH, pMargin, 3, SpringLayout.SOUTH, pLabel);
        sLayout.putConstraint(SpringLayout.WEST, pMargin, 3, SpringLayout.EAST, pCheck);
        //
        sLayout.putConstraint(SpringLayout.NORTH, roeCheck, 45, SpringLayout.SOUTH, deRatio);
        sLayout.putConstraint(SpringLayout.WEST, roeCheck, 18, SpringLayout.EAST, pMargin);
        sLayout.putConstraint(SpringLayout.NORTH, roeLabel, 30, SpringLayout.SOUTH, deRatio);
        sLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, roeLabel, 0, SpringLayout.HORIZONTAL_CENTER, roeRatio);
        sLayout.putConstraint(SpringLayout.NORTH, roeRatio, 3, SpringLayout.SOUTH, roeLabel);
        sLayout.putConstraint(SpringLayout.WEST, roeRatio, 3, SpringLayout.EAST, roeCheck);
        roeRatio.setPreferredSize(new Dimension(345, 50));
    }

    public InfinityProject() {
        super(new CardLayout());
        cl = (CardLayout) getLayout();
        initializeSliders();

        //IntroPage
        introPage.setBorder(BorderFactory.createTitledBorder("Infinity: Introduction"));
        SpringLayout layout = new SpringLayout();
        introPage.setLayout(layout);
        JLabel welcome = new JLabel("Welcome to Infinity!");
        welcome.setFont(new Font("Arial", Font.BOLD, 40));
        JButton newPort = new JButton("Create New Portfolio!");
        JButton existPort = new JButton("Check Existing Portfolios!");
        JButton quit = new JButton("Quit");
        quit.setPreferredSize(new Dimension(615, 40));
        newPort.setPreferredSize(new Dimension(307, 60));
        existPort.setPreferredSize(new Dimension(307, 60));
        newPort.addActionListener(this);
        existPort.addActionListener(this);
        quit.addActionListener(this);
        try{
            BufferedImage myPicture = ImageIO.read(new File("logo.png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(185, 185, Image.SCALE_FAST)));
            introPage.add(picLabel);
            layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, picLabel, 0, SpringLayout.HORIZONTAL_CENTER, introPage);
            layout.putConstraint(SpringLayout.NORTH, picLabel, 30, SpringLayout.SOUTH, welcome);
        } catch (IOException e){

        }
        introPage.add(welcome);
        introPage.add(quit);
        introPage.add(newPort);
        introPage.add(existPort);
        layout.putConstraint(SpringLayout.SOUTH, quit, 0, SpringLayout.SOUTH, introPage);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, welcome, 0, SpringLayout.HORIZONTAL_CENTER, introPage);
        layout.putConstraint(SpringLayout.NORTH, welcome, 40, SpringLayout.NORTH, introPage);
        layout.putConstraint(SpringLayout.SOUTH, newPort, 0, SpringLayout.NORTH, quit);
        layout.putConstraint(SpringLayout.SOUTH, existPort, 0, SpringLayout.NORTH, quit);
        layout.putConstraint(SpringLayout.WEST, newPort, 0, SpringLayout.WEST, introPage);
        layout.putConstraint(SpringLayout.EAST, existPort, 1, SpringLayout.EAST, introPage);

        //StrategyPage
        initializeStrategy();

        //StockPage
        initializeStocks();

        //ListPage
        try {
            List<File> filesInFolder = Files.walk(Paths.get(""))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            refinedList = new ArrayList<File>();
            for (int i = 0; i < filesInFolder.size(); i++) {
                if (filesInFolder.get(i).getName().endsWith(".txt")) {
                    refinedList.add(filesInFolder.get(i));
                }
            }
        } catch (IOException e) {
        }
        listPage.setBorder(BorderFactory.createTitledBorder("Infinity: View Your Portfolios"));
        nLayout = new SpringLayout();
        listPage.setLayout(nLayout);
        portfolios = new ArrayList<Portfolio>();
        names = new ArrayList<String>();
        for (int i = 0; i < refinedList.size(); i++) {
            portfolios.add(FileTools.read(refinedList.get(i).getName()));
            names.add (portfolios.get(i).getStrategyName());
        }
        dropDown = new JComboBox(names.toArray());
        dropDown.setPreferredSize(new Dimension(615, 40));
        listPage.add(dropDown);
        for (int i = 0; i < portfolios.size(); i++){
            if (portfolios.get(i).getStrategyName() == dropDown.getSelectedItem()) {
                currIndex = i;
                break;
            }
        }
        companiesInPortfolio=new String[portfolios.get(currIndex).getTickers().length];
        for (int i =0; i < portfolios.get(currIndex).getTickers().length; i++){
            companiesInPortfolio[i] = portfolios.get(currIndex).getTickers()[i] + " | " + portfolios.get(currIndex).getNames()[i] + " | " + portfolios.get(currIndex).getSectors()[i] + " | $" + portfolios.get(currIndex).getCostBasis()[i] + " | $" + portfolios.get(currIndex).getCurrentPrices()[i] + " | " + Math.round(portfolios.get(currIndex).getPercentChanges()[i]*10000.0)/100.0 + "%";
        }
        JLabel performance = new JLabel("Performance To Date: " + Math.round(portfolios.get(currIndex).getPortfolioReturn()*10000.0)/100.0 + "%", SwingConstants.CENTER);
        performance.setFont(new Font("Arial", Font.BOLD, 16));
        performance.setPreferredSize(new Dimension(280, 233));
        performance.setBorder(BorderFactory.createEtchedBorder());
        JList compList = new JList(companiesInPortfolio);
        JButton back = new JButton("Back");
        back.addActionListener(this);
        back.setPreferredSize(new Dimension(615,40));
        JScrollPane compFrame = new JScrollPane(compList);
        // GRAPH STARTS
        graph = new StockGraphFrame(portfolios.get(currIndex));
        panel = graph.getPanel();
        panel.setPreferredSize(new Dimension(335, 233));
        panel.setBorder(BorderFactory.createEtchedBorder());
        graph.pack();
        panel.setVisible(true);
        listPage.add(panel);
        compFrame.setBorder(BorderFactory.createEtchedBorder());
        compFrame.setPreferredSize(new Dimension(615, 200));
        listPage.add(compFrame);
        nLayout.putConstraint(SpringLayout.SOUTH, back, 0, SpringLayout.SOUTH, listPage);
        nLayout.putConstraint(SpringLayout.SOUTH, performance, 0, SpringLayout.NORTH, compFrame);
        nLayout.putConstraint(SpringLayout.EAST, performance, 0, SpringLayout.EAST, listPage);
        nLayout.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, listPage);
        nLayout.putConstraint(SpringLayout.SOUTH, panel, 0, SpringLayout.NORTH, compFrame);
        listPage.add(back);
        listPage.add(performance);
        nLayout.putConstraint(SpringLayout.SOUTH, compFrame, -40, SpringLayout.SOUTH, listPage);
        dropDown.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent ae){
                for (int i = 0; i < portfolios.size(); i++){
                    if (portfolios.get(i).getStrategyName() == dropDown.getSelectedItem()) {
                        currIndex = i;
                        break;
                    }
                }
                companiesInPortfolio=new String[portfolios.get(currIndex).getTickers().length];
                for (int i =0; i < portfolios.get(currIndex).getTickers().length; i++){
                    companiesInPortfolio[i] = portfolios.get(currIndex).getTickers()[i] + " | " + portfolios.get(currIndex).getNames()[i] + " | " + portfolios.get(currIndex).getSectors()[i] + " | $" + portfolios.get(currIndex).getCostBasis()[i] + " | $" + portfolios.get(currIndex).getCurrentPrices()[i] + " | " + Math.round(portfolios.get(currIndex).getPercentChanges()[i]*10000.0)/100.0 + "%";
                }
                performance.setText("Performance To Date: " + Math.round(portfolios.get(currIndex).getPortfolioReturn()*10000.0)/100.0 + "%");
                compList.setModel(new DefaultListModel());
                compList.setListData(companiesInPortfolio);
                listPage.remove(panel);
                graph = new StockGraphFrame(portfolios.get(currIndex));
                panel = graph.getPanel();
                panel.setPreferredSize(new Dimension(335, 233));
                panel.setBorder(BorderFactory.createEtchedBorder());
                nLayout.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, listPage);
                nLayout.putConstraint(SpringLayout.SOUTH, panel, 0, SpringLayout.NORTH, compFrame);
                listPage.add(panel);
            }
        });

        //Putting it all together
        add(introPage, "Introduction");
        add(strategyPage, "Strategy");
        add(stockPage, "Stocks");
        add(listPage, "List");
        cl.show(this, "Introduction");
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Quit"))
            System.exit(0);
        if (ae.getActionCommand().equals("Create New Portfolio!")) {
            cl.show(this, "Strategy");
            resetSliders();
        }
        if (ae.getActionCommand().equals("Check Existing Portfolios!")) {
            if (portfolios.size() != 0) {
                cl.show(this, "List");
            }
            else{
                JOptionPane.showMessageDialog(null,"ERROR: There are no portfolios to show!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (ae.getActionCommand().equals("Back"))
            cl.show(this, "Introduction");
        if (ae.getActionCommand().equals("Submit")) {
            if (!peCheck.isSelected() && !pbCheck.isSelected() && !pegCheck.isSelected() && !deCheck.isSelected() && !eCheck.isSelected() && !feCheck.isSelected() && !pCheck.isSelected() && !roeCheck.isSelected())
                JOptionPane.showMessageDialog(null, "You must select at least 1 screening metric!", "ERROR", JOptionPane.ERROR_MESSAGE);
            else {
                doScreen();
            }
        }
        if (ae.getActionCommand().equals("Enable All")) {
            peCheck.setSelected(true);
            pbCheck.setSelected(true);
            pegCheck.setSelected(true);
            deCheck.setSelected(true);
            eCheck.setSelected(true);
            feCheck.setSelected(true);
            pCheck.setSelected(true);
            roeCheck.setSelected(true);
        }
        if (ae.getActionCommand().equals("Value Investing Preset")) {
            peRatio.setValue(8);
            peCheck.setSelected(true);
            pbRatio.setValue(100);
            pbCheck.setSelected(true);
            pegRatio.setValue(300);
            pegCheck.setSelected(true);
            deRatio.setValue(100);
            deCheck.setSelected(true);
            eGrowth.setValue(7);
            eCheck.setSelected(true);
            pCheck.setSelected(false);
            roeCheck.setSelected(false);
            feCheck.setSelected(false);
        }
        if (ae.getActionCommand() == "Growth Investing Preset") {
            eCheck.setSelected(true);
            eGrowth.setValue(15);
            feGrowth.setValue(35);
            feCheck.setSelected(true);
            pMargin.setValue(5);
            pCheck.setSelected(true);
            roeCheck.setSelected(true);
            roeRatio.setValue(15);
            pegCheck.setSelected(true);
            pegRatio.setValue(500);
            peCheck.setSelected(true);
            peRatio.setValue(50);
            pbCheck.setSelected(false);
            deCheck.setSelected(false);
        }
        if (ae.getActionCommand() == "GARP Investing Preset") {
            pbCheck.setSelected(true);
            pbRatio.setValue(200);
            pegCheck.setSelected(true);
            pegRatio.setValue(100);
            eCheck.setSelected(true);
            eGrowth.setValue(12);
            feCheck.setSelected(true);
            feGrowth.setValue(12);
            roeCheck.setSelected(false);
            peCheck.setSelected(false);
            deCheck.setSelected(false);
            pCheck.setSelected(false);
        }
        if (ae.getActionCommand() == "Save") {
            savePortfolio();
            JOptionPane.showMessageDialog(null, "Congrats! Your portfolio has been saved. You can view your returns overall for this portfolio as time progresses using the portfolio checking function.", "Congrats!", JOptionPane.INFORMATION_MESSAGE);
            cl.show(this, "Introduction");
            remove(strategyPage);
            remove(stockPage);
            strategyPage = new JPanel();
            stockPage = new JPanel();
            initializeStrategy();
            initializeStocks();
            add(strategyPage, "Strategy");
            add(stockPage, "Stocks");
        }
        if (ae.getActionCommand() == "Go Back") {
            cl.show(this, "Strategy");
        }

    }

}
