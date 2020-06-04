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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;


/**
 * This panel is largely involved in simulating the orbits of moons for the
 * selected planet. The user is able to speed up orbits and watch how different planets
 * have faster/smaller/larger moons than others.
 */
public class OrbitSimulationPanel extends JPanel implements ActionListener, ChangeListener, MouseInputListener {

    private static final long serialVersionUID = 1L;

    final double G_CONSTANT = 6.67 * Math.pow(10, -11); // Nm^2/kg^2
    final double DENSITY_OF_OBJECTS = 2000; // kg/m^2
    final int ORBIT_SEPARATION = 50;

    private MainFrame mainFrame;
    private int panelWidth;
    private int panelHeight;

    private int delay = 10;
    private Timer timer;
    private int minimumMultiplier = 0;
    private int maximumMultiplier = 10;
    private int simulationMultiplier = 1;

    private Planet currentPlanet;
    private int planetX;
    private int planetY;
    private boolean isHoveringOverPlanet = false;
    private double orbitalVelocity;
    private List<double[]> moonInformation = new ArrayList<double[]>(); // ..[radius, x, y, orbitRadiant]

    private JSlider multiplierSlider;
    private JButton exitButton;

    private enum CoordinateDirection {
        x,y
    }

    public OrbitSimulationPanel(int width, int height, MainFrame main) {
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

    public void openOrbitSimulation(Planet planetToSimulate) {
        currentPlanet = planetToSimulate;

        timer.start();
        instantiateAllMoonsInOrbit();
        calculateMinimumOrbitVelocity();
        moveCenterPlanet();
    }

    public void closeOrbitSimulation() {
        timer.stop();
        timer.restart();
        multiplierSlider.setValue(1);
        moonInformation.clear();
        mainFrame.changeVisiblePanel(MainFrame.PanelTypes.spaceEnvironment);
    }

    public void updateSize(int width, int height) {
        this.panelWidth = width;
        this.panelHeight = height;
        setBounds(0, 0, width, height);
        if(currentPlanet != null) {
            moveCenterPlanet();
        }
    }

    private void moveCenterPlanet() {
        planetX = panelWidth / 2 - currentPlanet.getRadius();
        planetY = panelHeight / 2 - currentPlanet.getRadius();
    }

    private void calculateMinimumOrbitVelocity() {
        double mass = calculateVolume(currentPlanet.getRadius()) * DENSITY_OF_OBJECTS;
        orbitalVelocity = Math.sqrt((mass * G_CONSTANT) / currentPlanet.getRadius());
    }

    /**
     * Get information about the moons and then instantiate them in their assigned orbits.
     * Their starting location is randomly chosen between (0 rad - PI rad).
     * All the instantiated moons are then saved onto the moonInformation list.
     */
    private void instantiateAllMoonsInOrbit() {
        int orbitLevel = 1;
        Random moonPositionGenerator = new Random(currentPlanet.getPlanetSeed());
        for (int radius : currentPlanet.getMoons()) {
            double startingRadiant = moonPositionGenerator.nextDouble() * Math.PI;
            double hypotenius = (orbitLevel * ORBIT_SEPARATION) + currentPlanet.getRadius();
            double startingX = transformToCenter(Math.cos(startingRadiant) * hypotenius, CoordinateDirection.x);
            double startingY = transformToCenter(Math.sin(startingRadiant) * hypotenius, CoordinateDirection.y);
            double[] moonData = new double[] { radius, startingX, startingY, startingRadiant };
            moonInformation.add(moonData);
            orbitLevel++;
        }
    }

    private void createUserInterface() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.weighty = 1.0;
        constraint.anchor = GridBagConstraints.NORTH;
        constraint.insets = new Insets(10, 0, 0, 0);

        multiplierSlider = new JSlider(minimumMultiplier, maximumMultiplier, simulationMultiplier);
        multiplierSlider.setMajorTickSpacing(2);
        multiplierSlider.setMinorTickSpacing(1);
        multiplierSlider.setPaintTicks(true);
        multiplierSlider.setPaintLabels(true);
        multiplierSlider.setSnapToTicks(true);
        multiplierSlider.setBackground(Color.black);
        multiplierSlider.setForeground(Color.white);
        multiplierSlider.addChangeListener(this);
        constraint.gridx = 0;
        constraint.gridy = 0;

        add(multiplierSlider, constraint);
        exitButton = new JButton("Leave Simulation");
        exitButton.addActionListener(this);
        constraint.gridx = 1;

        add(exitButton, constraint);
    }

    /**
     * X and Y locations of an orbit are calculated based off of the moon's
     * current radiant in the circle. By using some basic radiant trigonomic functions
     * we can figure out the xPostion/yPosition of the new location of the moon.
     *<p>
     * The amount of radiants that each moon will move each time largly depends on the
     * calculations from {calculateOrbitSpeedUsingOrbitLevel()}
     */
    private void updateMoonPosition() {
        int orbitLevel = 1;
        List<double[]> updatedMoonInformation = new ArrayList<double[]>();
        for (double[] moonInfo : moonInformation) {
            double radius = moonInfo[0];
            double moonOrbitVelocity = (calculateOrbitSpeedUsingOrbitLevel(orbitLevel) / (100 / delay)) * simulationMultiplier;
            double newRadiant = moonInfo[3] + moonOrbitVelocity;
            double hypotenius = (orbitLevel * ORBIT_SEPARATION) + currentPlanet.getRadius();
            double newXPosition = transformToCenter(Math.cos(newRadiant) * hypotenius, CoordinateDirection.x);
            double newYposition = transformToCenter(Math.sin(newRadiant) * hypotenius, CoordinateDirection.y);
            double[] moonData = new double[] { radius, newXPosition, newYposition, newRadiant };
            updatedMoonInformation.add(moonData);
            orbitLevel++;
        }
        moonInformation = updatedMoonInformation;
    }

    private double calculateVolume(double radius) {
        return (4 / 3) * Math.PI * Math.pow(radius, 3);
    }

    /**
     * Orbit speeds are largely calculated depending on the orbitLevel of the moon and the
     * minimum orbitalVelocity that the planet requires. A reciprocal function of 1/.5x + 1
     * is used to offset the different orbitVelocities depending on their orbitlevel(x).
     * @param orbitLevel
     * @return {double}
     */
    private double calculateOrbitSpeedUsingOrbitLevel(int orbitLevel) {
        return orbitalVelocity * (1 / (.5 * orbitLevel) + 1);
    }

    /**
     * transform regular coordinate values to coordinates relating to the center
     * of the panel. This allows for objects to be centered instead of starting
     * from the top-left side of the screen.
     * @param coordinate
     * @param direction
     * @return
     */
    private double transformToCenter(double coordinate, CoordinateDirection direction) {
        if (direction == CoordinateDirection.x) {
            return coordinate + (panelWidth / 2);
        }
        return coordinate + (panelHeight / 2);
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

        if (action == exitButton.getText()){
            closeOrbitSimulation();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        isHoveringOverPlanet = false;
        if(distanceFromMouseToPlanet(mouseX, mouseY) <= currentPlanet.getRadius()) {
            isHoveringOverPlanet = true;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        simulationMultiplier = multiplierSlider.getValue();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!isHoveringOverPlanet) { return; }
        mainFrame.openLandSimualtion(currentPlanet.getPlanetSeed());
     }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) { }
}