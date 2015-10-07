#!/bin/bash -x 
mount none -t proc /proc
mount none -t sysfs /sys
mount none -t devpts /dev/pts
export HOME=/root
export LC_ALL=C
env
sleep 10
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 12345678  #Substitute "12345678" with the PPA's OpenPGP ID.
apt-get update
apt-get install --yes dbus
dbus-uuidgen > /var/lib/dbus/machine-id
dpkg-divert --local --rename --add /sbin/initctl

ln -s /bin/true /sbin/initctl
apt-get --yes upgrade

apt-get install --yes ubuntu-standard casper lupin-casper
apt-get install --yes discover laptop-detect os-prober
apt-get install --yes linux-generic 

apt-get install --no-install-recommends --yes network-manager

apt-get install --yes links2 mc htop xinit

apt-get install software-properties-common --yes
add-apt-repository ppa:openjdk-r/ppa --yes
apt-get update 
apt-get install openjdk-8-jdk --yes

apt-get install kernel-package scons git --yes
apt-get install libao4 libao-dev gcj-4.8-jdk --yes

apt-get install linux-sound-base alsa-base alsa-utils libasound2 --yes
apt-get install libflite1 pkg-config --yes


cd /tmp/
git clone https://github.com/Olga-Yakovleva/RHVoice.git
cd RHVoice
scons
scons install
ldconfig

cd /tmp/
wget http://marigostra.ru/download/voiceman-1.5.1.tar.gz
tar -xzvf voiceman-1.5.1.tar.gz
cd voiceman-1.5.1
./configure
make 
make install


#cd /tmp/
#wget http://download.luwrain.org/nightly/2015-09-29/luwrain-linux-nightly-2015-09-29.tar
#tar -xvf luwrain-linux-nightly-2015-09-29.tar -C /root/
#mv /root/luwrain*/ /root/luwrain/
#cd /root/luwrain/
#./setup.sh

