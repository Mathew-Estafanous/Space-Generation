package com.spacegeneration;


import javax.swing.JFrame;
import javax.swing.JLayeredPane;

public class SpaceFrame extends JFrame{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    int frameHeight = 800;
    int frameWidth = 900;

    JLayeredPane layeredPane;
    EnviroPanel enviroPanel;
    PlanetInfoPanel infoPanel;
    public SpaceFrame(){
        super("Space Procedural Generation");

        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        layeredPane = new JLayeredPane();
        enviroPanel = new EnviroPanel(this, frameHeight, frameWidth);
        infoPanel = new PlanetInfoPanel(frameWidth, frameHeight);
        layeredPane.add(enviroPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(infoPanel, JLayeredPane.POPUP_LAYER);
        add(layeredPane);
    }

    public PlanetInfoPanel getInfoPanel() {
        return infoPanel;
    }
}