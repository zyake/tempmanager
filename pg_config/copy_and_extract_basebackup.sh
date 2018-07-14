#/bin/sh

DATA_DIR="/var/lib/pgsql/10/data"
rm -fr $DATA_DIR
mkdir $DATA_DIR
cd /var/lib/pgsql/backups
TARGET_BACKUP=`ls -t *.gz|head -n 1`
cp $TARGET_BACKUP $DATA_DIR
gunzip $DATA_DIR/$TARGET_BACKUP
