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

import org.luwrain.core.*;
import org.luwrain.popups.*;

final class Conversations
{
    private final Luwrain luwrain;
    private final Strings strings;

    Conversations(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
    }

    boolean useSavedPassword()
    {
	return Popups.confirmDefaultYes(luwrain, strings.connectionPopupName(), strings.useSavedPassword());
    }

    boolean saveThePassword()
    {
	return Popups.confirmDefaultYes(luwrain, strings.connectionPopupName(), strings.saveThePassword());
    }

    String askPassword()
    {
	return Popups.simple(luwrain, strings.connectionPopupName(), strings.enterThePassword(), "");
    }
}