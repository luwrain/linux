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

package org.luwrain.linux;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.script.core.*;
import org.luwrain .script2.*;

public final class Extension extends EmptyExtension
{
    static private final String LOG_COMPONENT = Linux.LOG_COMPONENT;

    static private final String PREFIX_INPUT_POINTER = "--linux-input-pointer=";
    static private final String PREFIX_INPUT_FIFO = "--linux-input-fifo=";

    static private Linux linux = null;

    private ScriptCore scriptCore = null;
    private TermInfo termInfo = null;
    //    private Scripts scripts = null;
    private PointerInputListening[] pointerInputs = null;
    private FifoInputListening[] fifoInputs = null;

    @Override public String init(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	loadScriptCore(luwrain);
	//	this.scripts = new Scripts(luwrain);
	final CmdLine cmdLine = luwrain.getCmdLine();
	try {
	    this.termInfo = new TermInfo();
	    this.termInfo.read();
	    Log.debug(LOG_COMPONENT, "terminfo loaded for '" + termInfo.getTermName() + "'");
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to load terminfo: " + e.getClass().getName() + ":" + e.getMessage());
	    this.termInfo = null;
	}
	final List<PointerInputListening> inputs = new ArrayList<>();
	final List<FifoInputListening> fifos = new ArrayList<>();
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

    private void loadScriptCore(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.scriptCore = new ScriptCore(luwrain, new org.luwrain.linux.script.Bindings(luwrain));
	final File scriptsDir = luwrain.getFileProperty(Luwrain.PROP_DIR_JS);
	final File[] scripts = scriptsDir.listFiles();
	if (scripts == null)
	    return;
	for(File f: scripts)
	    if (f != null && f.getName().startsWith("linux-"))
	    {
		Log.debug(LOG_COMPONENT, "loading " + f.getAbsolutePath());
		try {
		    scriptCore.load(f);
		}
		catch(Throwable e)
		{
		    Log.error(LOG_COMPONENT, "unable to load " + f.getAbsolutePath() + ": " + e.getClass().getName() + ": " + e.getMessage());
		    e.printStackTrace();
		}
	    }
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	final List<Command> res = new ArrayList<>();
	res.addAll(Arrays.asList(scriptCore.getCommands()));
	res.addAll(Arrays.asList(
				 new SimpleShortcutCommand("term"),
				 new SimpleShortcutCommand("man"),
				 new SimpleShortcutCommand("parted"),
				 new SimpleShortcutCommand("wifi"),
				 new SimpleShortcutCommand("install")
				 ));
	return res.toArray(new Command[res.size()]);
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final List<ExtensionObject> res = new ArrayList<>();
	//	if (scripts.exists("webcam-take"))
	    //	    res.add(new ScriptsCommandLineTool(luwrain, "webcam-take"));
	res.add(new org.luwrain.linux.lib.SysJob());
	res.add(new SimpleShortcut("man", org.luwrain.app.man.App.class));
		res.add(new SimpleShortcut("parted", org.luwrain.app.parted.App.class));
	res.add(new SimpleShortcut("install", org.luwrain.app.install.App.class));

	res.add(new Shortcut(){
		@Override public String getExtObjName()
		{
		    return "term";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    NullCheck.notNullItems(args, "args");
		    if (args.length == 1 && !args[0].isEmpty())
			return new Application[]{new org.luwrain.app.term.App(termInfo, args[0])};
		    final String dir = luwrain.getActiveAreaAttr(Luwrain.AreaAttr.DIRECTORY);
		    if (dir != null && !dir.isEmpty())
			return new Application[]{new org.luwrain.app.term.App(termInfo, dir)};
		    return new Application[]{new org.luwrain.app.term.App(termInfo)};
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
		    return new Application[]{new org.luwrain.app.wifi.App()};
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
