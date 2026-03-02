FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/equity_dividend_bs_java-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
RUN ls -lh /app/
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
