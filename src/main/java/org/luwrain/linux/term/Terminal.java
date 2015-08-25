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

import java.util.*;

public class Terminal
{
    private PT pt = new PT();
    final private Vector<String> lines = new Vector<String>();

    public synchronized void open(String cmd) throws TerminalException
    {
	if (cmd == null)
	    throw new NullPointerException("cmd may not be null");
	pt.create();
	pt.launch(cmd);
    }

    synchronized public int getLineCount()
    {
	return lines.size();
    }

    synchronized public String getLine(int index)
    {
	return index < lines.size()?lines.get(index):"";
    }

    synchronized public void close()
    {
	//FIXME:
    }

    synchronized public boolean isOpened()
    {
	return true;
    }

    synchronized public boolean readData()
    {
	byte[] res = pt.read();
	if (res == null)
	    return false;
	while (res.length > 0)
	{
	    System.out.println("got " + res.length + " bytes");
	    try {
	    final String str = new String(res, "utf-8");
	    System.out.println(str);
	    }
	    catch (java.io.UnsupportedEncodingException e)
	    {
	    }

	    res = pt.read();
	    if (res == null)
		return false;
	}
	return true;//FIXME:
    }
}
