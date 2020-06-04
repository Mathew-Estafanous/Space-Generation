package com.spacegeneration;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/**
 * Main JFrame in which all JPanels stem from. Actions that affect all
 * the panels are initiated from here. Things like, switching the current
 * panel, resize, starting a simulation.
 * <p>
 * Every panel should call the Main Frame when changing between visible panels or
 * starting other simulations. Minimizing interaction between JPanels/other classes
 * and ensuring actions go through the Main Frame instead.
 */
public class MainFrame extends JFrame implements ComponentListener {

    private static final long serialVersionUID = 1L;

    private int frameHeight = 800;
    private int frameWidth = 800;

    private JLayeredPane layeredPane;
    private StartScreenPanel startScreenPanel;
    private UniversePanel universePanel;
    private OrbitSimulationPanel orbitSimulationPanel;
    private PlanetLandSimulationPanel landSimulationPanel;

    enum PanelTypes {
        startScreen, spaceEnvironment, orbitSimulation, landSimulation
    }

    public MainFrame() {
        super("Space Procedural Generation");

        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        layeredPane = new JLayeredPane();
        startScreenPanel = new StartScreenPanel(frameWidth, frameHeight, this);
        universePanel = new UniversePanel(frameWidth, frameHeight, this);
        orbitSimulationPanel = new OrbitSimulationPanel(frameWidth, frameHeight, this);
        landSimulationPanel = new PlanetLandSimulationPanel(frameWidth, frameHeight, this);

        layeredPane.add(startScreenPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(universePanel, JLayeredPane.POPUP_LAYER);
        layeredPane.add(orbitSimulationPanel, JLayeredPane.POPUP_LAYER);
        layeredPane.add(landSimulationPanel, JLayeredPane.POPUP_LAYER);

        addComponentListener(this);
        add(layeredPane);
        changeVisiblePanel(PanelTypes.startScreen);
    }

    public void changeVisiblePanel(PanelTypes frontPanel) {
        switch(frontPanel) {
            case startScreen:
                universePanel.setVisible(false);
                startScreenPanel.requestFocus();
                break;
            case spaceEnvironment:
                orbitSimulationPanel.setVisible(false);
                universePanel.setVisible(true);
                universePanel.requestFocus();
                break;
            case orbitSimulation:
                landSimulationPanel.setVisible(false);
                universePanel.setVisible(false);
                orbitSimulationPanel.setVisible(true);
                orbitSimulationPanel.requestFocus();
                break;
            case landSimulation:
                orbitSimulationPanel.setVisible(false);
                landSimulationPanel.setVisible(true);
                landSimulationPanel.requestFocus();
                break;
        }
    }

    /**
     * Start the Space universe simulation.
     * @param universeSeed
     */
    public void startUniverseSimulation(int universeSeed) {
        changeVisiblePanel(PanelTypes.spaceEnvironment);
        universePanel.startUniverseSimulation(universeSeed);
    }

    /**
     * Open Planet Orbit simulation
     * @param planetToSimulate
     */
    public void openOrbitSimulation(Planet planetToSimulate) {
        changeVisiblePanel(PanelTypes.orbitSimulation);
        orbitSimulationPanel.openOrbitSimulation(planetToSimulate);
    }

    /**
     * Open Planet Land Simulation
     * @param planetSeed
     */
    public void openLandSimualtion(int planetSeed) {
        changeVisiblePanel(PanelTypes.landSimulation);
        landSimulationPanel.openSimulation(planetSeed);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.frameHeight = getHeight();
        this.frameWidth = getWidth();
        startScreenPanel.updateSize(this.frameWidth, this.frameHeight);
        universePanel.updateSize(this.frameWidth, this.frameHeight);
        orbitSimulationPanel.updateSize(this.frameWidth, this.frameHeight);
        landSimulationPanel.updateSize(this.frameWidth, this.frameHeight);
    }

    @Override
    public void componentMoved(ComponentEvent e) { }

    @Override
    public void componentShown(ComponentEvent e) { }

    @Override
    public void componentHidden(ComponentEvent e) { }
}