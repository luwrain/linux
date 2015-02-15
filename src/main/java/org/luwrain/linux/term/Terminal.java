/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

import java.util.*;

public class Terminal
{
    private static native int openPty();
    private static native int exec(int pty, String cmd);
    private static native void close(int fd);
    private static native String errnoString();
    private static native String collect(int pty);

    private int fd = -1;
    private int pid = -1;
    private Vector<String> lines = new Vector<String>();

    public Terminal()
    {
    }

    public synchronized void open(String shellExp) throws TerminalException
    {
	if (shellExp == null || shellExp.trim().isEmpty())
	    return;
	fd = openPty(); 
	if (fd < 0)
	    throw new TerminalException("open:" + errnoString());
	pid = exec(fd, shellExp);
	if (pid < 0)
	{
	    final String message = errnoString();
	    close(fd);
	    fd = -1;
	    pid = -1;
	    throw new TerminalException(message);
	}
    }

    public synchronized int getLineCount()
    {
	return lines.size();
    }

    public synchronized String getLine(int index)
    {
	return index < lines.size()?lines.get(index):"";
    }

    public synchronized void close()
    {
    }

    public synchronized boolean isOpened()
    {
	return fd >= 0 && pid >= 0;
    }


    public synchronized boolean collectData()
    {
	String line = collect(fd);
	if (line != null && !line.isEmpty())
	    lines.add(line);
	return true;//FIXME:
    }
}

