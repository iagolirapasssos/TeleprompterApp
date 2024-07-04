package com.filps.teleprompter;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class TeleprompterApp {
    private JFrame frame;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private Timer timer;
    private int scrollSpeed = 30; // Padrão: 30 milissegundos
    private boolean isDarkMode = false;
    private JButton btnPlay, btnPause, btnStop, btnSpeedUp, btnSpeedDown;
    private JLabel speedLabel;
    private HighlightPanel highlightPanel;

    public TeleprompterApp() {
        initialize();
    }

    private void initialize() {
        FlatLightLaf.setup();
        frame = new JFrame("Teleprompter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        textArea = new JTextArea("Digite ou cole seu texto aqui...");
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(true);

        scrollPane = new JScrollPane(textArea);
        highlightPanel = new HighlightPanel(scrollPane);
        
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(highlightPanel, JLayeredPane.PALETTE_LAYER);

        frame.getContentPane().add(layeredPane, BorderLayout.CENTER);

        createMenuBar();
        createToolBar();

        frame.setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Arquivo");
        JMenu viewMenu = new JMenu("Visualizar");
        JMenu helpMenu = new JMenu("Ajuda");

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        JMenuItem exitItem = new JMenuItem("Sair");
        fileMenu.add(exitItem);

        JMenuItem toggleThemeItem = new JMenuItem("Alternar Tema");
        viewMenu.add(toggleThemeItem);

        JMenuItem aboutItem = new JMenuItem("Sobre");
        helpMenu.add(aboutItem);

        exitItem.addActionListener(e -> System.exit(0));
        toggleThemeItem.addActionListener(e -> toggleTheme());
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Teleprompter App v1.0", "Sobre", JOptionPane.INFORMATION_MESSAGE));
    }

    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);

        btnPlay = createStyledButton("Play", new ImageIcon("play_icon.png"));
        btnPause = createStyledButton("Pause", new ImageIcon("pause_icon.png"));
        btnStop = createStyledButton("Stop", new ImageIcon("stop_icon.png"));
        btnSpeedUp = createStyledButton("Aumentar", new ImageIcon("speed_up_icon.png"));
        btnSpeedDown = createStyledButton("Diminuir", new ImageIcon("speed_down_icon.png"));

        speedLabel = new JLabel("Velocidade: " + scrollSpeed);

        toolBar.add(btnPlay);
        toolBar.add(btnPause);
        toolBar.add(btnStop);
        toolBar.addSeparator();
        toolBar.add(btnSpeedDown);
        toolBar.add(speedLabel);
        toolBar.add(btnSpeedUp);

        btnPlay.addActionListener(e -> startScrolling());
        btnPause.addActionListener(e -> pauseScrolling());
        btnStop.addActionListener(e -> stopScrolling());
        btnSpeedUp.addActionListener(e -> changeSpeed(-5));
        btnSpeedDown.addActionListener(e -> changeSpeed(5));
    }

    private JButton createStyledButton(String text, Icon icon) {
        JButton button = new JButton(text, icon);
        button.setFocusPainted(false);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        return button;
    }

    private void changeSpeed(int delta) {
        scrollSpeed = Math.max(1, Math.min(100, scrollSpeed + delta));
        speedLabel.setText("Velocidade: " + scrollSpeed);
        if (timer != null) {
            stopScrolling();
            startScrolling();
        }
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        SwingUtilities.invokeLater(() -> {
            if (isDarkMode) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
            SwingUtilities.updateComponentTreeUI(frame);
            updateTextAreaStyle();
        });
    }

    private void updateTextAreaStyle() {
        if (isDarkMode) {
            textArea.setBackground(new Color(43, 43, 43));
            textArea.setForeground(Color.WHITE);
        } else {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
        }
    }

    private void startScrolling() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    int currentValue = verticalScrollBar.getValue();
                    int maxValue = verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount();
                    if (currentValue < maxValue) {
                        verticalScrollBar.setValue(currentValue + 1);
                        highlightPanel.setYPosition(verticalScrollBar.getValue());
                    } else {
                        stopScrolling();
                    }
                });
            }
        }, 0, scrollSpeed);
    }

    private void pauseScrolling() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void stopScrolling() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        SwingUtilities.invokeLater(() -> {
            textArea.setCaretPosition(0);
            scrollPane.getVerticalScrollBar().setValue(0);
            highlightPanel.setYPosition(0);
        });
    }

    private class HighlightPanel extends JPanel {
        private int yPosition = 0;
        private final JScrollPane scrollPane;

        public HighlightPanel(JScrollPane scrollPane) {
            this.scrollPane = scrollPane;
            setOpaque(false);
        }

        public void setYPosition(int y) {
            this.yPosition = y;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(255, 255, 0, 64)); // Amarelo semitransparente
            int y = scrollPane.getViewport().getViewPosition().y;
            int highlightHeight = 30;
            g.fillRect(0, y - yPosition, getWidth(), highlightHeight);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new TeleprompterApp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

