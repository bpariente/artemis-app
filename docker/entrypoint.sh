#!/bin/bash

export PORT0=${PORT0:-"8080"}

export JAVA_OPTS="--server.port=${PORT0}"

#java -Xmx${MICROSERVICES_HEAP_MB}m -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar /data/app.jar ${JAVA_OPTS}
java -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar /data/app.jar ${JAVA_OPTS}