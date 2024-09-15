DB_USER="postgres"
DB_PASSWORD="halim"
DB_NAME="cantine"
PROCEDURE="removeData()"

export PGPASSWORD=$DB_PASSWORD
psql -U $DB_USER -d $DB_NAME -c "CALL $PROCEDURE();"
