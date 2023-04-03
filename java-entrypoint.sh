#!/bin/sh

for file in $(ls /run/secrets/variables*.sh 2>/dev/null); do
  source $file
done

VAULT_CONFIG_FILE=""
if [ -f /run/secrets/config.hcl ]; then
  VAULT_CONFIG_FILE=/run/secrets/config.hcl
fi

JAVA_CMD="java -server -jar ${JAVA_TOOL_OPTIONS} app.jar"
echo $JAVA_CMD
if [ -n "$VAULT_CONFIG_FILE" ]; then
  ./envconsul -config=$VAULT_CONFIG_FILE $ENVCONSUL_PARAMS ${JAVA_CMD}
elif [ -n "$ENVCONSUL_PARAMS" ]; then
  ./envconsul $ENVCONSUL_PARAMS ${JAVA_CMD}
else
  exec ${JAVA_CMD}
fi
