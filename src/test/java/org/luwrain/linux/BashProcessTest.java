/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

import java.io.*;
import java.net.*;

import org.junit.*;

import org.luwrain.core.*;

public class BashProcessTest extends Assert
{
    @Test public void escapeEmpty()
    {
	assertEquals("''", BashProcess.escape(""));
    }

        @Test public void escapeSimple()
    {
	assertEquals("'abc'", BashProcess.escape("abc"));
    }

            @Test public void escapeComplex()
    {
	assertEquals("'a'\\''b'\\''c'", BashProcess.escape("a'b'c"));
    }


    
}
