#!/bin/bash -x
rm /var/lib/dbus/machine-id
rm /sbin/initctl
dpkg-divert --rename --remove /sbin/initctl

apt-get clean

rm -rf /tmp/*

rm /etc/resolv.conf
echo "nameserver 8.8.8.8" > /etc/resolv.conf

echo 'pcm.!default {
	type plug
	slave {
		pcm "hw:1,0"
	}
}
ctl.!default {
	type hw
	card 1
}' > /etc/asound.conf

umount -lf /proc
umount -lf /sys
umount -lf /dev/pts
exit