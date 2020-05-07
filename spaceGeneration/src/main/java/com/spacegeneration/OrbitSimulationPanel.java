package com.spacegeneration;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

public class OrbitSimulationPanel extends JPanel implements ActionListener, ChangeListener, MouseInputListener {

    private static final long serialVersionUID = 1L;

    final double G_CONSTANT = 6.67 * Math.pow(10, -11); // Nm^2/kg^2
    final double DENSITY_OF_OBJECTS = 2000; // kg/m^2
    final int ORBIT_SEPARATION = 50;

    SpaceFrame mainFrame;
    int panelWidth;
    int panelHeight;

    int delay = 10;
    Timer timer;
    int minimumMultiplier = 0;
    int maximumMultiplier = 10;
    int simulationMultiplier = 1;

    Planet currentPlanet;
    boolean isHoveringOverPlanet = false;
    double orbitalVelocity;
    int planetX;
    int planetY;
    List<double[]> moonInformation = new ArrayList<double[]>(); // ..[radius, x, y, orbitRadiant]

    JSlider multiplierSlider;
    JButton exitButton;

    public OrbitSimulationPanel(int width, int height, SpaceFrame main) {
        setOpaque(true);
        setBackground(Color.BLACK);
        setBounds(0, 0, width, height);
        setVisible(false);

        timer = new Timer(delay, this);
        this.panelWidth = width;
        this.panelHeight = height;
        this.mainFrame = main;

        createUserInterface();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void openSimulation(Planet planetToSimulate) {
        currentPlanet = planetToSimulate;

        timer.start();
        instantiateAllObjects();
        setVisible(true);
    }

    public void closeSimulation() {
        timer.stop();
        timer.restart();
        moonInformation.clear();
        mainFrame.getEnviroPanel().setIsSimulatingBoolean(false);
        setVisible(false);
    }

    public void updateSize(int width, int height) {
        this.panelWidth = width;
        this.panelHeight = height;
        setBounds(0, 0, width, height);
    }

    private void moveCenterPlanet() {
        planetX = panelWidth / 2 - currentPlanet.getRadius();
        planetY = panelHeight / 2 - currentPlanet.getRadius();
    }

    private void instantiateAllObjects() {
        moveCenterPlanet();

        double mass = calculateVolume(currentPlanet.getRadius()) * DENSITY_OF_OBJECTS;
        orbitalVelocity = Math.sqrt((mass * G_CONSTANT) / currentPlanet.getRadius());

        int orbitLevel = 1;
        for (int radius : currentPlanet.getMoons()) {
            double startingRadiant = Math.random() * Math.PI;
            double hypotenius = (orbitLevel * ORBIT_SEPARATION) + currentPlanet.getRadius();
            double startingX = transformToCenter(Math.cos(startingRadiant) * hypotenius, 0);
            double startingY = transformToCenter(Math.sin(startingRadiant) * hypotenius, 1);
            double[] moonData = new double[] { radius, startingX, startingY, startingRadiant };
            moonInformation.add(moonData);
            orbitLevel++;
        }
    }

    private void createUserInterface() {
        multiplierSlider = new JSlider(minimumMultiplier, maximumMultiplier, simulationMultiplier);
        multiplierSlider.setMajorTickSpacing(2);
        multiplierSlider.setMinorTickSpacing(1);
        multiplierSlider.setPaintTicks(true);
        multiplierSlider.setPaintLabels(true);
        multiplierSlider.setSnapToTicks(true);
        multiplierSlider.setBackground(Color.black);
        multiplierSlider.setForeground(Color.white);
        multiplierSlider.addChangeListener(this);

        exitButton = new JButton("Leave Simulation");
        exitButton.addActionListener(this);

        add(multiplierSlider);
        add(exitButton);
    }

    private void updateMoonPosition() {
        moveCenterPlanet();

        int orbitLevel = 1;
        List<double[]> updatedListInformation = new ArrayList<double[]>();
        for (double[] moonInfo : moonInformation) {
            double radius = moonInfo[0];
            double orbitVelocity = (calculateOrbitSpeedUsingOrbitLevel(orbitLevel) / (100 / delay)) * simulationMultiplier;
            double newRadiant = moonInfo[3] + orbitVelocity;
            double hypotenius = (orbitLevel * ORBIT_SEPARATION) + currentPlanet.getRadius();
            double newXPosition = transformToCenter(Math.cos(newRadiant) * hypotenius, 0);
            double newYposition = transformToCenter(Math.sin(newRadiant) * hypotenius, 1);
            double[] moonData = new double[] { radius, newXPosition, newYposition, newRadiant };
            updatedListInformation.add(moonData);
            orbitLevel++;
        }
        moonInformation = updatedListInformation;
    }

    private double calculateVolume(double radius) {
        return (4 / 3) * Math.PI * Math.pow(radius, 3);
    }

    private double calculateOrbitSpeedUsingOrbitLevel(int orbitLevel) {
        return orbitalVelocity * (1 / (.5 * orbitLevel) + 1);
    }

    private double transformToCenter(double coordinate, int direction) {
        if (direction == 0) {
            return coordinate + (panelWidth / 2);
        } else {
            return coordinate + (panelHeight / 2);
        }
    }

    private double distanceFromMouseToPlanet(int mouseX, int mouseY) {
        int distanceX = (planetX + currentPlanet.getRadius()) - mouseX;
        int distanceY = (planetY + currentPlanet.getRadius()) - mouseY;
        return Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paintBackgroundStars(g);
        paintMainPlanet(g);
        paintMoons(g);
    }

    private void paintMainPlanet(Graphics g) {
        int planetSize = currentPlanet.getRadius() * 2;
        if(isHoveringOverPlanet) {
            g.setColor(Color.CYAN);
            int outlineSize = (int) (planetSize * 1.2);
            int offset = (outlineSize - planetSize)/2;
            g.drawArc(planetX - offset, planetY - offset, outlineSize, outlineSize, 0, 360);
        }
        g.setColor(currentPlanet.getPlanetColour());
        g.fillArc(planetX, planetY, planetSize, planetSize, 0, 360);
    }

    private void paintMoons(Graphics g) {
        g.setColor(Color.decode("#C0C0C0"));
        for (double[] moonInfo : moonInformation) {
            int moonXCoordinate = (int) Math.round(moonInfo[1] - moonInfo[0]);
            int moonYCoordinate = (int) Math.round(moonInfo[2] - moonInfo[0]);
            int moonSize = (int) moonInfo[0];
            g.fillArc(moonXCoordinate, moonYCoordinate, moonSize, moonSize, 0, 360);
        }
    }

    private void paintBackgroundStars(Graphics g) {
        g.setColor(Color.white);
        Random starSeed = new Random(currentPlanet.getPlanetSeed());
        int totalStars = starSeed.nextInt(1000);
        for (int j = 0; j < totalStars; j++) {
            int starDiameter = starSeed.nextInt(3);
            int starX = starSeed.nextInt(panelWidth);
            int starY = starSeed.nextInt(panelHeight);
            g.fillArc(starX, starY, starDiameter, starDiameter, 0, 360);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        String action = ev.getActionCommand();
        if (ev.getSource() == timer) {
            updateMoonPosition();
            repaint();
        }

        if (action == "Leave Simulation") {
            closeSimulation();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        simulationMultiplier = multiplierSlider.getValue();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        if(distanceFromMouseToPlanet(mouseX, mouseY) <= currentPlanet.getRadius()) {
            isHoveringOverPlanet = true;
        } else {
            isHoveringOverPlanet = false;
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}
}