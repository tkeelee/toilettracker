package com.toilet.tracker;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Map;

public class StatisticsWindow {
    private final DatabaseManager dbManager;
    private final JFrame frame;

    public StatisticsWindow(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.frame = new JFrame("使用统计");
        initialize();
    }

    private void initialize() {
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 创建年份选择器
        JComboBox<Integer> yearSelector = new JComboBox<>();
        int currentYear = LocalDateTime.now().getYear();
        for (int year = currentYear - 5; year <= currentYear; year++) {
            yearSelector.addItem(year);
        }
        yearSelector.setSelectedItem(currentYear);

        // 创建图表面板
        ChartPanel chartPanel = createChartPanel(currentYear);
        
        // 年份选择事件
        yearSelector.addActionListener(e -> {
            int selectedYear = (Integer) yearSelector.getSelectedItem();
            frame.remove(chartPanel);
            frame.add(createChartPanel(selectedYear), BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });

        // 布局
        frame.setLayout(new BorderLayout());
        frame.add(yearSelector, BorderLayout.NORTH);
        frame.add(chartPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
    }

    private ChartPanel createChartPanel(int year) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries(year + "年平均时长（秒）");

        Map<String, Double> stats = dbManager.getMonthlyStats(year);
        int month = 1;
        for (Map.Entry<String, Double> entry : stats.entrySet()) {
            series.add(month++, entry.getValue());
        }
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
            "月度统计",
            "月份",
            "平均时长（秒）",
            dataset
        );

        return new ChartPanel(chart);
    }

    public void show() {
        frame.setVisible(true);
    }
}