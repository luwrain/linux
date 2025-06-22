/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

import java.io.File;

public final class VolumesList
{
    static Volume[] getVolumes()
    {
	final File[] f = File.listRoots();
	if (f == null || f.length == 0)
	    return new Volume[]{new Volume(Volume.Type.REGULAR, new File("C:\\"), "C:\\")};
	final Volume[] l = new Volume[f.length];
	for(int i = 0;i < f.length;++i)
	    l[i] = new Volume(Volume.Type.REGULAR, f[i], f[i].getAbsolutePath());
	return l;
    }
}
