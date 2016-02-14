
NAMESERVER=192.168.1.1
LWRISO_CUSTOMIZING=

in-chroot()
{
    chroot ./chroot "$@"
}
