
#CHROOT_DIR=/mnt/chroot/iso/chroot
NAMESERVER=192.168.1.1

in-chroot()
{
    chroot ./chroot "$@"
}
