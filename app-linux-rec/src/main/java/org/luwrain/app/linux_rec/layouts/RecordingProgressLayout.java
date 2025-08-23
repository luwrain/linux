/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.linux_rec.layouts;

import java.util.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.app.base.*;
import org.luwrain.controls.*;
import org.luwrain.app.linux_rec.*;

import static java.util.Objects.*;

public final class RecordingProgressLayout extends LayoutBase
{
    final App app;
    final Strings s;
    final NavigationArea area;
    private Job recJob = null;

    public RecordingProgressLayout(App app, ActionHandler close)
    {
	super(app);
	this.app = app;
	this.s = app.getStrings();
	this.area = new NavigationArea(getControlContext()) {
		@Override public int getLineCount()
		{
		    return 1;
		}
		@Override public String getLine(int index)
		{
		    return "";
		}
		@Override public String getAreaName()
		{
		    return s.recordingProgressAreaName();
		}
		@Override public boolean onInputEvent(InputEvent event)
		{
		    if (event.isSpecial())
			switch(event.getSpecial())
		    {
		    case ENTER:
			{
			    if (recJob == null)
			    {
				recJob = getLuwrain().newJob("sys", new String[]{ "parecord", "/x/proba.wav"}, App.REC_DIR.toString(), EnumSet.noneOf(Luwrain.JobFlags.class), null);
				app.message(s.recStarted());
			    } else
			    {
				recJob.stop();
				recJob = null;
				app.message(s.recFinished());
			    }
			    			return true;
		    }
		    }
		    return super.onInputEvent(event);
		    }
	};
	setCloseHandler(close);
	setAreaLayout(area, null);
	    }
}
