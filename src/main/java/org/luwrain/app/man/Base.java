/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.man;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.linux.*;

final class Base
{
    private final Luwrain luwrain;
    private final Strings strings;
    private String[] pages = new String[0];

    Base(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
    }

    boolean search(String query)
    {
	NullCheck.notEmpty(query, "query");
	final List<String> res = new LinkedList();
	final Scripts scripts = new Scripts(luwrain);
	final Process p = scripts.runAsync(Scripts.ID.MAN_SEARCH, new String[]{query}, false);
	if (p == null)
	    return false;
	try {
	    try {
		p.getOutputStream().close();
		final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = r.readLine();
		while (line != null)
		{
		    res.add(line);
		    line = r.readLine();
		}
		p.waitFor();
		pages = res.toArray(new String[res.size()]);
		return true;
	    }
	    finally {
		p.getInputStream().close();
	    }
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return false;
	}
	catch(IOException e)
	{
	    luwrain.crash(e);
	    return false;
	}
    }

    ConsoleArea2.Model getSearchAreaModel()
    {
	return new SearchAreaModel();
    }

    ConsoleArea2.Appearance getSearchAreaAppearance()
    {
	return new SearchAreaAppearance();
    }

    private class SearchAreaAppearance implements ConsoleArea2.Appearance
    {
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    luwrain.setEventResponse(DefaultEventResponse.text(item.toString()));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
	}
    }

    private class SearchAreaModel implements ConsoleArea2.Model
    {
        @Override public int getConsoleItemCount()
	{
	    return pages.length;
	}
	@Override public Object getConsoleItem(int index)
	{
	    if (index < 0 || index >= pages.length)
		throw new IllegalArgumentException("index (" + index + ") must be greater or equal to zero and less than " + pages.length);
	    return pages[index];
	}
    }
}
