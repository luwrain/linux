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
    public String newText = "";
    public boolean bell = false;

    final private PT pt = new PT();
    private byte[] toWrite = new byte[0];
    final private Vector<String> lines = new Vector<String>();

    public synchronized int getHotPointX()
    {
	return !lines.isEmpty()?lines.lastElement().length():0;
    }

    public synchronized int getHotPointY()
    {
	return !lines.isEmpty()?lines.size() - 1:0;
    }

    public synchronized void open(String cmd) throws TerminalException
    {
	if (cmd == null)
	    throw new NullPointerException("cmd may not be null");
	if (!pt.create())
	    throw new TerminalException("Cannot create pseudo terminal");
	if (!pt.launch(cmd))
	    throw new TerminalException("Cannot launch child process");
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
	pt.close();
    }

    public synchronized boolean isActive()
    {
	return true;
    }

    public synchronized void write(byte[] data)
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

    public synchronized boolean sync()
    {
	newText = "";
	bell = false;

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
	    onData(res);
	    res = pt.read();
	}
	return (newText != null && !newText.isEmpty()) || bell;
    }

    private void onData(byte[] data)
    {
	try {
String str = new String(data, "utf-8");//FIXME:UTF-8
for(int i = str.length() - 1;i >= 0;--i)
    if (str.charAt(i) == '\b')
    {
	onString(str.substring(0, i + 1));
	return;
    }
onString(str);
	}
	catch (java.io.UnsupportedEncodingException e)
	{
	    e.printStackTrace();
	    onString("#Skipped data: decoding problems#");
	}
    }

    private void onString(String str)
    {
	if (str == null || str.isEmpty())
	    return;
	if (lines.isEmpty())
	    lines.add("");

	final StringBuilder newTextBuilder = new StringBuilder();
	StringBuilder lastLineBuilder = new StringBuilder();

	for(int i = 0;i < str.length();++i)
	{
	    final char c = str.charAt(i);

	    if (Character.isISOControl(c))
	    {
		switch(c)
		{
		case 7://bell
		    bell = true;
		    continue;
		case '\n':
		    lines.set(lines.size() - 1, lines.lastElement() + lastLineBuilder.toString());
		    lines.add("");
		    lastLineBuilder = new StringBuilder();
		    newTextBuilder.append(" ");
		    continue;
		case '\b':
		    if (lastLineBuilder.toString().isEmpty())
		    {
			if (!lines.isEmpty() && !lines.lastElement().isEmpty())
			{
			    final String lastLine = lines.lastElement();
			    lines.set(lines.size() - 1, lastLine.substring(0, lastLine.length() - 1));
			}
		    }
		    continue;
		default:
		    continue;
		}
	    }

	    lastLineBuilder.append(c);
	    newTextBuilder.append(c);
	}
	lines.set(lines.size() - 1, lines.lastElement() + lastLineBuilder.toString());
	newText = newText + newTextBuilder.toString();
    }
}
