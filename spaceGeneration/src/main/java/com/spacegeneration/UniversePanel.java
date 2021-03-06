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

/**
 * Main space environment which generates, draws and allows users to interact
 * with planet regions and the planets that are inside it. Users are also able
 * to select planets and begin simulations of the planet.
 */
public class UniversePanel extends JPanel implements KeyListener, MouseInputListener {


    private static final long serialVersionUID = 1L;
    private MainFrame mainFrame;
    private int environmentHeight;
    private int environmentWidth;

    private int regionHeight;
    private int regionWidth;
    private int moveSpeed = 20;
    private int[] spacePosition = { 0, 0 };  //(x, y)

    private Hashtable<String, Integer> allSpaceRegions = new Hashtable<String, Integer>();
    private List<PlanetRegion> spaceRegionsToLoad = new ArrayList<PlanetRegion>();

    private Planet planetHovering;
    private int universeSeed;

    public UniversePanel(int width, int height, MainFrame mainPanel) {
        setOpaque(true);
        setBackground(Color.BLACK);
        setBounds(0, 0, width, height);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);

        mainFrame = mainPanel;
        environmentHeight = height;
        environmentWidth = width;
        regionHeight = height;
        regionWidth = width;
    }

    public void startUniverseSimulation(int seedForUniverse) {
        universeSeed = seedForUniverse;
        updateListOfRegionsToLoad();
    }

    public void updateSize(int width, int height) {
        setBounds(0, 0, width, height);
        environmentHeight = height;
        environmentWidth = width;
        repaint();
    }

    /**
     * Uses the spacePosition, environment size and the region sizes to
     * calculate the regions that are visible to the user. Regions that are
     * visible to the users will be added to the {spaceRegionsToLoad}.
     * <p>
     * New regions that have not been viewed before, will be added to the allSpaceRegions
     * hashtable for future reference.
     */
    private void updateListOfRegionsToLoad() {
        spaceRegionsToLoad.clear();

        int startXRegion = calculateRegionIndexFromCoordinateAndSize(spacePosition[0], regionWidth);
        int startYRegion = calculateRegionIndexFromCoordinateAndSize(spacePosition[1], regionHeight);
        int endXRegion = calculateRegionIndexFromCoordinateAndSize(spacePosition[0] + environmentWidth, regionWidth);
        int endYRegion = calculateRegionIndexFromCoordinateAndSize(spacePosition[1] + environmentHeight, regionHeight);
        for(int xRegion = startXRegion; xRegion <= endXRegion; xRegion++) {
            for(int yRegion = startYRegion; yRegion <= endYRegion; yRegion++) {
                String regionKeyIndex = createRegionKeyIndex(xRegion, yRegion);
                if(allSpaceRegions.get(regionKeyIndex) == null) {
                    allSpaceRegions.put(regionKeyIndex, createSeed(regionKeyIndex));
                }
                PlanetRegion regionToAdd = new PlanetRegion(allSpaceRegions.get(regionKeyIndex), xRegion, yRegion, regionHeight, regionWidth);
                spaceRegionsToLoad.add(regionToAdd);
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (PlanetRegion region : spaceRegionsToLoad) {
            drawStarsInRegion(region, g);
            drawPlanetsInRegion(region, g);
        }

        if(planetHovering != null) {
           drawHoveringOutline(g);
        }

        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString("(" + Integer.toString(spacePosition[0]) + "," + Integer.toString(spacePosition[1]) + ")", 10, 20);
    }

    private void drawHoveringOutline(Graphics g) {
        g.setColor(Color.CYAN);
        int outlineSize = (int) Math.round((planetHovering.getRadius() * 2) * 1.2);
        int offSetTransform = (outlineSize - planetHovering.getRadius() * 2)/2;
        int xTransform = transformToScreenspace(planetHovering.getXCoordinate(), spacePosition[0]) - offSetTransform;
        int yTransform = transformToScreenspace(planetHovering.getYCoordinate(), spacePosition[1]) - offSetTransform;
        g.drawArc(xTransform, yTransform, outlineSize, outlineSize, 0, 360);
    }

    private void drawStarsInRegion(PlanetRegion region, Graphics g) {
        for(int[] starInfo: region.getStars()) {
            g.setColor(Color.white);
            int starLocationX = transformToScreenspace(starInfo[0], spacePosition[0]);
            int starLocationY = transformToScreenspace(starInfo[1], spacePosition[1]);
            g.fillArc(starLocationX, starLocationY, starInfo[2], starInfo[2], 0, 360);
        }
    }

    private void drawPlanetsInRegion(PlanetRegion region, Graphics g) {
        for (Planet planet : region.getListOfPlanets()) {
            Color planetColor = planet.getPlanetColour();
            g.setColor(planetColor);
            int xTransformed = transformToScreenspace(planet.getXCoordinate(), spacePosition[0]);
            int yTransformed = transformToScreenspace(planet.getYCoordinate(), spacePosition[1]);
            g.fillArc(xTransformed, yTransformed, planet.getRadius() * 2, planet.getRadius() * 2, 0, 360);
        }
    }

    /**
     * Takes a certain coordinate and a size (width or length), then divides the
     * coordinate by the size and floors it to an integer value. This will get an
     * index value relating to the region that coordinate is in.
     *
     * @param coordinate
     * @param size
     * @return {int} Region Index
     */
    private int calculateRegionIndexFromCoordinateAndSize(int coordinate, int size) {
        return Math.floorDiv(coordinate, size);
    }

    /**
     * takes the region key and returns a specific region seed according to the
     * region key's hashcode and the universe seed.
     *
     * @param regionKey
     * @return {int} Region Seed
     */
    private int createSeed(String regionKey) {
        int regionHashCode = regionKey.hashCode();
        Random seedOfUniverse = new Random(universeSeed);
        return seedOfUniverse.nextInt(regionHashCode);
    }

    /**
     * Takes original coordinate and offset it according to the world position.
     * Accounting for the current location that the user is in.
     *
     * @param original
     * @param offset
     * @return {int} Transformed Coordinate
     */
    private int transformToScreenspace(int original, int offset) {
        return original - offset;
    }

    
    /**
     * Combines both regionX and regionY together and returns it as a
     * region key for the allSpaceRegions HashTable.
     *
     * @param regionX
     * @param regionY
     * @return {String}
     */
    private String createRegionKeyIndex(int regionX, int regionY) {
        return Integer.toString(regionX) + Integer.toString(regionY);
    }

    private PlanetRegion findPlanetRegionFromCoordinates(int mouseX, int mouseY) {
        int xRegion = calculateRegionIndexFromCoordinateAndSize(mouseX, regionWidth);
        int yRegion = calculateRegionIndexFromCoordinateAndSize(mouseY, regionHeight);
        for(PlanetRegion region: spaceRegionsToLoad) {
            if(region.getXRegion() == xRegion && region.getYRegion() == yRegion) {
                return region;
            }
        }
        return null;
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

        updateListOfRegionsToLoad();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!SwingUtilities.isLeftMouseButton(e)) { return; }

        if(planetHovering != null) {
            mainFrame.openOrbitSimulation(planetHovering);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = spacePosition[0] + e.getX();
        int mouseY = spacePosition[1] + e.getY();
        PlanetRegion selectedRegion = findPlanetRegionFromCoordinates(mouseX, mouseY);
        Planet selectedPlanet = selectedRegion.findPlanetByLocation(mouseX, mouseY);
        planetHovering = selectedPlanet;
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) { }
}