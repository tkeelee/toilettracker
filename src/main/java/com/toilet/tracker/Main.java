package com.toilet.tracker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class Main {
    private JFrame frame;
    private JButton startButton;
    private JButton endButton;
    private JLabel statusLabel;
    private LocalDateTime startTime;
    private final DatabaseManager dbManager;

    public Main() {
        dbManager = new DatabaseManager();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("厕所打卡器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // 创建UI组件
        startButton = new JButton("开始打卡");
        endButton = new JButton("结束打卡");
        statusLabel = new JLabel("当前未打卡");
        JButton viewStatsButton = new JButton("查看统计");
        
        endButton.setEnabled(false);

        // 设置事件处理
        startButton.addActionListener(e -> startTracking());
        endButton.addActionListener(e -> endTracking());
        viewStatsButton.addActionListener(e -> showStatistics());

        // 布局
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(endButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(statusLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(viewStatsButton);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
    }

    private void startTracking() {
        startTime = LocalDateTime.now();
        startButton.setEnabled(false);
        endButton.setEnabled(true);
        statusLabel.setText("打卡中...");
    }

    private void endTracking() {
        LocalDateTime endTime = LocalDateTime.now();
        long duration = java.time.Duration.between(startTime, endTime).toSeconds();
        dbManager.saveRecord(startTime, endTime, duration);
        
        startButton.setEnabled(true);
        endButton.setEnabled(false);
        statusLabel.setText(String.format("上次打卡时长：%d 秒", duration));
    }

    private void showStatistics() {
        StatisticsWindow statsWindow = new StatisticsWindow(dbManager);
        statsWindow.show();
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new Main().show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}