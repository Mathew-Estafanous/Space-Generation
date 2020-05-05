package com.spacegeneration;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class EnviroPanel extends JPanel implements KeyListener, MouseInputListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    SpaceFrame mainFrame;
    Random universeSeedGenerator;
    int enviroHeight;
    int enviroWidth;

    int moveSpeed = 20;
    int[] spacePosition = { 0, 0 }; // (x,y)
    int[] regionLocation = { 0, 0 }; // (x,y)
    int maxPlanets = 20;
    int minPlanets = 10;

    Hashtable<String, Integer> allRegions = new Hashtable<String, Integer>();
    List<PlanetRegion> spaceRegions = new ArrayList<PlanetRegion>();
    PlanetRegion centerRegion = null;
    Planet planetHovering;

    public EnviroPanel(SpaceFrame mainFrame, int height, int width) {
        setOpaque(true);
        setBackground(Color.BLACK);
        setBounds(0, 0, width, height);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);

        this.mainFrame = mainFrame;
        this.enviroHeight = height;
        this.enviroWidth = width;

        PlanetRegion initialRegion = new PlanetRegion(createSeed(), 0, 0, enviroHeight, enviroWidth);
        spaceRegions.add(initialRegion);
        String locationCoord = putIntegersTogetherAsString(initialRegion.getXRegion(), initialRegion.getYRegion());
        allRegions.put(locationCoord, initialRegion.getRegionSeed());

        updateCenterRegion();
        updateRegionsToLoad();
    }

    public void updateVisibleSpaceSize(int width, int height) {
        setBounds(0, 0, width, height);
        repaint();
    }

    private void updateCenterRegion() {
        int xVal = Math.abs(spacePosition[0]);
        int yVal = Math.abs(spacePosition[1]);
        int xSign = (spacePosition[0] < 0) ? -1 : 1;
        int ySign = (spacePosition[1] < 0) ? -1 : 1;

        int xRegionLoc = (xVal / enviroWidth) * xSign;
        int yRegionLoc = (yVal / enviroHeight) * ySign;
        regionLocation[0] = xRegionLoc;
        regionLocation[1] = yRegionLoc;
    }

    private void updateRegionsToLoad() {
        spaceRegions.clear();
        int screenWidth = mainFrame.getWidth();
        int screenHeight = mainFrame.getHeight();

        int startXRegion = Math.floorDiv(spacePosition[0], enviroWidth);
        int startYRegion = Math.floorDiv(spacePosition[1], enviroHeight);
        int endXRegion = Math.floorDiv(spacePosition[0] + screenWidth, enviroWidth);
        int endYRegion = Math.floorDiv(spacePosition[1] + screenHeight, enviroHeight);
        for(int xRegion = startXRegion; xRegion <= endXRegion; xRegion++) {
            for(int yRegion = startYRegion; yRegion <= endYRegion; yRegion++) {
                String regionLocation = putIntegersTogetherAsString(xRegion, yRegion);
                if(allRegions.get(regionLocation) == null) {
                    allRegions.put(regionLocation, createSeed());
                }
                PlanetRegion regionToAdd = new PlanetRegion(allRegions.get(regionLocation), xRegion, yRegion, enviroHeight, enviroWidth);
                spaceRegions.add(regionToAdd);
            }
        }
    }

    private PlanetRegion getSelectedRegion(int mouseX, int mouseY) {
        int xRegion = Math.floorDiv(mouseX, enviroWidth);
        int yRegion = Math.floorDiv(mouseY, enviroHeight);
        for(PlanetRegion region: spaceRegions) {
            if(region.getXRegion() == xRegion && region.getYRegion() == yRegion) {
                return region;
            }
        }
        return null;
    }

    private int createSeed() {
        return (int) (Math.random() * 1000000);
    }

    private int transformToScreenspace(int original, int offset) {
        return original - offset;
    }

    private String putIntegersTogetherAsString(int a, int b) {
        return Integer.toString(a) + Integer.toString(b);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (PlanetRegion region : spaceRegions) {
            drawStarsInRegion(region, g);
            drawPlanetsInRegion(region, g);
        }

        if(planetHovering != null) {
            g.setColor(Color.CYAN);
            int xTransform = transformToScreenspace(planetHovering.getXCoordinate(), spacePosition[0]);
            int yTransform = transformToScreenspace(planetHovering.getYCoordinate(), spacePosition[1]);
            g.drawArc(xTransform - 5, yTransform - 5, (planetHovering.getRadius() * 2) + 10, (planetHovering.getRadius() * 2) + 10, 0, 360);
        }

        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString("(" + Integer.toString(spacePosition[0]) + "," + Integer.toString(spacePosition[1]) + ")", 10, 20);
    }

    private void drawStarsInRegion(PlanetRegion region, Graphics g) {
        for(int[] starInfo: region.getStars()) {
            g.setColor(Color.white);
            int starTransformX = transformToScreenspace(starInfo[0], spacePosition[0]);
            int starTransformY = transformToScreenspace(starInfo[1], spacePosition[1]);
            g.fillArc(starTransformX, starTransformY, starInfo[2], starInfo[2], 0, 360);
        }
    }



    private void drawPlanetsInRegion(PlanetRegion region, Graphics g) {
        for (Planet planet : region.getListOfPlanets()) {
            Color pColour = planet.getPlanetColour();
            g.setColor(pColour);
            int xTransformed = transformToScreenspace(planet.getXCoordinate(), spacePosition[0]);
            int yTransformed = transformToScreenspace(planet.getYCoordinate(), spacePosition[1]);
            g.fillArc(xTransformed, yTransformed, planet.getRadius() * 2, planet.getRadius() * 2, 0, 360);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        char currentKey = e.getKeyChar();
        switch(currentKey) {
            case 'w':
                spacePosition[1] -= moveSpeed;
                break;
            case 's':
                spacePosition[1] += moveSpeed;
                break;
            case 'a':
                spacePosition[0] -= moveSpeed;
                break;
            case 'd':
                spacePosition[0] += moveSpeed;
                break;
        }

        updateCenterRegion();
        updateRegionsToLoad();
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)) {
            PlanetInfoPanel infoPanel = mainFrame.getInfoPanel();
            if(planetHovering == null) {
                infoPanel.closePlanetInfo();
            } else {
                infoPanel.openPlanetInfo(planetHovering);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = spacePosition[0] + e.getX();
        int mouseY = spacePosition[1] + e.getY();
        PlanetRegion selectedRegion = getSelectedRegion(mouseX, mouseY);
        Planet selectedPlanet = selectedRegion.findPlanetByLocation(mouseX, mouseY, 0,  selectedRegion.listOfPlanetObject.size() - 1);
        planetHovering = selectedPlanet;
        repaint();
    }
}