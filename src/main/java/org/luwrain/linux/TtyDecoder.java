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

package org.luwrain.linux;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

public class TtyDecoder
{
    public interface Listener
    {
	void onBell();
	void onBackspace();
	void onArrowLeft();
	void onArrowRight();
	void onNewLine();
	void onChar(char c);
    }

    protected final Reader reader;
    protected final Listener listener;

    public TtyDecoder(Reader reader, Listener listener)
    {
	NullCheck.notNull(reader, "reader");
	NullCheck.notNull(listener, "listener");
	this.reader = reader;
	this.listener = listener;
    }

    public boolean read() throws IOException
    {
	final int c = reader.read();
	if (c < 0)
	    return false;
	switch(c)
	{
	case 27://special escape sequence
	    return onEscapeSeq();
	default:
	    if (Character.isISOControl((char)c))
		onIsoControl((char)c); else
		listener.onChar((char)c);
	    return true;
	}
    }

    private void onIsoControl(char c)
    {
	switch(c)
	{
	case 7://bell
	    listener.onBell();
	    return;
	case 10://new line
	    listener.onNewLine();
	    return;
	case '\b':
	    listener.onBackspace();
	    return;
	default:
	    Log.debug(Linux.LOG_COMPONENT, "unhandled terminal char: " + String.format("%d", (int)c));
	}
    }

    private boolean onEscapeSeq() throws IOException
    {
	int c = reader.read();
	if (c < 0)
	    return false;
	if (c == '[')
	{
	    c = reader.read();
	    if (c < 0)
		return false;
	    if (c == 'C')
	    {
		listener.onArrowRight();
		return true;
	    }
	}
	return true;
    }
}
