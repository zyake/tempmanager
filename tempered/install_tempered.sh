# /bin/sh

# for details https://qiita.com/fiftystorm36/items/8fa764b703baa1a47687

# install hidapi
git clone https://github.com/signal11/hidapi
cd hidapi/linux
make -f Makefile-manual

cd $HOME

# install TEMPEred
git clone https://github.com/hughesr/TEMPered
cd TEMPered
git checkout hack-413d-2107
git reset --hard 75aa1e2
cmake .
make

cd $HOME

# install crontab entry
crontab -l > mycron
echo "* * * * *  sudo $HOME/TEMPered/utils/tempered > $HOME/tempered.txt; /usr/bin/wget --post-file $HOME/tempered.txt http://localhost:8080/tempmanager/record" >> mycron
crontab mycron
rm mycron