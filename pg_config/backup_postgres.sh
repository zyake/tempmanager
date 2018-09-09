#/bin/sh
BACKUP=/opt/postgres/backups/
pg_basebackup -D $BACKUP/backup -F tar  -z
tar czvf $BACKUP/backup.`date "+%TempratureLimitCheckJob"`.tar.gz backup
rm -fr $BACKUP/backup