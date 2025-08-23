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
    private String name = null;
    private Date started = null;

    public RecordingProgressLayout(App app, ActionHandler close, ListArea<Entry> entriesArea)
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
started = new Date();
				    RecordingProgressLayout.this.name = App.TIME_FORMAT.format(started) + ".wav";
				    recJob = getLuwrain().newJob("sys",
								 new String[]{ "parecord", RecordingProgressLayout.this.name}, App.REC_DIR.toString(),
								 EnumSet.noneOf(Luwrain.JobFlags.class),
								 null);
				    app.message(s.recStarted());
				} else
				{
				    recJob.stop();
				    recJob = null;
				    final var e = new Entry();
				    e.name = RecordingProgressLayout.this.name;
				    e.startedTimestamp = started.getTime();
				    e.finishedTimestamp = new Date().getTime();
				    app.conf.entries.add(e);
				    entriesArea.refresh();
				    entriesArea.select(e, false);
				    getLuwrain().saveConf(app.conf);
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
