/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.linux.term;

class PT
{
    native static private int createImpl();
    native static private int launchImpl(int fd, String cmd);
    native static private void closeImpl(int fd);
    native static private byte[] readImpl(int fd);
    native static private String errnoString();

    private int fd = -1;
    private int pid = -1;

    synchronized public void create()
    {
	fd = createImpl(); 
    }

    synchronized public void launch(String cmd)
    {
	launchImpl(fd, cmd);
    }

    synchronized public byte[] read()
    {
	return readImpl(fd);
    }

    synchronized public void close()
    {
	closeImpl(fd);
    }
}
