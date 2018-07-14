#/bin/sh
pg_basebackup -D backup -F tar  -z
mv backup/base.tar.gz backup/base.`date "+%a"`.tar.gz
scp backup/base.`date "+%a".tar.gz` postgres@192.168.10.211:/var/lib/pgsql/backups
rm -fr backup