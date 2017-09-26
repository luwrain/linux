
LWRISO_LANG=ru
LWRISO_VERSION=1.0.1
LWRISO_ARCH=amd64
LWRISO_DATE="$(date +%Y%m%d)"
LWRISO_KERNEL=4.10.0-28-generic
LWRISO_NAMESERVER=192.168.1.1
CHANNELS_DIR=$LWRISO_ROOT/opt/luwrain/i18n/ru/org/luwrain/speech/channels/

if [ "$LWRISO_ARCH" == x86_64 ]; then
    LWRISO_ARCH=amd64
fi
