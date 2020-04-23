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
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;
import org.luwrain.template.*;

import org.luwrain.app.term.Strings;

final class MainLayout extends LayoutBase
{
    private final App app;
    private final Terminal term ;
    private final NavigationArea termArea;
    private Vector<String> lines = new Vector();
    private int oldHotPointX = -1;
    private int oldHotPointY = -1;

    MainLayout(App app)
    {
	this.app = app;
	this.term = new Terminal(app.getLuwrain(), app.termInfo);
	this.termArea = new NavigationArea(new DefaultControlContext(app.getLuwrain())){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ENTER:
			    app.sendChar('\n');
			    return true;
			case BACKSPACE:
			    app.sendChar('\b');
						    return true;
			case ESCAPE:
			    app.closeApp();
			    return true;
			}
		    if (!event.isSpecial() && !event.isModified())
		    {
			app.sendChar(event.getChar());
			return true;
		    }
		    		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		    /*
			case TAB:
			    term.write(new byte[]{(byte)'\t'});
			    return true;
			case 			    ALTERNATIVE_ARROW_LEFT:
			    term.writeCode(Terminal.Codes.ARROW_LEFT);
			    return true;
			case 			    ALTERNATIVE_ARROW_RIGHT:
			    term.writeCode(Terminal.Codes.ARROW_RIGHT);
			    return true;
			case 			    ALTERNATIVE_ARROW_UP:
			    term.writeCode(Terminal.Codes.ARROW_UP);
			    return true;
			case 			    ALTERNATIVE_ARROW_DOWN:
			    term.writeCode(Terminal.Codes.ARROW_DOWN);
			    return true;
			}
		    if (!event.isSpecial())
		    {
			if (event.getChar() == ' ')
			{
			    final String lastWord = TextUtils.getLastWord(getLine(getHotPointY()), getHotPointX());
			    if (lastWord != null && !lastWord.trim().isEmpty())
				luwrain.speak(lastWord);
			}
			term.write(event.getChar());
			return true;
		    }
		    //FIXME:
		    return super.onInputEvent(event);
		    */
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() == EnvironmentEvent.Type.REGULAR)
			switch(event.getCode())
			{
			case CLOSE:
			    			    app.closeApp();
			    return true;
			}
		    if (app.onSystemEvent(this, event))
			return true;
			return super.onSystemEvent(event);
		}
				@Override public int getLineCount()
		{
		    final int count = term.getLineCount();
		    return count > 0?count:1;
		    		}
		@Override public String getLine(int index)
		{
		    return term.getLine(index);
		}
		@Override public void announceLine(int index, String line)
		{
		    app.getLuwrain().setEventResponse(DefaultEventResponse.text(app.getLuwrain().getSpeakableText(line, Luwrain.SpeakableTextType.PROGRAMMING)));
		}
		@Override public String getAreaName()
		{
		    return app.getStrings().areaName();
		}
	    };
    }

    void update(char ch)
    {
		    /*
				 boolean bell)
    {
	if (bell)
	    luwrain.playSound(Sounds.TERM_BELL);
	if (text != null && !text.trim().isEmpty())
	{
	    if (text.length() == 1)
		luwrain.speakLetter(text.charAt(0)); else
		luwrain.speak(text);
	}
	if (hotPointX != oldHotPointX || hotPointY != oldHotPointY)
	{
	    if (text == null || text.isEmpty())
	    {
		final String line = area.getLine(hotPointY);
		if (line != null && hotPointX < line.length())
		    luwrain.speakLetter(line.charAt(hotPointX));
	    }
	    area.setHotPoint(hotPointX, hotPointY);
	    oldHotPointX = hotPointX;
	    oldHotPointY = hotPointY;
	}
	luwrain.onAreaNewContent(area);
	    */
	    }

    void termText(String text)
    {
	NullCheck.notNull(text, "text");
	term.termText(text);
    }


    AreaLayout getLayout()
    {
	return new AreaLayout(termArea);
    }
}
