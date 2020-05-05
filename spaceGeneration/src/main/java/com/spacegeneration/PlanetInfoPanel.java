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
    int yLocationInPercent = 70;
    int infoWidthInPercent = 65;
    int infoHeightInPercent = 25;
    int infoPanelXLocation;
    int infoPanelYLocation;
    int infoPanelWidth;
    int infoPanelHeight;

    int backgroundWidthInPercent = 95;
    int backgroundHeightInPercent = 95;
    int backgroundXLocation;
    int backgroundYLocation;
    int backgroundWidth;
    int backgroundHeight;

    double planetDrawingOffset = 1.2;
    int planetMaginifation = 2;
    Planet selectedPlanet;

    public PlanetInfoPanel(int width, int height) {
        setOpaque(true);
        setBackground(Color.blue);

        infoPanelHeight = multiplyValueByPercent(height, infoHeightInPercent);
        infoPanelWidth = multiplyValueByPercent(width, infoWidthInPercent);
        infoPanelXLocation = multiplyValueByPercent(width, xLocationInPercent);
        infoPanelYLocation = multiplyValueByPercent(height, yLocationInPercent);
        setBounds(infoPanelXLocation, infoPanelYLocation, infoPanelWidth, infoPanelHeight);
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

        drawSpaceBackground(g);
        if(selectedPlanet == null) { return; }

        drawPlanet(g);
        drawMoons(g);
    }

    private void drawSpaceBackground(Graphics g) {
        backgroundHeight = multiplyValueByPercent(infoPanelHeight, backgroundHeightInPercent);
        backgroundWidth = multiplyValueByPercent(infoPanelWidth, backgroundWidthInPercent);
        backgroundYLocation = (infoPanelHeight - backgroundHeight)/2;
        backgroundXLocation = (infoPanelWidth - backgroundWidth)/2;
        g.setColor(Color.black);
        g.fillRect(backgroundXLocation, backgroundYLocation, backgroundWidth, backgroundHeight);
    }

    private void drawPlanet(Graphics g) {
        g.setColor(selectedPlanet.getPlanetColour());
        int planetSize = selectedPlanet.getRadius() * planetMaginifation;
        int planetXPosition = (int)(backgroundXLocation * planetDrawingOffset);
        int planetYPosition = (backgroundHeight/2) + backgroundYLocation - selectedPlanet.getRadius();
        g.fillArc(planetXPosition, planetYPosition, planetSize, planetSize, 0, 360);
    }

    private void drawMoons(Graphics g) {
        int moonXPosition = (int)(backgroundXLocation * planetDrawingOffset) + selectedPlanet.getRadius() * 3;
        int[] moonRadiusInfo = selectedPlanet.getMoons();
        g.setColor(Color.decode("#C0C0C0"));
        for(int moonRadius: moonRadiusInfo) {
            int moonSize = moonRadius * planetMaginifation;
            int moonYPosition = (backgroundHeight/2) + backgroundYLocation - moonRadius;
            g.fillArc(moonXPosition, moonYPosition, moonSize, moonSize, 0, 360);
            moonXPosition += (moonRadius * 2) + 10;
        }
    }

    private int multiplyValueByPercent(int original, int percent) {
        return (original * percent) / 100;
    }
}