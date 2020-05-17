package com.spacegeneration;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This panel is used to simulate the land that the planet has. Uses an opensorce library called
 * FastNoise to create the perlin noise that is used to determin the different landforms that
 * will be displayed for the user.
 * A JSON file contating different land colour palettes are used to determin the land types.
 */
public class PlanetLandSimulationPanel extends JPanel implements KeyListener, ActionListener {
    private static final long serialVersionUID = 1L;

    MainFrame mainFrame;
    int landWidth;
    int landHeight;
    int landSeed;
    Random seedGenerator = new Random(0);

    float[][] noiseLand;
    FastNoise noiseGenerator;
    float minimumValue = 0;
    float maximumValue = 0;
    float perlinRange = 0;

    int landType;
    double[] landLayerValues;
    String[] landLayerColors;
    JSONParser jsonParser;
    final String FILE_DIRECTORY = "/landLayerData.json";

    JButton closeSimulationBtn;

    public PlanetLandSimulationPanel(int width, int height, MainFrame main) {
        this.landWidth = width;
        this.landHeight = height;
        this.mainFrame = main;
        jsonParser = new JSONParser();

        setOpaque(true);
        setBackground(Color.BLACK);
        setBounds(0, 0, width, height);
        setVisible(false);
        setFocusable(true);


        closeSimulationBtn = new JButton("Leave Planet");
        closeSimulationBtn.addActionListener(this);
        add(closeSimulationBtn);
        addKeyListener(this);
    }

    public void openSimulation(int seed) {
        landSeed = seed;
        seedGenerator = new Random(seed);

        createPerlinNoise();
        parseJSONLayerData();
        repaint();
    }

    public void closeLandSimulation() {
        mainFrame.changeVisiblePanel(MainFrame.PanelTypes.orbitSimulation);
        noiseLand = null;
    }

    public void updateLandSize(int width, int height) {
        this.landWidth = width;
        this.landHeight = height;
        setBounds(0, 0, width, height);
        createPerlinNoise();
    }

    private void createPerlinNoise() {
        noiseGenerator = new FastNoise(landSeed);
        noiseLand = new float[landWidth][landHeight];
        for (int x = 0; x < landWidth; x++) {
            for (int y = 0; y < landHeight; y++) {
                noiseLand[x][y] = noiseGenerator.GetPerlin(x, y);
                updateRangeValues(x, y);
            }
        }
        perlinRange = maximumValue - minimumValue;
    }

    private void updateRangeValues(int x, int y) {
        minimumValue = (noiseLand[x][y] < minimumValue)? noiseLand[x][y]:minimumValue;
        maximumValue = (noiseLand[x][y] > maximumValue)? noiseLand[x][y]:maximumValue;
    }

    /**
     * Retrieves the JSON file that is located in the resources folder and then parses
     * the information in the file depending on the chosen landType. Once the landType
     * has been chosen, the values for each layer and color are then stored in local
     * variables landLayerValues, landLayerColors.
     */
    private void parseJSONLayerData() {
        try {
            InputStream in = this.getClass().getResourceAsStream("/landLayerData.json");
            InputStreamReader reader = new InputStreamReader(in);
            Object jsonObj = jsonParser.parse(reader);
            JSONArray landTypeList = (JSONArray) jsonObj;
            landType = seedGenerator.nextInt(landTypeList.size());
            JSONObject chosenLandTypeData = (JSONObject) landTypeList.get(landType);

            JSONObject landLayerData = (JSONObject) chosenLandTypeData.get("landTypeInformation");
            ObjectMapper objectMapper = new ObjectMapper();
            landLayerValues = objectMapper.readValue(landLayerData.get("layerValues").toString(), double[].class);
            landLayerColors = objectMapper.readValue(landLayerData.get("landColors").toString(), String[].class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private double convertLayerValuesToPerlinRangeValues(double layerValue) {
        return minimumValue + (perlinRange * layerValue);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < landWidth; x++) {
            for (int y = 0; y < landHeight; y++) {
                paintLandPixle(x, y, g);
            }
        }
    }

    private void paintLandPixle(int x, int y, Graphics g) {
        for(int layer = 0; layer < landLayerValues.length;layer++) {
            if(noiseLand[x][y] > convertLayerValuesToPerlinRangeValues(landLayerValues[layer])) { continue; }

            g.setColor(Color.decode(landLayerColors[layer]));
            g.fillRect(x, y, 1, 1);
            break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       String action = e.getActionCommand();
       if(action == closeSimulationBtn.getText()) {
           closeLandSimulation();
       }
    }

    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }
}