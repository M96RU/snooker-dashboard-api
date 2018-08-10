#!/bin/bash -x
set -eo pipefail

mysql -p${DB_PW} -P3306 -h db -e "CREATE DATABASE /*!32312 IF NOT EXISTS*/ snooker /*!40100 COLLATE 'utf8mb4_unicode_ci' */;"

cd /usr/src/app
mvn spring-boot:run
