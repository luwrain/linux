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

    static protected Object[] prepareContent()
    {
	final List res = new LinkedList();
	final DisksList disksList = new DisksList();
	final VolumesList volumesList = new VolumesList();
	final Volume[] volumes = volumesList.getVolumes();
	for(Disk d: disksList.getRemovableDisks())
	{
	    {
		int i = 0;
		for(i = 0;i < volumes.length;++i)
		    if (volumes[i].type == Volume.Type.REMOVABLE && volumes[i].name.equals(d.getDevName()))
			break;
		if (i < volumes.length)
		    continue;
	    }
	    boolean found = false;
	    for(Partition p: d.getPartitions())
	    {
		int k = 0;
		for(k = 0;k < volumes.length;++k)
		    if (volumes[k].type == Volume.Type.REMOVABLE && volumes[k].name.equals(p.getDevName()))
			break;
		if (k < volumes.length)
		    found = true;
	    }
	    if (found)
		continue;
	    res.add(d);
	}
	for(Volume v: volumes)
	    res.add(v);
	return res.toArray(new Object[res.size()]);
    }

    static public String getVolumeTypeStr(Luwrain luwrain, Volume volume)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(volume, "volume");
	switch(volume.type)
	{
	case ROOT:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemRoot");
	case USER_HOME:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemUserHome");
	case REGULAR:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemRegular");
	case REMOTE:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemRemote");
	case REMOVABLE:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemRemovable");
	default:
	    return "";
	}
    }

    static private ListArea.Params constructParams(Luwrain luwrain, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.name = name;
	params.model = new ListUtils.FixedModel(prepareContent());
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
	    if (item instanceof Volume)
	    {
		final Volume vol = (Volume)item;
		if (flags.contains(Flags.BRIEF))
		    value = vol.name; else
		    if (vol.type != Volume.Type.USER_HOME && vol.type != Volume.Type.ROOT)
			value = getVolumeTypeStr(luwrain, vol) + " " + vol.name; else
			value = getVolumeTypeStr(luwrain, vol);
	    } else
		if (item instanceof Disk)
		{
		    final Disk disk = (Disk)item;
		    value = "Неподключенное устройство " + disk.getDevName();//FIXME:
		} else
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
