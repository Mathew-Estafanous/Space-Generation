package com.spacegeneration;

import javax.swing.SwingUtilities;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }
    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SpaceFrame();
            }
        });
    }
}
