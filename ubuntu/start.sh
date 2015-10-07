#!/bin/bash -x
sudo umount -l work/chroot/dev
sudo rm -rf ./work
sudo apt-get install debootstrap
mkdir -p work/chroot
cd work
sudo debootstrap --arch=i386 trusty chroot http://mirror.yandex.ru/ubuntu
sudo mount --bind /dev chroot/dev
sudo cp /etc/hosts chroot/etc/hosts
sudo cp /etc/resolv.conf chroot/etc/resolv.conf
sudo cp /etc/apt/sources.list chroot/etc/apt/sources.list
sudo cp ../chroot.sh chroot/tmp/chroot.sh
sudo cp ../chroot2.sh chroot/tmp/chroot2.sh
sudo chmod +x chroot/tmp/chroot.sh
sudo chmod +x chroot/tmp/chroot2.sh
sudo chroot chroot

