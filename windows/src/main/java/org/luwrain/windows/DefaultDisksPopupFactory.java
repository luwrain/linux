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

package org.luwrain.windows;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

public final class DefaultDisksPopupFactory implements DisksPopup.Factory
    {
	@Override public DisksPopup.Disks newDisks(Luwrain luwrain)
	{
	    return new DisksImpl();
	}

private final class DisksImpl implements DisksPopup.Disks
	{
	    @Override public DisksPopup.Disk[] getDisks(Set<DisksPopup.Flags> flags)
	    {
		final List<DiskImpl> res = new ArrayList<>();
		final File[] roots = File.listRoots();
		if (roots != null)
		for(File f: roots)
		    res.add(new DiskImpl(f.getAbsolutePath()));
		return res.toArray(new DisksPopup.Disk[res.size()]);
	    }
	}

	private final class DiskImpl implements DisksPopup.Disk
	{
	    final String
		title, path;
	    DiskImpl(String path)
	    {
		this.title = path;
		this.path = path;
	    }
	    @Override public boolean isActivated()
	    {
		return true;
	    }
	    @Override public File activate(Set<DisksPopup.Flags> flags)
	    {
		return new File(path);
	    }
	    	    @Override public boolean deactivate(Set<DisksPopup.Flags> flags)
	    {
		return true;
	    }
	    	    	    @Override public boolean poweroff(Set<DisksPopup.Flags> flags)
	    {
		return true;
	    }

	    
	    @Override public String toString()
	    {
		return title;
	    }
	}
    }
