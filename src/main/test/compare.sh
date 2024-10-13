OUTPUT_C=c_output.txt
OUTPUT_JAVA=java_output.txt
DIFF_FILE=c_java_difference.txt

DIFF_FILE_TRUTH=diff_signature.txt
DIFF_RESULTS=diff_results.txt

TARGET_DIR=../../../target
JAR_FILE=extremelogic-texttrack-0.0.1.jar

java -jar ${TARGET_DIR}/${JAR_FILE} ./cc_minimum.ts > ${OUTPUT_JAVA} 

diff ${OUTPUT_C} ${OUTPUT_JAVA} > ${DIFF_FILE}

if diff ${DIFF_FILE} ${DIFF_FILE_TRUTH} > ${DIFF_RESULTS}; then
  echo "Files are the same"
else
  echo "Files are different"
fi
