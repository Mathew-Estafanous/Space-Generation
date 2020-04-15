package com.spacegeneration;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class PlanetInfoPanel extends JPanel{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int xLocation = 0; // %
    int yLocation = 75; //%
    int infoWidth = 100; //%
    int infoHeight = 25; //%
    int infoPanelWidth;
    int infoPanelHeight;

    int frameWidth;
    int frameHeight;

    Planet selectedPlanet;

    public PlanetInfoPanel(int width, int height) {
        setOpaque(true);
        setBackground(Color.blue);

        this.frameHeight = height;
        this.frameWidth = width;
        this.infoPanelHeight = (infoHeight* frameHeight)/100;
        this.infoPanelWidth = (infoWidth * frameWidth)/100;
        setBounds(frameWidth * xLocation/100, frameHeight * yLocation/100, this.infoPanelWidth, this.infoPanelHeight);
        System.out.println(getBounds());
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
        // TODO Auto-generated method stub
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(10, 10, 500, 150);

        if(selectedPlanet == null) { return; }

        g.setColor(new Color(selectedPlanet.planetColour[0], selectedPlanet.planetColour[1], selectedPlanet.planetColour[2]));
        g.fillArc(15, 85 - selectedPlanet.radius, selectedPlanet.radius * 2, selectedPlanet.radius * 2, 0, 360);

        int moonXPos = selectedPlanet.radius * 3;
        int[] moonInfo = selectedPlanet.getMoons();
        g.setColor(Color.decode("#C0C0C0"));
        for(int moonRadius: moonInfo) {
            g.fillArc(moonXPos, 85 - moonRadius, moonRadius * 2, moonRadius * 2, 0, 360);
            moonXPos += (moonRadius * 2) + 10;
        }
    }
}