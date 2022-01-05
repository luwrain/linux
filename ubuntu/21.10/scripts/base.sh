
LWRISO_ARCH=amd64
LWRISO_DATE="$(date +%Y%m%d)"
LWRISO_USER=luwrain
LWRISO_NAMESERVER=8.8.8.8
LWRISO_LANG=ru
LWRISO_BOOT_VER=21.10

export LWRISO_ROOT=/iso/chroot

chroot-run()
{
    chroot ./chroot "$@"
}

install-pkg()
{
chroot-run apt-get --yes install $@
}

remove-pkg()
{
chroot-run apt-get --yes remove $@
}

rm-pkg()
{
chroot-run apt-get --yes remove --ignore-missing --no-download $@
}


remove-pkg-prefix()
{
    remove-pkg $(apt-cache search "$1" | grep "^$1" | cut -f1 -d' ')
}

