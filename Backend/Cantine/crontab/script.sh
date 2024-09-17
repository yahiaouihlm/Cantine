#!/bin/bash

# Check if all required environment variables are set
if [[ -z "$DB_NAME" || -z "$DB_USER" || -z "$DB_PASSWORD" ]]; then
  echo "Error: Some environment variables (DB_NAME, DB_USER, DB_PASSWORD) are not defined."
  exit 1
fi

# Execute the stored procedure
psql -U $DB_USER -d $DB_NAME -c "CALL removeData();"

# Check the result of the command
if [ $? -eq 0 ]; then
  echo "Procedure removeData executed successfully."
else
  echo "Failed to execute the removeData procedure."
fi



#RUN THE SCRIPT IN A CRON JOB
crontab -e
0 0 1 1 *    script_of_stored_procedure.sh
# TO LIST ALL CRON JOBS FOR THE CURRENT USER
crontab -l