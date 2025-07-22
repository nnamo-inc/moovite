.PHONY: compile run build-and-run clean

compile:
	mvn clean package

run:
	java -jar target/moovite-1.0-SNAPSHOT.jar

br: compile run # Build and Run

clean:
	mvn clean

help:
	@echo "Available targets:"
	@echo "  compile      - Build the project using Maven"
	@echo "  run          - Run the compiled JAR"
	@echo "  build-and-run - Compile and run in sequence"
	@echo "  clean        - Clean Maven build artifacts"
	@echo "  help         - Show this help message"
