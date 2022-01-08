/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.linux.services;

import java.io.*;
import java.net.*;

import org.junit.*;

import org.luwrain.linux.*;

public class NmCliTest extends Assert
{
    private NmCli nmCli = null;

    @Test public void info() throws IOException
    {
	final WifiNetwork[] n = nmCli.scan();
	assertNotNull(n);
	assertEquals(7, n.length);
	for(WifiNetwork i: n)
	{
	    assertNotNull(i);
	    assertNotNull(i.getName());
	    assertFalse(i.getName().isEmpty());
	}
	final WifiNetwork i = n[0];
	assertNotNull("DIRECT-Pp-BRAVIA", i.getName());
    }

    @Before public void create()
    {
	this.nmCli = new NmCli(caller());
    }

    private NmCli.Caller caller()
    {
	return (args)->{
	    return new String[]{
		"IN-USE:                                 *",
		"BSSID:                                  12:A0:96:DE:64:DB",
		"SSID:                                   DIRECT-Pp-BRAVIA",
		"MODE:                                   Инфраструктура",
		"CHAN:                                   1",
		"RATE:                                   65 МБ/с",
		"SIGNAL:                                 65",
		"BARS:                                   ***",
		"SECURITY:                               WPA2",
		"IN-USE:",
		"BSSID:                                  D4:CA:6D:57:0F:63",
		"SSID:                                   fruitnest",
		"MODE:                                   Инфраструктура",
		"CHAN:                                   1",
		"RATE:                                   130 МБ/с",
		"SIGNAL:                                 55",
		"BARS:                                   **",
		"SECURITY:                               WPA2",
		"IN-USE:",
		"BSSID:                                  84:D8:1B:AC:61:85",
		"SSID:                                   keks-2.4",
		"MODE:                                   Инфраструктура",
		"CHAN:                                   6",
		"RATE:                                   270 МБ/с",
		"SIGNAL:                                 42",
		"BARS:                                   **",
		"SECURITY:                               WPA2",
		"IN-USE:",
		"BSSID:                                  D8:0D:17:03:C2:00",
		"SSID:                                   Sov_22_30",
		"MODE:                                   Инфраструктура",
		"CHAN:                                   11",
		"RATE:                                   270 МБ/с",
		"SIGNAL:                                 30",
		"BARS:                                   *",
		"SECURITY:                               WPA2",
		"IN-USE:",
		"BSSID:                                  F8:F0:82:5E:12:6E",
		"SSID:                                   Inessa",
		"MODE:                                   Инфраструктура",
		"CHAN:                                   3",
		"RATE:                                   270 МБ/с",
		"SIGNAL:                                 27",
		"BARS:                                   *",
		"SECURITY:                               WPA2",
		"IN-USE:",
		"BSSID:                                  60:A4:B7:40:A5:36",
		"SSID:                                   TP-Link_A536",
		"MODE:                                   Инфраструктура",
		"CHAN:                                   4",
		"RATE:                                   270 МБ/с",
		"SIGNAL:                                 24",
		"BARS:                                   *",
		"SECURITY:                               WPA2",
		"IN-USE:",
		"BSSID:                                  78:98:E8:D2:B9:C0",
		"SSID:                                   NTS_12",
		"MODE:                                   Инфраструктура",
		"CHAN:                                   5",
		"RATE:                                   270 МБ/с",
		"SIGNAL:                                 22",
		"BARS:                                   *",
		"SECURITY:                               WPA2"			    };
	};
    }
}
