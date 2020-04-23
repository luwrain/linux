/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.term;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;

final class Terminal implements Lines
{
    static private final String LOG_COMPONENT = App.LOG_COMPONENT;

    private final Luwrain luwrain;
    private final TermInfo termInfo;
    private Vector<String> lines = new Vector();
    private int hotPointX = 0;
    private int hotPointY = -1;
    private StringBuilder seq = new StringBuilder();

    Terminal(Luwrain luwrain, TermInfo termInfo)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(termInfo, "termInfo");
	this .luwrain = luwrain;
	this.termInfo = termInfo;
    }

    @Override public int getLineCount()
    {
	return lines.size();
    }

    @Override public String getLine(int index)
    {
	if (index >= lines.size())
	    return "";
	return lines.get(index);
    }

    void termText(String text)
    {
	NullCheck.notNull(text, "text");
	if (text.isEmpty())
	    return;
	if (lines.isEmpty())
	    lines.add("");
	final StringBuilder speaking = new StringBuilder();
	try {
	    for(int i = 0;i < text.length();i++)
	    {
		final char ch = text.charAt(i);
		this.seq.append(ch);
		final String seqStr = new String(this.seq);
		final String res = termInfo.find(seqStr);
		if (res == null)
		{
		    speaking.append(seqStr);
		    this.seq = new StringBuilder();
		    switch(seqStr)
		    {
		    case "\n":
			lines.add("");
			continue;
		    default:
			lines.set(lines.size() - 1, lines.get(lines.size() - 1) + seqStr);
			continue;
		    }
		}
		if (res.isEmpty())
		    continue;
		this.seq = new StringBuilder();
		switch(res)
		{
		case "color":
		    continue;
		default:
		    Log.warning(LOG_COMPONENT, "unknown command: '" + res + "'");
		    continue;
		}
	    }
	}
	finally {
	    speak(new String(speaking));
	}
    }

    private void speak(String text)
    {
	NullCheck.notNull(text, "text");
	final StringBuilder str = new StringBuilder();
	for(int i = 0;i < text.length();i++)
	{
	    final char ch = text.charAt(i);
	    if (ch == 7)
	    {
		luwrain.playSound(Sounds.TERM_BELL);
		continue;
	    }
	    if (ch < 32)
	    {
		str.append(" ");
		continue;
	    }
	    str.append(ch);
	}
	luwrain.speak(luwrain.getSpeakableText(new String(str), Luwrain.SpeakableTextType.PROGRAMMING));
    }
}
