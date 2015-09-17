/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

public class ProcessGroup
{
    private static native int execProcessGroup(String cmd, String input);
    private static native int getStatus(int pid);
    private static native void killPg(int pid);

    private int pid = -1;

    public synchronized boolean run(String cmd, String input)
    {
	if (cmd == null)
	    throw new NullPointerException("cmd may not be null");
	if (input == null)
	    throw new NullPointerException("input may not be null");
	if (cmd.isEmpty())
	    throw new IllegalArgumentException("cmd may not be empty");
	stop();
	pid = execProcessGroup(cmd, input);
	if (pid == 0)
	    pid = -1;
	return pid > 0;
    }

    public synchronized boolean busy()
    {
	if (pid <= 0)
	    return false;
	final int status = getStatus(pid);
	if (status == 1)
	    return true;
	pid = -1;
	return false;
    }

    public synchronized void stop()
    {
	if (pid <= 0)
	    return;
	killPg(pid);
	pid = -1;
    }
}
