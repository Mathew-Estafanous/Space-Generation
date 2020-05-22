package com.spacegeneration;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class PlanetRegionTest {

  @Parameterized.Parameter(0)
  public int seed;

  // creates the test data
  @Parameterized.Parameters(name = "{index}: Test with seed1={0}")
  public static Collection<Object[]> data() {
      Object[][] data = new Object[][] {{123}, {124}, {453}, {87345}, {1454}, {23515}, {346234}};
      return Arrays.asList(data);
  }

  @Test
  public void testSortedPlanetList() {
      PlanetRegion region = new PlanetRegion(seed, 0, 0, 800, 800);
      assertThat(region.getListOfPlanets(), xCoordinatesInIncreasingOrder());
  }

  private Matcher<? super List<Planet>> xCoordinatesInIncreasingOrder() {
    return new TypeSafeMatcher<List<Planet>>() {
      @Override
      public void describeTo (Description description) {
        description.appendText("X Coordinates are not in increasing order.");
      }

      @Override
      protected boolean matchesSafely (List<Planet> item) {
        for(int i = 0 ; i < item.size() - 1; i++) {
          if(item.get(i).getXCoordinate() > item.get(i + 1).getXCoordinate()) {
              return false;
          }
        }
        return true;
      }
    };
  }


  @Test
  public void testPlanetsAreInsideRegion() {
      PlanetRegion region = new PlanetRegion(seed, 0, 0, 800, 800);
      assertThat(region, planetsAreInsideTheirRegion());
    }

  private Matcher<? super PlanetRegion> planetsAreInsideTheirRegion() {
    return new TypeSafeMatcher <PlanetRegion>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Some Planets are not inside their own region.");
      }

      @Override
      protected boolean matchesSafely(PlanetRegion planetRegions) {
        int width = planetRegions.getWidth();
        int height = planetRegions.getHeight();
        for(Planet planet: planetRegions.getListOfPlanets()) {
          if(planet.getXCoordinate() + planet.getRadius() > width || planet.getXCoordinate() < 0) {
            return false;
          } else if(planet.getYCoordinate() + planet.getRadius() > height || planet.getYCoordinate() < 0) {
            return false;
          }
        }
        return true;
      }
    };
  }
}
