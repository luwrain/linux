# Copyright 2015 Roman Volovodov <gr.rPman@gmail.com>
# Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

function msg() 
{
    echo $@ >> $LOG
    echo $@ 
}
