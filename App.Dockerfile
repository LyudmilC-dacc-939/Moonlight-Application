# Official image with JDK 20
FROM eclipse-temurin:20-jdk AS builder

# Set the working directory inside the container
WORKDIR /app

#Install Maven
RUN apt-get update && \
	apt-get install -y maven

# Copy the pom.xml file from the project root into the /app directory
COPY pom.xml ./

# Copy the src directory into /app/src
COPY src ./src

# Download project dependencies
RUN mvn dependency:go-offline

# Run the Maven package command to build and install the project artifact
RUN mvn clean package

# Debugging: list contents of target directory
RUN ls -la /app/target

# Set the working directory for the runtime
WORKDIR /app

# Copy the built JAR file from the build stage and rename it as Moonlight-System.jar
RUN cp /app/target/bootcamp-java-24.jar /app/Moonlight-System.jar

# Expose the port the app will run on
EXPOSE 8085

#add the -d commands after run if you want to run the container in the background
#RUN -p 8085:8080 --name moonlight-app bootcamp-java-24-july_app

# Run the application
ENTRYPOINT ["java", "-jar", "/app/Moonlight-System.jar"]
