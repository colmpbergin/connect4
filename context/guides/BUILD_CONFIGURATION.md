# Build Configuration Summary

## Unified Output Directories

Both Ant and Gradle now use the same output directory structure:

### Compiled Classes
- **Main classes**: `build/classes/java/main`
- **Test classes**: `build/classes/java/test`
- **Instrumented classes**: `build/classes-instrumented` (for coverage)

### Source Directories
- **Main source**: `src/main`
- **Test source**: `src/test`

### Artifacts
- **JAR files**: `lib/connect4.jar`, `lib/connect4-gui.jar`
- **Test reports**: `junit/data` (XML), `junit/html` (HTML)
- **Coverage reports**: `junit/coverage`

## Build Commands

### Gradle
```bash
./gradlew clean          # Clean build outputs
./gradlew compile        # Compile main classes
./gradlew test           # Run tests
./gradlew coverage       # Run tests with coverage
./gradlew jar            # Build console JAR
./gradlew jarGui         # Build GUI JAR
./gradlew run            # Run console version
./gradlew runGui         # Run GUI version
```

### Ant
```bash
ant clean                # Clean build outputs
ant compile              # Compile main and test classes
ant test                 # Run tests
ant coverage             # Run tests with coverage
ant jar                  # Build console JAR
ant jar-gui              # Build GUI JAR
ant run                  # Run console version
ant run-gui              # Run GUI version
```

## VSCode Integration

### Project Name
- Gradle project: `connect4` (lowercase)
- Matches VSCode expectations

### Launch Configurations
- **GameController**: Runs console version
- **GraphicalGameController**: Runs GUI version
- Both use projectName: `connect4`

### Java Extension
- Automatically detects Gradle project structure
- Uses standard Gradle output directories
- No conflicts with custom paths

## Benefits of Unified Structure

1. **Consistency**: Same paths whether using Ant or Gradle
2. **VSCode Compatibility**: Standard Gradle structure works seamlessly with Java extension
3. **No Conflicts**: Single source of truth for compiled classes
4. **Easier Debugging**: Launch configurations work correctly
5. **Tool Compatibility**: Modern IDEs and tools expect standard Gradle structure

## Migration Notes

### Changed from Previous Configuration
- **Old main output**: `bin/classes` → **New**: `build/classes/java/main`
- **Old test output**: `bin/classes-test` → **New**: `build/classes/java/test`
- **Old instrumented**: `bin/classes-instrumented` → **New**: `build/classes-instrumented`
- **Project name**: `Connect4` → **New**: `connect4`

### Why These Changes
- VSCode Java extension works best with standard Gradle conventions
- Eliminates "project does not exist" errors
- Fixes package mismatch errors
- Enables proper symbol resolution and code navigation
