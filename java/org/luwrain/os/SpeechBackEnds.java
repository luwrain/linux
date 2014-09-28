/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.os;

import org.luwrain.os.speech.*;

public class SpeechBackEnds
{
    static public SpeechBackEnd obtain(String type,
					String host,
					int port)
    {
	if (type == null || type.trim().isEmpty())
	    return null;
	if (!type.equals("voiceman"))
	    return null;
	VoiceMan backend = new VoiceMan();
	if (!backend.connect(!host.trim().isEmpty()?host.trim():"localhost", port))
	    return null;
	return backend;
    }
}
