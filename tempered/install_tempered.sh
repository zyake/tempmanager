# /bin/sh

# for details https://qiita.com/fiftystorm36/items/8fa764b703baa1a47687
# Modified for CentOS 7

# install depencencies
sudo yum install git cmake make gcc libusb-devel libfoxv systemd-devel gcc-c++

# install hidapi
git clone https://github.com/signal11/hidapi
cd hidapi/linux
sed 's/libusb-1.0/libusb/g' Makefile-manual > mod-makefile
make -f mod-makefile
sudo cp libhidapi-hidraw.so /lib64
sudo cp hid.o /lib64

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