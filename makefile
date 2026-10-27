up:
	docker-compose up --build

down:
	docker-compose down

run:
	.\mvnw.cmd spring-boot:run
test:
	.\mvnw.cmd clean test
	
.PHONY: \
  up \
  down