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
protected File result = null;

    public DisksVolumesPopup(Luwrain luwrain, String name, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, constructParams(luwrain, name), popupFlags);
    }

    @Override public File result()
    {
	return this.result;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() || !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		return closing.doOk();
	    case INSERT:
		{
		    final File[] res = mount(selected());
		if (res == null)
		    return false;
		if (res.length == 0)
		{
		    luwrain.playSound(Sounds.ERROR);
		    return true;
		}
	refresh();
	luwrain.playSound(Sounds.DONE);
	return true;
		}
	    case DELETE:
		return umountSelected();
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() == EnvironmentEvent.Type.BROADCAST)
	    switch(event.getCode())
	    {
	    case REFRESH:
		if (event.getBroadcastFilterUniRef().startsWith("disksvolumes:"))
		    refresh();
		return true;
	    default:
		return super.onSystemEvent(event);
	    }
	return super.onSystemEvent(event);
    }

    @Override public boolean onOk()
    {
	final Object res = selected();
	if (res == null)
	    return false;
	if (res instanceof Disk)
	{
		    final File[] mountRes = mount(res);
		    if (mountRes == null)
			return false;
		    if (mountRes.length == 0)
			return true;
		    this.result = mountRes[0];
		    return true;
	}
	if (res instanceof Volume)
	{
	    final Volume volume = (Volume)res;
	    this.result = volume.file;
	    return true;
	}
	return false;
    }

    protected File[] mount(Object obj)
    {
	if (obj == null || !(obj instanceof Disk))
	    return null;
	final Disk disk = (Disk)obj;
	final Mounting mounting = new Mounting(luwrain, new DefaultMountPointConstructor());
return mounting.mountAll(disk);
    }

    protected boolean umountSelected()
    {
	final Object selected = selected();
	if (selected == null || !(selected instanceof Volume))
	    return false;
	final Volume volume = (Volume)selected;
	if (volume.type != Volume.Type.REMOVABLE)
	    return false;
	final Mounting mounting = new Mounting(luwrain, new DefaultMountPointConstructor());
	if (mounting.umount(volume))
	{
	    luwrain.playSound(Sounds.DONE);
	    refresh();
	} else
	    luwrain.playSound(Sounds.ERROR);
	return true;
    }

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
	params.model = new ListUtils.FixedModel(prepareContent()){
		@Override public void refresh()
		{
		    setItems(prepareContent());
		}};
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
	    final String value = getTitleStr(item, flags.contains(Flags.BRIEF));
	    if (!value.trim().isEmpty())
		luwrain.setEventResponse(DefaultEventResponse.text(value)); else
		luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	}
	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    return getTitleStr(item, false);
	}
	protected String getTitleStr(Object item, boolean brief)
	{
	    NullCheck.notNull(item, "item");
	    if (item instanceof Volume)
	    {
		final Volume vol = (Volume)item;
		if (brief)
		    return vol.name;
		if (vol.type != Volume.Type.USER_HOME && vol.type != Volume.Type.ROOT)
		    return getVolumeTypeStr(luwrain, vol) + " " + vol.name;
		return getVolumeTypeStr(luwrain, vol);
	    }
	    if (item instanceof Disk)
	    {
		final Disk disk = (Disk)item;
		return "Неподключенное устройство " + disk.getDevName();//FIXME:
	    }
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
