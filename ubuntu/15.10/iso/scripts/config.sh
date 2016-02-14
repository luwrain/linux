
NAMESERVER=192.168.1.1
export LWRISO_CUSTOMIZING=

in-chroot()
{
    chroot ./chroot "$@"
}
