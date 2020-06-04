package com.spacegeneration;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Main Screen that is shown when the program is initially started. The screen
 * is used to insert a seed, if the user chooses to do so. The user is then able
 * to start the universe simulation.
 */
public class StartScreenPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;
    private int screenWidth;
    private int screenHeight;

    private JLabel mainTitle;
    private String originalText = "Insert Seed (Optional)";
    private JTextField seedInputField;
    private JButton startSimulationBtn;

    int totalStars = 800;
    int[][] backgroundStars;
    int maximumStarRadius = 5;

    public StartScreenPanel(int width, int height, MainFrame frame) {
        setOpaque(true);
        setBackground(Color.BLACK);
        setBounds(0, 0, width, height);
        setFocusable(true);

        this.screenWidth = width;
        this.screenHeight = height;
        this.mainFrame = frame;

        createMainScreenInterface();
        createSpaceBackground();
    }

    private void createMainScreenInterface() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();

        mainTitle = new JLabel("Space Simulator");
        mainTitle.setForeground(Color.white);
        Font titleFont = mainTitle.getFont();
        mainTitle.setFont(new Font(titleFont.getName(), Font.BOLD, 75));
        constraint.gridx = 1;
        constraint.gridy = 0;
        add(mainTitle, constraint);

        seedInputField = new JTextField(originalText);
        seedInputField.setBackground(Color.black);
        seedInputField.setForeground(Color.white);
        seedInputField.setHorizontalAlignment(SwingConstants.CENTER);
        constraint.gridy = 1;
        constraint.ipady = 30;
        constraint.ipadx = 200;
        constraint.insets = new Insets(30, 0, 0, 0);
        add(seedInputField, constraint);

        startSimulationBtn = new JButton("Start Space Simulation");
        startSimulationBtn.addActionListener(this);
        constraint.gridy = 2;
        constraint.ipadx = 50;
        constraint.ipady = 30;
        constraint.insets = new Insets(30, 0, 0, 0);
        add(startSimulationBtn, constraint);
    }

    public void updateSize(int width, int height) {
        setBounds(0, 0, width, height);
        screenWidth = width;
        screenHeight = height;

        createSpaceBackground();
        repaint();
    }

    private void createSpaceBackground() {
        backgroundStars = new int[totalStars][];
        for (int j = totalStars - 1; j >= 0; j--) {
            int xLocation = (int) Math.round(Math.random() * screenWidth);
            int yLocation = (int) Math.round(Math.random() * screenHeight);
            int radius = (int) Math.round(Math.random() * maximumStarRadius);
            backgroundStars[j] = new int[] { xLocation, yLocation, radius };
        }
    }

    private void startSimulationWithSelectedSeed() {
        int seedValue = 0;
        if(seedInputField.getText().equals(originalText)) {
            seedValue = (int) (Math.random() * 100000);
        } else {
            char[] characterArray = seedInputField.getText().toCharArray();
            for(char character: characterArray) {
                seedValue += character;
            }
        }
        mainFrame.startUniverseSimulation(seedValue);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.white);
        for (int[] star : backgroundStars) {
            g.fillArc(star[0], star[1], star[2], star[2], 0, 360);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if(action != startSimulationBtn.getText()) { return; }

        startSimulationWithSelectedSeed();
    }
}