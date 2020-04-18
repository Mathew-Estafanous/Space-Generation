package com.spacegeneration;

import java.util.Random;
import java.awt.Color;
public class PlanetRegion {

    int maxPlanets = 20;
    int minPlanets = 10;
    int spaceWidth;
    int spaceHeight;
    int seedRange = 100000;

    Random seed;
    public int regionSeed;
    public int totalPlanets;

    public int xLocation;
    public int yLocation;

    Planet[] planetObj;
    int[][] starLocation;
    public PlanetRegion(int seed, int xLoc, int yLoc, int height, int width) {
        this.regionSeed = seed;
        this.totalPlanets = new Random(this.regionSeed).nextInt(maxPlanets) + minPlanets;
        this.seed = new Random(this.regionSeed);
        this.xLocation = xLoc;
        this.yLocation = yLoc;
        this.spaceHeight = height;
        this.spaceWidth = width;

        createPlanets();
        createStars();
    }

    private void createPlanets() {
        planetObj = new Planet[this.totalPlanets];
        for(int i = 0; i < totalPlanets; i++) {
            int radius = seed.nextInt(40) + 10;
            int xCoordinate = seed.nextInt(spaceWidth - 2 * radius) + (xLocation * spaceWidth);
            int yCoordinate = seed.nextInt(spaceHeight - 2 * radius) + (yLocation * spaceHeight);
            Color planetColour = new Color(seed.nextInt(255), seed.nextInt(255), seed.nextInt(255));
            int planetSeed = seed.nextInt(100000);
            Planet newPlanet = new Planet(xCoordinate, yCoordinate, radius, planetColour, planetSeed);
            planetObj[i] = newPlanet;
        }
    }

    private void createStars() {
        int totalStars = seed.nextInt(900) + 100;
        starLocation = new int[totalStars][];
        for(int s = 0; s < totalStars; s++) {
            int starRadius = seed.nextInt(4) + 1;
            int starX = seed.nextInt(spaceWidth - 2 * starRadius) + (xLocation * spaceWidth);
            int starY = seed.nextInt(spaceHeight - 2 * starRadius) + (yLocation * spaceHeight);
            int[] starInfo = {starX, starY, starRadius};
            starLocation[s] = starInfo;
        }
    }

    public int[][] getStars() {
        return starLocation;
    }

    public Planet[] getPlanets() {
        return planetObj;
    }

    public Planet findPlanetByLocation(int selectX, int selectY) {
        Planet selectedPlanet = null;
        for(Planet curPlanet: planetObj) {
            double distanceFromMouse = distance(curPlanet.getXCoordinate(), curPlanet.getYCoordinate(), selectX, selectY);
            if(distanceFromMouse < curPlanet.getRadius()) {
                if(selectedPlanet == null) {
                    selectedPlanet = curPlanet;
                    continue;
                }
                double distanceToSelectedPlanet = distance(selectedPlanet.getXCoordinate(), selectedPlanet.getYCoordinate(), selectX, selectY);
                if(distanceFromMouse < distanceToSelectedPlanet) {
                    selectedPlanet = curPlanet;
                }
            }
        }
        return selectedPlanet;
    }

    public double distance(int x1, int y1, int x2, int y2) {
        double xDistance = x2 - x1;
        double yDistance = y2 - y1;
        double totalDistance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
        return totalDistance;
    }
}