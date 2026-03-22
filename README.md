### TODO
See TODO's in the code!

### Building and Running the Game

#### Console mode (AI vs AI)

    gradle run

Or with Ant:

    ant run

#### GUI mode (Human vs AI, Human vs Human, or AI vs AI)

    gradle jar-gui && java -jar lib/connect4-gui.jar

Or with Ant:

    ant run-gui

A dialog will prompt you to choose the game mode on startup.

#### Other Gradle targets

    gradle test       # run tests
    gradle clean      # remove build artifacts

#### Other Ant targets (build.xml)

    jar: Build the console JAR (lib/connect4.jar)
    jar-gui: Build the GUI JAR (lib/connect4-gui.jar)
    test: Run all JUnit tests
    clean: Remove build artifacts
