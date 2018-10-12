#/bin/sh
pg_basebackup -D backup -F tar  -z
tar czvf backup.`date "+%a"`.tar.gz backup
scp backup.`date "+%a"`.tar.gz postgres@192.168.10.211:/var/lib/pgsql/backups
rm -fr backup.`date "+%a".tar.gz`
rm -fr backup