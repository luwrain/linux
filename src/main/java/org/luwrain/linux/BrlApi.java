/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.linux;

import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class BrlApi implements Braille
{
    @Override public InitResult init(EventConsumer eventConsumer)
    {
	return new InitResult();
	    }

    @Override synchronized public void writeText(String text)
    {
    }

    @Override public String getDriverName()
    {
	return "";
    }

    @Override public int getDisplayWidth()
    {
	return 0;
    }

    @Override public int getDisplayHeight()
    {
	return 0;
    }

    synchronized private void readKeys()
    {
    }
}
