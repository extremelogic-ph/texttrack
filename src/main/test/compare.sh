#!/bin/bash

VIDEO_FILE="./cc_minimum.ts"

if [ ! -f "${VIDEO_FILE}" ]; then
    echo "Video file '${VIDEO_FILE}' does not exist. Not proceeding to test. "
    exit 1
fi

OUTPUT_C=c_output.txt
OUTPUT_JAVA=java_output.txt
DIFF_FILE=c_java_difference.txt

DIFF_FILE_TRUTH=diff_signature.txt
DIFF_RESULTS=diff_results.txt

TARGET_DIR=../../../target
JAR_FILE=extremelogic-texttrack-0.0.2-SNAPSHOT.jar

printf "Running jar %s" "${JAR_FILE}. "

java -jar ${TARGET_DIR}/${JAR_FILE} ./cc_minimum.ts > ${OUTPUT_JAVA} 

diff ${OUTPUT_C} ${OUTPUT_JAVA} > ${DIFF_FILE}

if diff ${DIFF_FILE} ${DIFF_FILE_TRUTH} > ${DIFF_RESULTS}; then
  printf "Comparison [OK]\n"
else
  printf "Comparison [NG]\n"
fi
