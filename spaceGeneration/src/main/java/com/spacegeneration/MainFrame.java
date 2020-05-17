package com.spacegeneration;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

public class MainFrame extends JFrame implements ComponentListener {

    private static final long serialVersionUID = 1L;

    private int frameHeight = 800;
    private int frameWidth = 1000;

    private JLayeredPane layeredPane;
    private EnviroPanel enviroPanel;
    private OrbitSimulationPanel orbitSimulationPanel;
    private PlanetLandSimulationPanel landSimulationPanel;

    enum PanelTypes {
        mainEnvironment, orbitSimulation, landSimulation
    }

    public MainFrame() {
        super("Space Procedural Generation");

        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        layeredPane = new JLayeredPane();
        enviroPanel = new EnviroPanel(frameWidth, frameHeight, this);
        orbitSimulationPanel = new OrbitSimulationPanel(frameWidth, frameHeight, this);
        landSimulationPanel = new PlanetLandSimulationPanel(frameWidth, frameHeight, this);

        layeredPane.add(enviroPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(orbitSimulationPanel, JLayeredPane.POPUP_LAYER);
        layeredPane.add(landSimulationPanel, JLayeredPane.POPUP_LAYER);

        addComponentListener(this);
        add(layeredPane);
    }

    public void changeVisiblePanel(PanelTypes frontPanel) {
        switch(frontPanel) {
            case mainEnvironment:
                orbitSimulationPanel.setVisible(false);
                enviroPanel.requestFocus();
                break;
            case orbitSimulation:
                landSimulationPanel.setVisible(false);
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
     * Open Planet Orbit simulation
     * @param planetToSimulate
     */
    public void openSimulation(Planet planetToSimulate) {
        changeVisiblePanel(PanelTypes.orbitSimulation);
        orbitSimulationPanel.openOrbitSimulation(planetToSimulate);
    }

    /**
     * Open Planet Land Simulation
     * @param planetSeed
     */
    public void openSimulation(int planetSeed) {
        changeVisiblePanel(PanelTypes.landSimulation);
        landSimulationPanel.openSimulation(planetSeed);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.frameHeight = getHeight();
        this.frameWidth = getWidth();
        enviroPanel.updateVisibleSpaceSize(this.frameWidth, this.frameHeight);
        orbitSimulationPanel.updateSize(this.frameWidth, this.frameHeight);
        landSimulationPanel.updateLandSize(this.frameWidth, this.frameHeight);
    }

    @Override
    public void componentMoved(ComponentEvent e) { }

    @Override
    public void componentShown(ComponentEvent e) { }

    @Override
    public void componentHidden(ComponentEvent e) { }
}