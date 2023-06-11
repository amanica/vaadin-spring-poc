
## Running the application

The project is a standard Gradle project. 
But it uses [**Java 17**](https://adoptium.net/en-GB/temurin/releases/).

To run it from the command line:

### Linux
first run `export JAVA_HOME="<path to jdk17>"`

then run `./gradlew bootRun`

### Windows
first `set "JAVA_HOME=<path to jdk17>"`

then `gradlew bootRun`

### Open in Browser
`http://localhost:8080`

**Hit `<Ctrl>+C` to stop the application** 

## Technology choices
### Java 17 
Latest Long Term Support version

## Main References

* [Spring starter project with vaadin](https://start.spring.io/#!type=gradle-project&language=java&platformVersion=3.1.0&packaging=jar&jvmVersion=17&groupId=com.example&artifactId=vaadin-spring-poc&name=vaadin-spring-poc&description=Vaadin%20project%20for%20Spring%20Boot&packageName=com.example.vaadinspringpoc&dependencies=vaadin,lombok,devtools,configuration-processor,data-jpa,h2,validation,actuator)
* [Vaadin tutorial](https://vaadin.com/docs/latest/tutorial)
* [MonthDay in JPA](https://stackoverflow.com/a/60699637/381083)