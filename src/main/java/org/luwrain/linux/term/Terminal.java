/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.linux.term;

import java.util.*;

public class Terminal
{
    public boolean newHotPoint = false;
    public boolean newText = false;
    public boolean bell = false;
    public int oldHotPointX = 0;
    public int oldHotPointY = 0;
    public int newHotPointX = 0;
    public int newHotPointY = 0;


    final private PT pt = new PT();
    private byte[] toWrite = new byte[0];
    final private Vector<String> lines = new Vector<String>();

    synchronized public void open(String cmd) throws TerminalException
    {
	if (cmd == null)
	    throw new NullPointerException("cmd may not be null");
	if (!pt.create())
	    throw new TerminalException("Cannot create pseudo terminal");
	if (!pt.launch(cmd))
	    throw new TerminalException("Cannot launch child process");
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

    synchronized public boolean isActive()
    {
	return true;
    }

    synchronized public void write(byte[] data)
    {
	if (data == null || data.length < 1)
	    return;
	final int oldLen = toWrite.length;
	toWrite = Arrays.copyOf(toWrite, oldLen + data.length);
	for(int i = 0;i < data.length;++i)
	    toWrite[oldLen + i] = data[i];
    }

    public void write(char c)
    {
	write(new byte[]{(byte)c});
    }

    synchronized public boolean exchange()
    {
	newHotPoint = false;
	newText = false;
	bell = false;
	oldHotPointX = newHotPointX;
	oldHotPointY = newHotPointY;

	if (toWrite.length > 0)
	{
	    final int res = pt.write(toWrite);
	    if (res > 0)
	    {
		if (res < toWrite.length)
		{
		    for(int i = res;i < toWrite.length;++i)
			toWrite[i - res] = toWrite[i];
		    toWrite = Arrays.copyOf(toWrite, toWrite.length - res);
		} else
		    toWrite = new byte[0];
	    }
	} //writing;

	byte[] res = pt.read();
	while (res != null && res.length > 0)
	{
	    try {
		final String str = new String(res, "utf-8");
addString(str);
		//		System.out.println("adding \"" + str + "\" to lines");
	    }
	    catch (java.io.UnsupportedEncodingException e)
	    {
		lines.add("FIXME:encoding problem");
	    }
	    res = pt.read();
	    if (res == null)
		break;
	}
	return newHotPoint || newText || bell;
    }

    private void addString(String str)
    {
	if (str == null || str.isEmpty())
	    return;
	if (lines.isEmpty())
	    lines.add("");
	StringBuilder b = new StringBuilder();
	for(int i = 0;i < str.length();++i)
	{
	    final char c = str.charAt(i);
	    if (Character.isISOControl(c))
	    {
		if (c == 7)
		{
		    bell = true;
		    continue;
		}
		if (c == '\n')
		{
		    lines.set(lines.size() - 1, lines.lastElement() + b.toString());
		    lines.add("");
		    b = new StringBuilder();
		    continue;
		}
		//		continue;
	    }
	    b.append(c);
	}
		    lines.set(lines.size() - 1, lines.lastElement() + b.toString());
    }
}
