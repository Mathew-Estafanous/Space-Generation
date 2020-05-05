package com.spacegeneration;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test.
     */
    @Test
    public void testApp() {
        assertTrue(true);
    }

    @Test
    public void testSortedPlanetList() {
        PlanetRegion region = new PlanetRegion(4324, 0, 0, 800, 800);
        assertThat(region.listOfPlanetObject, xCoordinatesInIncreasingOrder());
    }

    private Matcher<? super List<Planet>> xCoordinatesInIncreasingOrder()
    {
    return new TypeSafeMatcher<List<Planet>>()
    {
      @Override
      public void describeTo (Description description)
      {
        description.appendText("X Coordinates are not in increasing order.");
      }

      @Override
      protected boolean matchesSafely (List<Planet> item)
      {
        for(int i = 0 ; i < item.size() - 1; i++) {
          if(item.get(i).getXCoordinate() > item.get(i + 1).getXCoordinate()) {
              return false;
          }
        }
        return true;
      }
    };
    }
}
