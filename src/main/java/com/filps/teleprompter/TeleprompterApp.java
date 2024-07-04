package com.filps.teleprompter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class TeleprompterApp {

    private JFrame frame;
    private JTextPane textPane;
    private Timer timer;
    private int scrollSpeed = 30; // Padrão: 30 milissegundos

    public TeleprompterApp() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Teleprompter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText("<html><body>Digite ou cole seu texto aqui...</body></html>");

        JScrollPane scrollPane = new JScrollPane(textPane);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);

        JButton btnPlay = new JButton("Play");
        toolBar.add(btnPlay);

        JButton btnPause = new JButton("Pause");
        toolBar.add(btnPause);

        JButton btnStop = new JButton("Stop");
        toolBar.add(btnStop);

        JButton btnSpeedUp = new JButton("Speed Up");
        toolBar.add(btnSpeedUp);

        JButton btnSpeedDown = new JButton("Speed Down");
        toolBar.add(btnSpeedDown);

        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startScrolling();
            }
        });

        btnPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopScrolling();
            }
        });

        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopScrolling();
                textPane.setCaretPosition(0);
            }
        });

        btnSpeedUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (scrollSpeed > 5) {
                    scrollSpeed -= 5;
                }
            }
        });

        btnSpeedDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scrollSpeed += 5;
            }
        });

        frame.setVisible(true);
    }

    private void startScrolling() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        int currentPosition = textPane.getCaretPosition();
                        textPane.setCaretPosition(currentPosition + 1);
                    }
                });
            }
        }, 0, scrollSpeed);
    }

    private void stopScrolling() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TeleprompterApp window = new TeleprompterApp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

