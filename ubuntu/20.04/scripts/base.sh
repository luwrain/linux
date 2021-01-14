# Copyright 2021 Michael Pozhidaev <msp@luwrain.org>
# The LUWRAIN Project, licensed under the terms of GPL v.3

export LWRISO_ROOT=/iso/chroot
export LWRISO_USER=luwrain

in-chroot()
{
    chroot ./chroot "$@"
}

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

remove-pkg-prefix()
{
    remove-pkg $(apt-cache search "$1" | grep "^$1" | cut -f1 -d' ')
}
