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

package org.luwrain.linux;

import java.util.*;
import java.io.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.linux.disks.*;

public class Extension extends org.luwrain.core.extensions.EmptyExtension
{
    static private final String PREFIX_INPUT_POINTER = "--linux-input-pointer=";
    static private final String PREFIX_INPUT_FIFO = "--linux-input-fifo=";

    static private Linux linux = null;
    private Scripts scripts = null;
    private PointerInputListening[] pointerInputs = null;
    private FifoInputListening[] fifoInputs = null;

    @Override public String init(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.scripts = new Scripts(luwrain);
	final CmdLine cmdLine = luwrain.getCmdLine();
	final List<PointerInputListening> inputs = new LinkedList();
	final List<FifoInputListening> fifos = new LinkedList();
	for(String s: cmdLine.getArgs(PREFIX_INPUT_POINTER))
	    inputs.add(new PointerInputListening(luwrain, s));
	for(String s: cmdLine.getArgs(PREFIX_INPUT_FIFO))
	    fifos.add(new FifoInputListening(luwrain, linux, s));
	for(PointerInputListening l: inputs)
	    l.run();
	for(FifoInputListening l: fifos)
	    l.run();
	this.pointerInputs = inputs.toArray(new PointerInputListening[inputs.size()]);
	this.fifoInputs = fifos.toArray(new FifoInputListening[fifos.size()]);
	return null;
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[]{

	    	    new Command() {
		@Override public String getName()
		{
		    return "mount-new-disk";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Disk disk = linux.getNewlyAvailableDisk();
		    if (disk == null)
		    {
			luwrain.message("Подключение новых дисков не производилось", Luwrain.MessageType.ERROR);//FIXME:
			return;
		    }
		    final Mounting mounting = new Mounting(luwrain, new DefaultMountPointConstructor());
		    final File[] res = mounting.mountAll(disk);
		    if (res .length == 0)
		    {
			luwrain.message("Невозможно подключить новый диск", Luwrain.MessageType.ERROR);
			return;
		    }
		    luwrain.launchApp("commander", new String[]{res[0].getAbsolutePath()});
		}
	    },


	    new Command() {
		@Override public String getName()
		{
		    return "suspend";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    scripts.runSync(Scripts.ID.SUSPEND, true);
		}
	    },

	    new Command() {
		@Override public String getName()
		{
		    return "reboot";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    scripts.runSync(Scripts.ID.REBOOT, true);
		}
	    },

	    new Command() {
		@Override public String getName()
		{
		    return "shutdown";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    scripts.runSync(Scripts.ID.SHUTDOWN, true);
		}
	    },

	    new Command() {
		@Override public String getName()
		{
		    return "term";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    final String currentDir = luwrain.getActiveAreaAttr(Luwrain.AreaAttr.DIRECTORY);
		    if (currentDir != null && !currentDir.isEmpty())
			luwrain.launchApp("term", new String[]{currentDir}); else
			luwrain.launchApp("term", new String[]{luwrain.getFileProperty("luwrain.dir.userhome").getAbsolutePath()});
		}
	    },

	    new Command() {
		@Override public String getName()
		{
		    return "man";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    luwrain.launchApp("man");
		}
	    },

	    new Command() {
		@Override public String getName()
		{
		    return "wifi";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    luwrain.launchApp("wifi");
		}
	    },

	    new Command() {
		@Override public String getName()
		{
		    return "install";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    luwrain.launchApp("install");
		}
	    },

	};
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final List<ExtensionObject> res = new LinkedList();
	if (scripts.exists("webcam-take"))
	res.add(new ScriptsCommandLineTool(luwrain, "webcam-take"));

	res.add(new SystemProperties.Battery(luwrain));

	res.add(new Shortcut(){
		@Override public String getExtObjName()
		{
		    return "man";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    NullCheck.notNull(args, "args");
		    return new Application[]{new org.luwrain.app.man.App()};
		}
	    });
	
	res.add(new Shortcut(){
		@Override public String getExtObjName()
		{
		    return "install";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    NullCheck.notNull(args, "args");
		    return new Application[]{new org.luwrain.app.install.App()};
		}
	    });

	res.add(new Shortcut(){
		@Override public String getExtObjName()
		{
		    return "term";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    NullCheck.notNull(args, "args");
		    if (args.length == 1)
			return new Application[]{new org.luwrain.app.term.TermApp(args[0])};
		    return new Application[]{new org.luwrain.app.term.TermApp("/")};
		}
	    });

	res.add(new Shortcut(){
		@Override public String getExtObjName()
		{
		    return "wifi";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    NullCheck.notNull(args, "args");
		    return new Application[]{new org.luwrain.app.wifi.App(linux.getWifiConnections())};
		}
	    });

	return res.toArray(new ExtensionObject[res.size()]);
    }

    static void setLinux(Linux newLinux)
    {
	if (newLinux == null)
	    throw new NullPointerException("newLinux may not be null");
	if (linux != null)
	    throw new RuntimeException("linux object instance is already set");
	linux = newLinux;
	    }
}



