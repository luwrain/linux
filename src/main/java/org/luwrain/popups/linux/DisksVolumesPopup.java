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

//LWR_API 1.0

package org.luwrain.popups.linux;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.linux.*;
import org.luwrain.linux.disks.*;

public class DisksVolumesPopup extends ListPopupBase implements org.luwrain.popups.DisksVolumesPopup
{
    static private Linux linux = null;
protected Volume result = null;

    public DisksVolumesPopup(Luwrain luwrain, String name, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, constructParams(luwrain, name), popupFlags);
    }

    @Override public File result()
    {
	return null;//FIXME:
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() || !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		closing.doOk();
		return true;
		/*
	    case INSERT:
		return attach();
	    case DELETE:
		return detach();
		*/
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onOk()
    {
	/*
	final Object res = selected();
	if (res == null || !(res instanceof Partition))
	    return false;
	result = (Partition)res;
	return true;
	*/
	return false;
    }

    /*
    private boolean attach()
    {
	final Object selected = selected();
	if (selected == null ||
	    selected instanceof Partition ||
	    selected instanceof String)
	    return false;
	final int res = control.attachStorageDevice(selected);
	if (res < 0)
	{
	    luwrain.message("Во время попытки подключения разделов на съёмном накопителе произошла ошибка", Luwrain.MessageType.ERROR);
	    return true;
	}
	luwrain.message("Подключено разделов: " + res, res > 0?Luwrain.MessageType.OK:Luwrain.MessageType.REGULAR);
	refresh();
	return true;
    }

    private boolean detach()
    {
	final Object selected = selected();
	if (selected == null ||
	    selected instanceof Partition ||
	    selected instanceof String)
	    return false;
	final int res = control.detachStorageDevice(selected);
	if (res < 0)
	{
	    luwrain.message("Во время попытки отключения разделов на съёмном накопителе произошла ошибка", Luwrain.MessageType.ERROR);
	    return true;
	}
	luwrain.message("Отключено разделов: " + res, res > 0?Luwrain.MessageType.OK:Luwrain.MessageType.REGULAR);
	refresh();
	return true;
    }
    */

    static private ListArea.Params constructParams(Luwrain luwrain, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.name = name;
	params.model = new ListUtils.FixedModel();
	params.appearance = new Appearance(luwrain);
	//	params.flags = listFlags;
	return params;
    }

    static protected class Appearance implements ListArea.Appearance
    {
	protected final Luwrain luwrain;
	Appearance(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}
	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flag ");
	    final String value;
	    /*
	    if (item instanceof Volume)
	    {
		final Volume vol = (Volume)item;
		if (flags.contains(Flags.BRIEF))
		    value = part.getBriefTitle(); else
		    value = part.getFullTitle();
	    } else
	    */
		value = item.toString();
	    if (!value.trim().isEmpty())
		luwrain.setEventResponse(DefaultEventResponse.text(value)); else
		luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	}
	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    /*
	    if (item instanceof Partition)
	    {
		final Partition part = (Partition)item;
		return part.getFullTitle();
	    }
	    */
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
}
