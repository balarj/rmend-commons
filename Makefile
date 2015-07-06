unit-test:
	@echo "---------------------------- Running Unit-tests ---------------------------"
	mvn clean test
integration-test:
	@echo "---------------------------- Running Integration-tests ---------------------------"
	@ping -c 1 $(SERVER)
	mvn -Dtest.integration.server=$(SERVER) clean verify
package:
	mvn -Dci.version=$(VERSION) -Dci.buildid=$(BUILD_ID) -Dmaven.test.skip=true clean package
deploy:
	mvn -Dci.version=$(VERSION) -Dci.buildid=$(BUILD_ID) -Dmaven.test.skip=true clean deploy
install:
	mvn -Dci.version=$(VERSION) -Dci.buildid=$(BUILD_ID) -Dmaven.test.skip=true clean install
clean:
	mvn clean
compile:
	mvn clean compile
