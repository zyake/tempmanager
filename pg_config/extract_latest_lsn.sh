#/bin/sh

cd /var/lib/pgsql/archive_logs
rm *backup.gz
TARGET_FILE=`ls -t *.gz|head -n 1`
TARGET_FILE_WITHOUT_EXT=`echo $TARGET_FILE|sed 's/.gz//g'`
gunzip -c $TARGET_FILE > $TARGET_FILE_WITHOUT_EXT
LAST_LINE=`pg_waldump $TARGET_FILE_WITHOUT_EXT|tail -n 1`
LSN=`echo $LAST_LINE | grep -oP 'lsn: \K([^,]+)'`
rm $TARGET_FILE_WITHOUT_EXT

echo $LSN