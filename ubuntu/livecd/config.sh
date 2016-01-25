
CHROOT_DIR=/mnt/chroot/iso/chroot

in-chroot()
{
    chroot ./chroot "$@"
}
