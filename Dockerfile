FROM centos:7
MAINTAINER "Manuel RUSSO <manuelrusso@laposte.net>"

# Setting French timezone
RUN rm /etc/localtime
RUN ln -s /usr/share/zoneinfo/Europe/Paris /etc/localtime
RUN date

# Launch
COPY ./docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]

# Init DB
RUN yum install -y mysql

# Install Maven
RUN yum install -y maven
RUN mvn -version

# Init Java
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app/pom.xml
RUN mvn clean package
