.PHONY: compile-run

compile-run: 
	./mvnw clean package
	java -jar target/oauth-0.0.1-SNAPSHOT.jar