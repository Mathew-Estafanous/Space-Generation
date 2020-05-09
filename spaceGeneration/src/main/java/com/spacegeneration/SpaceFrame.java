package com.spacegeneration;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

public class SpaceFrame extends JFrame implements ComponentListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    int frameHeight = 800;
    int frameWidth = 900;

    JLayeredPane layeredPane;
    EnviroPanel enviroPanel;
    PlanetInfoPanel infoPanel;
    OrbitSimulationPanel orbitSimulationPanel;
    PlanetLandSimulationPanel landSimulationPanel;

    public SpaceFrame() {
        super("Space Procedural Generation");

        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        layeredPane = new JLayeredPane();
        enviroPanel = new EnviroPanel(this, frameHeight, frameWidth);
        infoPanel = new PlanetInfoPanel(frameWidth, frameHeight);
        orbitSimulationPanel = new OrbitSimulationPanel(frameWidth, frameHeight, this);
        landSimulationPanel = new PlanetLandSimulationPanel(frameWidth, frameHeight, this);
        layeredPane.add(enviroPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(infoPanel, JLayeredPane.POPUP_LAYER);
        layeredPane.add(orbitSimulationPanel, JLayeredPane.POPUP_LAYER);
        layeredPane.add(landSimulationPanel, JLayeredPane.POPUP_LAYER);

        addComponentListener(this);
        add(layeredPane);
    }

    public PlanetInfoPanel getInfoPanel() {
        return infoPanel;
    }

    public EnviroPanel getEnviroPanel() {
        return enviroPanel;
    }

    public OrbitSimulationPanel getOrbitSimulationPanel() {
        return orbitSimulationPanel;
    }

    public PlanetLandSimulationPanel getLandSimulation() {
        return landSimulationPanel;
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