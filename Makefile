# Define variables for source, class directories, and output jar
SRC_DIR = src
CLASS_DIR = classes
OUTPUT_JAR = chinese-chess.jar

# Default target (compiles all Java files)
all:
	cd $(SRC_DIR)/ && javac -d ../$(CLASS_DIR) CCInterface.java && ln -sf ../$(SRC_DIR)/images ../$(CLASS_DIR)
	cd $(CLASS_DIR)/ && jar cfm ../$(OUTPUT_JAR) ../manifest.txt *.class images

# Rule to run the program (assuming Main.java is your main class)
run: all
	java -jar $(OUTPUT_JAR)

# Clean up generated class files
clean:
	rm -rf $(CLASS_DIR)/ $(OUTPUT_JAR)
