/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.linux.fileops;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

abstract class Base implements FilesOperation
{
    private final Listener listener;
    private final String name;

    private boolean finished = false;
    private boolean finishingAccepted = false ;
    private Result result = new Result();
    protected boolean interrupted = false;

    Base(Listener listener, String name)
    {
	NullCheck.notNull(listener, "listener");
	NullCheck.notEmpty(name, "name");
	this.listener = listener;
	this.name = name;
    }

    abstract protected Result work() throws IOException;

    @Override public void run()
    {
	try {
	    try {
		result = work();
	    }
	    catch (Exception e)
	    {
		Log.error("linux", name + ":" + e.getClass().getName() + ":" + e.getMessage());
		result = new Result(Result.Type.EXCEPTION, e);
	    }
	}
	finally {
	    finished = true;
	    listener.onOperationProgress(this);
	}
    }

    @Override public synchronized void interrupt()
    {
	interrupted = true;
    }

    @Override public synchronized  String getOperationName()
    {
	return name;
    }

    @Override public synchronized  boolean isFinished()
    {
	return finished;
    }

    @Override public synchronized Result getResult()
    {
	return result;
    }

    @Override public boolean finishingAccepted()
    {
	if (finishingAccepted)
	    return true;
	finishingAccepted = true;
	return false;
    }

    protected boolean isDirectory(Path path, boolean followSymlinks) throws IOException
    {
	NullCheck.notNull(path, "path");
	    if (followSymlinks)
		return Files.isDirectory(path); else
		return Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS);
    }

    protected Path[] getDirContent(final Path path) throws IOException
    {
	final LinkedList<Path> res = new LinkedList<Path>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		for (Path p : directoryStream) 
		    res.add(p);
	    } 
	return res.toArray(new Path[res.size()]);
    }

    protected boolean isRegularFile(Path path, boolean followSymlinks) throws IOException
    {
	NullCheck.notNull(path, "path");
	    if (followSymlinks)
		return Files.isRegularFile(path); else
		return Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS);
    }

    protected boolean exists(Path path, boolean followSymlinks) throws IOException
    {
	NullCheck.notNull(path, "path");
	    if (followSymlinks)
		return Files.exists(path); else
		return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
    }

    protected Result deleteFileOrDir(Path p) throws IOException
    {
	NullCheck.notNull(p, "p");
	if (interrupted)
	    return new Result(Result.Type.INTERRUPTED);
	if (isDirectory(p, false))
	{
	    final Path[] content = getDirContent(p);
	    for(Path pp: content)
	    {
		final Result res = deleteFileOrDir(pp);
		if (res.getType() != Result.Type.OK)
		    return res;
	    }
	}
	Files.delete(p);
	return new Result();
    }

    protected void status(String message)
    {
	Log.debug("linux", message);
    }

    protected ConfirmationChoices confirmOverwrite(Path path)
    {
	NullCheck.notNull(path, "path");
	return listener.confirmOverwrite(path);
    }

    protected void onProgress(FilesOperation op)
    {
	NullCheck.notNull(op, "op");
	listener.onOperationProgress(op);
    }

    static long getTotalSize(Path p) throws IOException
    {
	NullCheck.notNull(p, "p");
	if (Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS))
	    return Files.size(p);
	if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS))
	    return 0;
	long res = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(p)) {
		for (Path pp : directoryStream) 
		    res += getTotalSize(pp);
	    } 
	return res;
    }
}
