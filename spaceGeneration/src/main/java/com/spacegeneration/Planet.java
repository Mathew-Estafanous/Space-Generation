package com.spacegeneration;

import java.util.Random;
import java.awt.Color;
public class Planet {

    private int xCoordinate;
    private int yCoordinate;
    private int planetRadius;
    private Color planetColour;
    private int planetSeed;
    private PlanetRegion regionPlanetIsLocated;

    public Planet(int xVal, int yVal, int radius, Color colour, int seed, PlanetRegion region) {
        this.xCoordinate = xVal;
        this.yCoordinate = yVal;
        this.planetRadius = radius;
        this.planetColour = colour;
        this.planetSeed = seed;
        this.regionPlanetIsLocated = region;
    }

    public int[] getMoons(){
        Random seed = new Random(planetSeed);
        int moonValue = seed.nextInt(9);
        int totalMoons = (this.planetRadius * moonValue) / this.regionPlanetIsLocated.getMaxPlanetRadius();
        int[] moonInfo = new int[totalMoons];
        for(int i = 0; i < totalMoons; i++) {
            int radius = seed.nextInt(this.planetRadius / 2) + 5;
            moonInfo[i] = radius;
        }
        return moonInfo;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public int getRadius() {
        return planetRadius;
    }

    public Color getPlanetColour() {
        return planetColour;
    }

    public int getPlanetSeed() {
        return planetSeed;
    }
}