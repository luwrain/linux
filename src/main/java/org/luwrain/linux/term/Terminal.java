/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

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
import java.io.*;
import java.nio.charset.*;

import org.luwrain.core.NullCheck;

public class Terminal
{
    public enum Codes {
	ARROW_LEFT,
	ARROW_RIGHT,
	ARROW_UP,
	ARROW_DOWN,
    };

private StringBuilder newText = null;
    public boolean bell = false;
    private int hotPointX = 0;
    private int hotPointY = 0;

    final private PT pt = new PT();
    private byte[] toWrite = new byte[0];
    final private Vector<String> lines = new Vector<String>();

    public Terminal()
    {
	lines.add("");
    }

    public synchronized boolean sync()
    {
	newText = new StringBuilder();
	bell = false;
	//Trying to write pending data if needed
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
	//Reading data
	boolean haveNewData = false;
	byte[] res = pt.read();
	while (res != null && res.length > 0)
	{
	    haveNewData = true;
	    try {
	    onData(res);
	    }
	    catch(IOException e)
	    {
		e.printStackTrace();
	    }
	    res = pt.read();
	}
	return haveNewData;
    }

    private void onData(byte[] data) throws IOException
    {
	final ByteArrayInputStream s = new ByteArrayInputStream(data);
	final BufferedReader r = new BufferedReader(new InputStreamReader(s, StandardCharsets.UTF_8));
	int c = 0;
	      while ( (c = r.read()) >= 0)
	      {
		  while (lines.size() <= hotPointY)
		      lines.add("");
		  if (hotPointX > lines.get(hotPointY).length())
		      hotPointX = lines.get(hotPointY).length();
		  if (c == 27)//special escape sequence
		  {
		      onEscapeSeq(r);
		      continue;
		  }
		  if (Character.isISOControl(c))
		      onIsoControl(c); else
		  onRegularChar((char)c);
	      }
    }

    private void onRegularChar(char c)
    {
		  newText.append(c);
		  final String line = lines.get(hotPointY);
		  if (hotPointX < line.length())
		      lines.set(hotPointY, line.substring(0, hotPointX) + c + line.substring(hotPointX + 1)); else
		      lines.set(hotPointY, line + c);
		  ++hotPointX;
    }

    private void onIsoControl(int c)
    {
		switch(c)
		{
		case 7://bell
		    bell = true;
		    return;
		case 10://new line
		    {
			final String line = lines.get(hotPointY);
			if (hotPointX < line.length())
			{
			    lines.set(hotPointY, line.substring(0, hotPointX));
			    lines.add(hotPointY + 1, line.substring(hotPointX));
			} else
			    lines.add(hotPointY + 1, "");
			++hotPointY;
			hotPointX = 0;
			}
		    newText.append(" ");
		    return;
		case '\b':
		    if (hotPointX > 0)
			--hotPointX;
		    return;
		}
    }

    private void onEscapeSeq(Reader r) throws IOException
    {
	int c = r.read();
	if (c < 0)
	    return;
	if (c == '[')
	{
	    c = r.read();
	    if (c < 0)
		return;
	    if (c == 'C')
	    {
		if (hotPointX < lines.get(hotPointY).length())
		    ++hotPointX;
		return;
	    } //arrow right
	}
    }

    public String newText()
    {
	return newText != null?new String(newText):"";
    }

    public synchronized int getHotPointX()
    {
	return hotPointX;
    }

    public synchronized int getHotPointY()
    {
	return hotPointY;
    }

    public synchronized void open(String cmd, String dir) throws TerminalException
    {
	NullCheck.notNull(cmd, "cmd");
	NullCheck.notNull(dir, "dir");
	if (!pt.create())
	    throw new TerminalException("Cannot create pseudo terminal");
	if (!pt.launch(cmd, dir))
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
	return pt.isOpened();
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

    public boolean write(char c)
    {
	final ByteArrayOutputStream s = new ByteArrayOutputStream();
	final OutputStreamWriter w = new OutputStreamWriter(s, StandardCharsets.UTF_8);
	try {
	    w.write(c);
	    w.flush();
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return false;
	}
	write(s.toByteArray());
	return true;
    }

    public boolean write(String str)
    {
	final ByteArrayOutputStream s = new ByteArrayOutputStream();
	final OutputStreamWriter w = new OutputStreamWriter(s, StandardCharsets.UTF_8);
	try {
	    w.write(str);
	    w.flush();
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return false;
	}
	write(s.toByteArray());
	return true;
    }

    public void writeCode(Codes code)
    {
	switch(code)
	{
	case ARROW_LEFT:
write(new byte[]{27,'[','D'});
return;
	case ARROW_RIGHT:
write(new byte[]{27,'[','C'});
return;
	case ARROW_UP:
write(new byte[]{27,'[','5','~'});
return;
	case ARROW_DOWN:
write(new byte[]{27,'[','6','~'});
return;
	}
    }

}
