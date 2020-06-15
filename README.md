# Space Simulation

**Version 2.0.0**

# **Table Of Contents**
 * [Description](#description)
 * [Installation & Opening](#installation--opening)
 * [How To Use](#how-to-use)
 * [Technologies Used](#technologies-used)
 * [Contributors](#contributors)

# Description

This is simple space simulation that generates an entire universe through the use of one universe seed.

Generated planets have their own number of moons, colour, size, etc. which can all be simulated. Orbiting moons and a simulation of the planet's land can be shown and will always remain consistent if the user would like to revisit the planet.

This project was started after I had an interested in object procedural generation. After researching about all kinds of terrain and object generation I decided to create this project to apply my new knowledge in a basic form that would help me understand the different concepts of procedural generation.

# Installation & Opening
*Note:* You should have a version of Java installed on your device that you would like to run the application on.

**Steps To Open:**
  * Go to the [Release](Release) folder
  * Download the .EXE file, in a location you can find.
  * Try running the downloaded .EXE file by running it as if it was a regular application. (Double-click)

**Steps If EXE file did not open:** (For Windows Users)
  * Download the JAR file instead.
  * Find JAR file in folders
  * Copy directory of file
  (ie. C:\Users\mathe\Documents\Space-Generation\Release)
  * Open the command prompt
  * Type in "cd" followed by the file directory and click enter.
  (ie. cd C:\Users\mathe\Documents\Space-Generation\Release)
  * Then type "java -jar (file name).jar"
  (ie. java -jar spaceGeneration-2.0-SNAPSHOT.jar)

If the steps given do not work, please contact me.

# How To Use
The application is fairly simple to use and easy to get the hang of quickly. When the application is initially loaded up, it will start with a simple main menu that has two main interactions. There is a section the user to input a universe seed (this is optional). If you do not input a seed then a random seed will be generated.

![Main Menu](https://i.imgur.com/70Mz3uW.png)

When you enter the universe simulation, you can WASD to move around the universe. All generated planets will remain exactly where they are generated, no matter how far you go. Memory usage is not high since the planets are deleted once you reach a certain distance.

![Universe Simualtion](https://i.imgur.com/qYC52r1.png)

If you want to see a simulation of the moons orbiting a certain planet, you can click on the planet of your choice and the orbit simulation will begin. You can also speed up the orbit, up to 10x, by using the slider at the top of the panel.

![Orbit Simulation](https://i.imgur.com/aE1akIM.png)

If you would also like to see a simulation of the land formation of the planet, you can also click on the planet again to be taken to the land simulation of the planet. You cannot move in the land simulation as it is only a visual representation of the planet.

![Land Simulation](https://i.imgur.com/4jrOmlS.png)

# Technologies Used
There is a wide variety of useful technologies that were available. These are just a few of the technologies that were used to make this project possible.

**List Of Technologies:**
  * Java
  * Maven Projects
  * JSON Files
  * Perlin Noise
  * Seed Generation

# Contributors

**Main Contributor:** [Mathew Estafanous](https://github.com/Mathew-Estafanous)

No other contributors at the moment.
