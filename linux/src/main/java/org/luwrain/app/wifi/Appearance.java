/*
   Copyright 2012-2022 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.wifi;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.linux.*;

import static org.luwrain.core.DefaultEventResponse.*;

final class Appearance extends ListUtils.AbstractAppearance<WifiNetwork>
{
    private final Luwrain luwrain;
    private final Strings strings;
    Appearance(App app)
    {
	this.luwrain = app.getLuwrain();
	this.strings = app.getStrings();
    }

    @Override public void announceItem(WifiNetwork wifi, Set<Flags> flags)
    {
	if (wifi.isConnected())
	    luwrain.setEventResponse(listItem(Sounds.SELECTED, wifi.getName(), Suggestions.CLICKABLE_LIST_ITEM)); else
	    if (wifi.getProtectionType() != null && !wifi.getProtectionType().isEmpty())
		luwrain.setEventResponse(listItem(Sounds.BLOCKED, wifi.getName(), Suggestions.CLICKABLE_LIST_ITEM)); else
	luwrain.setEventResponse(listItem(wifi.toString(), Suggestions.CLICKABLE_LIST_ITEM));
    }

    @Override public String getScreenAppearance(WifiNetwork wifi, Set<Flags> flags)
    {
	return wifi.toString();
    }
}
