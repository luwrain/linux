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

package org.luwrain.app.wifi;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.linux.wifi.*;

final class Appearance implements ListArea.Appearance<Object>
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Connections connections;

    Appearance(Luwrain luwrain, Strings strings, Connections connections)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(connections, "connections");
	this.luwrain = luwrain;
	this.strings = strings;
	this.connections = connections;
    }

    @Override public void announceItem(Object item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");
	if (!(item instanceof Network))
	    return;
	final Network network = (Network)item;
	if (!connections.getConnectedNetworkName().isEmpty() && connections.getConnectedNetworkName().equals(network.name))
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.SELECTED, network.toString(), Suggestions.CLICKABLE_LIST_ITEM)); else
	    if (network.hasPassword)
		luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.PROTECTED_RESOURCE, network.toString(), Suggestions.CLICKABLE_LIST_ITEM)); else
		luwrain.setEventResponse(DefaultEventResponse.listItem(network.toString(), Suggestions.CLICKABLE_LIST_ITEM));
    }

    @Override public String getScreenAppearance(Object item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");
	return item.toString();
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 0;
    }

    @Override public int getObservableRightBound(Object item)
    {
	return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
    }
}
