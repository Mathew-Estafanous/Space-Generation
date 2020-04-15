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

public class EnviroPanel extends JPanel implements KeyListener, MouseInputListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    SpaceFrame mainFrame;
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

        PlanetRegion initalPlanet = new PlanetRegion(createSeed(), 0, 0, enviroHeight, enviroWidth);
        spaceRegions.add(initalPlanet);
        String locationCoord = Integer.toString(initalPlanet.xLocation) + Integer.toString(initalPlanet.yLocation);
        allRegions.put(locationCoord, initalPlanet.regionSeed);

        updateCenterRegion();
        updateSurroundingRegions();
    }

    public void updateCenterRegion() {
        int xVal = Math.abs(spacePosition[0]);
        int yVal = Math.abs(spacePosition[1]);
        int xSign = (spacePosition[0] < 0) ? -1 : 1;
        int ySign = (spacePosition[1] < 0) ? -1 : 1;

        int xRegionLoc = (xVal / enviroWidth) * xSign;
        int yRegionLoc = (yVal / enviroHeight) * ySign;
        regionLocation[0] = xRegionLoc;
        regionLocation[1] = yRegionLoc;
    }

    public void updateSurroundingRegions() {
        for (PlanetRegion region : spaceRegions) {
            if (region.xLocation == regionLocation[0] && region.yLocation == regionLocation[1]) {
                centerRegion = region;
                break;
            }
        }

        spaceRegions.clear();
        for (int x = centerRegion.xLocation - 1; x <= centerRegion.xLocation + 1; x++) {
            for (int y = centerRegion.yLocation - 1; y <= centerRegion.yLocation + 1; y++) {
                String location = Integer.toString(x) + Integer.toString(y);
                if (allRegions.get(location) == null) {
                    allRegions.put(location, createSeed());
                }
                PlanetRegion region = new PlanetRegion(allRegions.get(location), x, y, enviroHeight, enviroWidth);
                spaceRegions.add(region);
            }
        }
    }

    public PlanetRegion getSelectedRegion(int mouseX, int mouseY) {
        int xRegion = Math.floorDiv(mouseX,enviroWidth);
        int yRegion = Math.floorDiv(mouseY, enviroHeight);
        for(PlanetRegion region: spaceRegions) {
            if(region.xLocation == xRegion && region.yLocation == yRegion) {
                return region;
            }
        }
        return null;
    }

    public int createSeed() {
        return (int) (Math.random() * 100000);
    }

    public int transformToScreenspace(int original, int offset) {
        return original - offset;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (PlanetRegion region : spaceRegions) {
            for(int[] starInfo: region.getStars()) {
                g.setColor(Color.white);
                int starTransformX = transformToScreenspace(starInfo[0], spacePosition[0]);
                int starTransformY = transformToScreenspace(starInfo[1], spacePosition[1]);
                g.fillArc(starTransformX, starTransformY, starInfo[2], starInfo[2], 0, 360);
            }

            for (Planet planet : region.getPlanets()) {
                Color pColour = new Color(planet.planetColour[0], planet.planetColour[1], planet.planetColour[2]);
                g.setColor(pColour);
                int xTransformed = transformToScreenspace(planet.xCoord, spacePosition[0]);
                int yTransformed = transformToScreenspace(planet.yCoord, spacePosition[1]);
                g.fillArc(xTransformed, yTransformed, planet.radius, planet.radius, 0, 360);
            }
        }

        if(planetHovering != null) {
            g.setColor(Color.CYAN);
            int xTransform = transformToScreenspace(planetHovering.xCoord, spacePosition[0]);
            int yTransform = transformToScreenspace(planetHovering.yCoord, spacePosition[1]);
            g.drawArc(xTransform - 5, yTransform - 5, planetHovering.radius + 10, planetHovering.radius + 10, 0, 360);
        }

        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString("(" + Integer.toString(spacePosition[0]) + "," + Integer.toString(spacePosition[1]) + ")", 10, 20);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        char currentKey = e.getKeyChar();
        if (currentKey == 'w') {
            spacePosition[1] -= moveSpeed;
        } else if (currentKey == 's') {
            spacePosition[1] += moveSpeed;
        } else if (currentKey == 'a') {
            spacePosition[0] -= moveSpeed;
        } else if (currentKey == 'd') {
            spacePosition[0] += moveSpeed;
        }

        updateCenterRegion();
        updateSurroundingRegions();
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
        Planet selectedPlanet = selectedRegion.findPlanetByLocation(mouseX, mouseY);
        planetHovering = selectedPlanet;
        repaint();
    }
}