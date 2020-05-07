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
    OrbitSimulationPanel simulationPanel;

    public SpaceFrame() {
        super("Space Procedural Generation");

        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        layeredPane = new JLayeredPane();
        enviroPanel = new EnviroPanel(this, frameHeight, frameWidth);
        infoPanel = new PlanetInfoPanel(frameWidth, frameHeight);
        simulationPanel = new OrbitSimulationPanel(frameWidth, frameHeight, this);

        layeredPane.add(enviroPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(infoPanel, JLayeredPane.POPUP_LAYER);
        layeredPane.add(simulationPanel, JLayeredPane.POPUP_LAYER);

        addComponentListener(this);
        add(layeredPane);
    }

    public PlanetInfoPanel getInfoPanel() {
        return infoPanel;
    }

    public OrbitSimulationPanel getOrbitSimulationPanel() {
        return simulationPanel;
    }

    public EnviroPanel getEnviroPanel() {
        return enviroPanel;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.frameHeight = getHeight();
        this.frameWidth = getWidth();
        enviroPanel.updateVisibleSpaceSize(this.frameWidth, this.frameHeight);
        simulationPanel.updateSize(this.frameWidth, this.frameHeight);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void componentShown(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // TODO Auto-generated method stub

    }
}