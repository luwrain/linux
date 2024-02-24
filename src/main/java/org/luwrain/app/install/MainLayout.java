/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.install;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
//import org.luwrain.core.JobsManager.Entry;

final class MainLayout extends LayoutBase
{
    private final App app;
    final WizardArea wizardArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.wizardArea = new WizardArea(getControlContext());
	setAreaLayout(wizardArea, actions());
	final WizardArea.Frame devFrame = wizardArea.newFrame()
	.addText(app.getStrings().greeting());
	for(String f: app.blockDevices.getHardDrives())
	    devFrame.addClickable(getDeviceStr(f), (values)->{ return false; });
	wizardArea.show(devFrame);
    }

    private String getDeviceStr(String f)
    {
	NullCheck.notNull(f, "f");
	final StringBuilder b = new StringBuilder();
	b.append(f).append(", ")
	.append(app.blockDevices.getDeviceSize(f) / 1048576 / 1024).append("G, ")
	.append(app.blockDevices.getDeviceName(f));
	return new String(b);
    }
}
