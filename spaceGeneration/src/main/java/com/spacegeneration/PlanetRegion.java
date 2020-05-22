package com.spacegeneration;

import java.util.Random;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

/**
 * PlanetRegion class contains most of the information about thre region, such as the
 * number of planets, the region location, stars, etc. All of the planetRegion information
 * is based off the assigned seed number.
 */
public class PlanetRegion {

    private int maxPlanets = 20;
    private int minPlanets = 10;
    private int maxPlanetRadius = 25;
    private int minPlanetRadius= 5;
    private int spaceWidth;
    private int spaceHeight;
    private int seedRange = 1000000;

    private Random seed;
    private int regionSeed;
    private int totalPlanets;

    private int xRegion;
    private int yRegion;

    private List<Planet> listOfPlanetObject = new ArrayList<Planet>();
    private int[][] starLocation;

    private enum CoordinateType {
        x, y
    }

    public PlanetRegion(int seed, int xLoc, int yLoc, int height, int width) {
        this.regionSeed = seed;
        this.seed = new Random(this.regionSeed);
        this.totalPlanets = this.seed.nextInt(maxPlanets) + minPlanets;
        this.xRegion = xLoc;
        this.yRegion = yLoc;
        this.spaceHeight = height;
        this.spaceWidth = width;

        createPlanets();
        createStars();
    }

    private void createPlanets() {
        int planetCount = 0;
        while(planetCount < totalPlanets) {
            int radius = seed.nextInt(maxPlanetRadius) + minPlanetRadius;
            int xCoordinate = seed.nextInt(spaceWidth - (2 * radius)) + (xRegion * spaceWidth);
            int yCoordinate = seed.nextInt(spaceHeight - (2 * radius)) + (yRegion * spaceHeight);
            Color planetColour = new Color(seed.nextInt(255), seed.nextInt(255), seed.nextInt(255));
            int planetSeed = seed.nextInt(seedRange);
            Planet newPlanet = new Planet(xCoordinate, yCoordinate, radius, planetColour, planetSeed, this);

            if(isPlanetOverlappingAnother(newPlanet, 0, listOfPlanetObject.size() - 1)) { continue; }

            addPlanetToSortedxCoordinateList(newPlanet);
            planetCount++;
        }
    }

    private void createStars() {
        int totalStars = seed.nextInt(900) + 100;
        starLocation = new int[totalStars][];
        for(int s = 0; s < totalStars; s++) {
            int starRadius = seed.nextInt(4) + 1;
            int starX = seed.nextInt(spaceWidth - 2 * starRadius) + (xRegion * spaceWidth);
            int starY = seed.nextInt(spaceHeight - 2 * starRadius) + (yRegion * spaceHeight);
            int[] starInfo = {starX, starY, starRadius};
            starLocation[s] = starInfo;
        }
    }

    private void addPlanetToSortedxCoordinateList(Planet planet) {
        for(int index = 0; index < listOfPlanetObject.size(); index++) {
            if(listOfPlanetObject.get(index).getXCoordinate() > planet.getXCoordinate()) {
                listOfPlanetObject.add(index, planet);
                return;
            }
        }
        listOfPlanetObject.add(planet);
    }

    /**
     * Recurrsive quicksort method that searches through the sorted list of planets
     * by starting in the middle and pivoting either left or right depending if the
     * planetToCheck's xCoordinate is larger or smaller.
     *
     * @param planetToCheck
     * @param min
     * @param max
     * @return
     */
    private boolean isPlanetOverlappingAnother(Planet planetToCheck, int min, int max) {
        if(listOfPlanetObject.size() == 0) { return false; }

        int midIndex = (min + max) / 2;
        Planet midPlanet = listOfPlanetObject.get(midIndex);
        int midPlanetX = offSetPlanetCoordinateByRadius(midPlanet, CoordinateType.x);
        int midPlanetY = offSetPlanetCoordinateByRadius(midPlanet, CoordinateType.y);
        int planetToCheckX = offSetPlanetCoordinateByRadius(planetToCheck, CoordinateType.x);
        int planetToCheckY = offSetPlanetCoordinateByRadius(planetToCheck, CoordinateType.y);
        double distanceBetweenPlanets = calculateDistanceOfTwoObjects(midPlanetX, midPlanetY, planetToCheckX, planetToCheckY);
        if(distanceBetweenPlanets < midPlanet.getRadius() * 2|| distanceBetweenPlanets < planetToCheck.getRadius() * 2) {
            return true;
        } else if(max - min == 0) {
            return false;
        } else if(planetToCheck.getXCoordinate() > midPlanet.getXCoordinate()) {
            return isPlanetOverlappingAnother(planetToCheck, midIndex + 1, max);
        } else {
            return isPlanetOverlappingAnother(planetToCheck, min, midIndex );
        }
    }

    private int offSetPlanetCoordinateByRadius(Planet planet, CoordinateType whichCoordinate) {
        int originalCoordinate = (whichCoordinate == CoordinateType.x)? planet.getXCoordinate():planet.getYCoordinate();
        return originalCoordinate + planet.getRadius();
    }

    private double calculateDistanceOfTwoObjects(int x1, int y1, int x2, int y2) {
        double xDistance = x2 - x1;
        double yDistance = y2 - y1;
        double totalDistance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
        return totalDistance;
    }

    public Planet findPlanetByLocation(int selectX, int selectY) {
        for(int j = 0; j < listOfPlanetObject.size(); j++) {
            Planet jPlanet = listOfPlanetObject.get(j);
            int planetXCoord = offSetPlanetCoordinateByRadius(jPlanet, CoordinateType.x);
            int planetyCoord = offSetPlanetCoordinateByRadius(jPlanet, CoordinateType.y);
            double distanceBetweenTwoPoints = calculateDistanceOfTwoObjects(planetXCoord, planetyCoord, selectX, selectY);
            if(distanceBetweenTwoPoints < jPlanet.getRadius()) {
                return jPlanet;
            }
            if(planetXCoord > selectX) { break; }
        }
        return null;
    }

    public int[][] getStars() {
        return starLocation;
    }

    public List<Planet> getListOfPlanets() {
        return listOfPlanetObject;
    }

    public int getMaxPlanetRadius() {
        return maxPlanetRadius;
    }

    public int getRegionSeed() {
        return regionSeed;
    }

    public int getXRegion() {
        return xRegion;
    }

    public int getYRegion() {
        return yRegion;
    }

    public int getWidth() {
        return spaceWidth;
    }

    public int getHeight() {
        return spaceHeight;
    }
}