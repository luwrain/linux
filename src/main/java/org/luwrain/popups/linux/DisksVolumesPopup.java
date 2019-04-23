
package org.luwrain.popups.linux;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.script.*;
import org.luwrain.linux.*;
import org.luwrain.linux.disks.*;

final class DisksVolumesPopup extends ListPopupBase implements org.luwrain.popups.DisksVolumesPopup
{
    static private final String LOG_COMPONENT = "linux";
    static private final String LIST_HOOK = "luwrain.linux.popups.disks.list";
        static private final String CLICK_HOOK = "luwrain.linux.popups.disks.click";

private File result = null;

    DisksVolumesPopup(Luwrain luwrain, String name, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, constructParams(luwrain, name), popupFlags);
    }

    @Override public File result()
    {
	return this.result;
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() || !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		return closing.doOk();
		/*
	    case INSERT:
	    case DELETE:
		*/
	    }
	return super.onInputEvent(event);
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
	return true;
    }

    static private Object[] prepareContent(Luwrain luwrain)
    {

	final DisksList disksList = new DisksList();
	final Disk[] disks = disksList.getRemovableDisks();
	final VolumesList volumesList = new VolumesList();
	final Volume[] volumes = volumesList.getVolumes();
	final Object volumesObj = ScriptUtils.createReadOnlyArray(volumes);
		final Object disksObj = ScriptUtils.createReadOnlyArray(disks);
	final java.util.concurrent.atomic.AtomicReference hookRes = new java.util.concurrent.atomic.AtomicReference();
	luwrain.xRunHooks(LIST_HOOK, (hook)->{
		try {
		    final Object obj = hook.run(new Object[]{volumesObj, disksObj});
		    if (obj == null)
			return Luwrain.HookResult.CONTINUE;
		    final List objs = ScriptUtils.getArray(obj);
		    if (objs == null)
			return Luwrain.HookResult.CONTINUE;
		    hookRes.set(objs.toArray(new Object[objs.size()]));
		    return Luwrain.HookResult.BREAK;
		}
		catch(RuntimeException e)
		{
		    Log.error(LOG_COMPONENT, "unable to run the " + LIST_HOOK + " hook:" + e.getClass().getName() + ":" + e.getMessage());
		    return Luwrain.HookResult.CONTINUE;
		}
	    });
	if (hookRes == null)
	    return new Item[0];
	if (!(hookRes.get() instanceof Object[]))
	    return new Item[0];
	final Object[] objs = (Object[])hookRes.get();
		final List<Item> res = new LinkedList();
	for(Object o: objs)
	{
	    final Object titleObj = ScriptUtils.getMember(o, "title");
	    final String title = ScriptUtils.getStringValue(titleObj);
	    if (title == null || title.isEmpty())
		continue;
	    res.add(new Item(title, ScriptUtils.getMember(o, "obj")));
	}
	return res.toArray(new Item[res.size()]);
    }

    static private ListArea.Params constructParams(Luwrain luwrain, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.name = name;
	params.model = new ListUtils.FixedModel(prepareContent(luwrain)){
		@Override public void refresh()
		{
		    setItems(prepareContent(luwrain));
		}};
	params.appearance = new ListUtils.DefaultAppearance(new DefaultControlContext(luwrain));
	return params;
    }

    static private final class Item
    {
	final String title;
	final Object obj;

	Item(String title, Object obj)
	{
	    NullCheck.notEmpty(title, "title");
	    this.title = title;
	    this.obj = obj;
	}

	@Override public String toString()
	{
	    return title;
	}
    }

}
