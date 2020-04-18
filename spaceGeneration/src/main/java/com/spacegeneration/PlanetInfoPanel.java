package com.spacegeneration;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class PlanetInfoPanel extends JPanel{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int xLocationInPercent = 0;
    int yLocationInPercent = 75;
    int infoWidthInPercent = 58;
    int infoHeightInPercent = 25;

    Planet selectedPlanet;
    public PlanetInfoPanel(int width, int height) {
        setOpaque(true);
        setBackground(Color.blue);

        int infoPanelHeight = (infoHeightInPercent* height)/100;
        int infoPanelWidth = (infoWidthInPercent * width)/100;
        setBounds(width * xLocationInPercent/100, height * yLocationInPercent/100, infoPanelWidth, infoPanelHeight);
        setVisible(false);
    }

    public void openPlanetInfo(Planet planet) {
        selectedPlanet = planet;
        setVisible(true);
        repaint();
    }

    public void closePlanetInfo() {
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(10, 10, 500, 150);

        if(selectedPlanet == null) { return; }

        g.setColor(selectedPlanet.getPlanetColour());
        g.fillArc(15, 85 - selectedPlanet.getRadius(), selectedPlanet.getRadius() * 2, selectedPlanet.getRadius() * 2, 0, 360);

        int moonXPosition = selectedPlanet.getRadius() * 3;
        int[] moonRadiusInfo = selectedPlanet.getMoons();
        g.setColor(Color.decode("#C0C0C0"));
        for(int moonRadius: moonRadiusInfo) {
            g.fillArc(moonXPosition, 85 - moonRadius, moonRadius * 2, moonRadius * 2, 0, 360);
            moonXPosition += (moonRadius * 2) + 10;
        }
    }
}