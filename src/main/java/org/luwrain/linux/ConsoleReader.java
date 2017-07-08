/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import java.nio.charset.*;

import org.luwrain.core.NullCheck;

public class ConsoleReader
{
    protected List<Item> items = new LinkedList<Item>();
    protected StringBuilder newText = new StringBuilder();

    public void read(Reader reader) throws IOException
    {
	NullCheck.notNull(reader, "reader");
	while (reader.ready())
	{
	    final int c = reader.read();
	    if (c == 27)//special escape sequence
	    {
		commit();
		onEscapeSeq(reader);
		continue;
	    }
	    if (Character.isISOControl(c))
	    {
		commit();
		onIsoControl(c); 
		continue;
	    }
	    onRegularChar((char)c);
	}
    }

    protected void onRegularChar(char c)
    {
	newText.append(c);
    }

    protected void onIsoControl(int c)
    {
	switch(c)
	{
	case 7://bell
	    items.add(new Item(Item.Type.BELL));
	    return;
	case 10://new line
	    items.add(new Item(Item.Type.NEW_LINE));
	    return;
	case '\b':
	    items.add(new Item(Item.Type.BACKSPACE));
	    return;
	}
    }

    protected void onEscapeSeq(Reader reader) throws IOException
    {
	NullCheck.notNull(reader, "reader");
	int c = reader.read();
	if (c < 0)
	    return;
	if (c == '[')
	{
	    c = reader.read();
	    if (c < 0)
		return;
	    if (c == 'C')
	    {
		items.add(new Item(Item.Type.KEY_RIGHT));
		return;
	    } //arrow right
	}
    }

    public void commit()
    {
	final String text = new String(newText);
	if (!text.isEmpty())
	    items.add(new Item(text));
	newText = new StringBuilder();
    }

    public void clear()
    {
	newText = new StringBuilder();
	items.clear();
    }

    public Item[] getItems()
    {
	return items.toArray(new Item[items.size()]);
    }

    static public class Item 
    {
	public enum Type 
	{
	    TEXT,
	    BELL,
	    NEW_LINE,
	    BACKSPACE,
	    KEY_RIGHT,
	};

	public final Type type;
	public final String text;

	public Item(Type type)
	{
	    NullCheck.notNull(type, "type");
	    this.type = type;
	    this.text = null;
	}

	public Item(String text)
	{
	    NullCheck.notNull(text, "text");
	    this.type = Type.TEXT;
	    this.text = text;
	}

	@Override public String toString()
	{
	    if (type == Type.TEXT && text != null)
		return "[" + type.toString() + "] " + text;
	    return "[" + type.toString() + "]";
	}
    }
}
