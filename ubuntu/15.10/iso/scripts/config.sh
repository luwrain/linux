
NAMESERVER=192.168.1.1
LWRISO_CUSTOMIZING=homeros

in-chroot()
{
    chroot ./chroot "$@"
}
