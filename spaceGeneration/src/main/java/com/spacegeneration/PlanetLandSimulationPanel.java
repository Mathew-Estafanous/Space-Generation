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

public class PlanetLandSimulationPanel extends JPanel implements KeyListener, ActionListener {
    private static final long serialVersionUID = 1L;

    SpaceFrame mainFrame;
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

    boolean isCurrentlySimulating = false;
    JButton closeSimulationBtn;

    public PlanetLandSimulationPanel(int width, int height, SpaceFrame main) {
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

    public void openLandSimulation(int seed) {
        isCurrentlySimulating = true;
        landSeed = seed;
        seedGenerator = new Random(seed);

        setVisible(true);
        createInitialPerlinNoise();
        parseJSONLayerData();
        repaint();
    }

    public void closeLandSimulation() {
        setVisible(false);
        isCurrentlySimulating = false;
        mainFrame.getOrbitSimulationPanel().setVisible(true);
    }

    public void updateLandSize(int width, int height) {
        this.landWidth = width;
        this.landHeight = height;
        setBounds(0, 0, width, height);
        createInitialPerlinNoise();
    }

    private void createInitialPerlinNoise() {
        noiseGenerator = new FastNoise(landSeed);
        noiseLand = new float[landWidth][landHeight];
        for (int x = 0; x < landWidth; x++) {
            for (int y = 0; y < landHeight; y++) {
                noiseLand[x][y] = noiseGenerator.GetPerlin(x, y);
                if(noiseLand[x][y] < minimumValue) {
                    minimumValue = noiseLand[x][y];
                } else if(noiseLand[x][y] > maximumValue) {
                    maximumValue = noiseLand[x][y];
                }
            }
        }
        perlinRange = maximumValue - minimumValue;
    }

    private void parseJSONLayerData() {
        try {
            InputStream in = this.getClass().getResourceAsStream("/landLayerData.json");
            InputStreamReader reader = new InputStreamReader(in);
            Object jsonObj = jsonParser.parse(reader);
            JSONArray landTypeList = (JSONArray) jsonObj;
            landType = seedGenerator.nextInt(landTypeList.size() - 1);
            JSONObject landTypeData = (JSONObject) landTypeList.get(landType);

            JSONObject landLayerData = (JSONObject) landTypeData.get("landTypeInformation");
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

    private double convertLayerValuesToPerlinRangeValues(double[] landLayerValues, int layer) {
        double layerValue = landLayerValues[layer];
        return minimumValue + (perlinRange * layerValue);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < landWidth; x++) {
            for (int y = 0; y < landHeight; y++) {
                for(int layer = 0; layer < landLayerValues.length;layer++) {
                    if(noiseLand[x][y] <= convertLayerValuesToPerlinRangeValues(landLayerValues, layer)) {
                        g.setColor(Color.decode(landLayerColors[layer]));
                        g.fillRect(x, y, 1, 1);
                        break;
                    }
                }
            }
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