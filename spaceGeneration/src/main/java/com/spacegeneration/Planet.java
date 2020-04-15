package com.spacegeneration;

import java.util.Random;
public class Planet {

    public int xCoord;
    public int yCoord;
    public int radius;
    public int[] planetColour;
    public int planetSeed;

    public Planet(int xVal, int yVal, int radius, int[] colour, int seed) {
        this.xCoord = xVal;
        this.yCoord = yVal;
        this.radius = radius;
        this.planetColour = colour;
        this.planetSeed = seed;
    }

    public int[] getMoons(){
        Random seed = new Random(planetSeed);
        int val = seed.nextInt(10);
        int totalMoons = (this.radius * val) / 50;
        int[] moonInfo = new int[totalMoons];
        for(int i = 0; i < totalMoons; i++) {
            int radius = seed.nextInt(this.radius / 2) + 5;
            moonInfo[i] = radius;
        }
        return moonInfo;
    }
}