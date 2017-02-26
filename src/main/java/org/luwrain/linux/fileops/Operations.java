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

import java.io.*;
import java.nio.file.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

public class Operations implements FilesOperations
{
    @Override public FilesOperation copy(FilesOperation.Listener listener, String name,
					 File[] whatToCopy, File copyTo)
    {
	return new Copy(listener, name, toPath(whatToCopy), copyTo.toPath());
    }

    @Override public FilesOperation move(FilesOperation.Listener listener, String name,
					 File[] whatToMove, File moveTo)
    {
	NullCheck.notNull(name, "name");
	NullCheck.notNullItems(whatToMove, "whatToMove");
	NullCheck.notNull(moveTo, "moveTo");
	return new Move(listener, name, toPath(whatToMove), moveTo.toPath());
    }

    @Override public FilesOperation delete(FilesOperation.Listener listener, String name,
					   File[] whatToDelete)
    {
	return new Delete(listener, name, toPath(whatToDelete));
    }

    static private Path[] toPath(File[] files)
    {
	NullCheck.notNullItems(files, "files");
	final Path[] res = new Path[files.length];
	for(int i = 0;i < files.length;++i)
	    res[i] = files[i].toPath();
	return res;
    }

    static public void main(String[] args)
    {
	if (args.length < 1)
	{
	    System.err.println("ERROR:no operation given");
	    System.exit(1);
	}

	final Operations operations = new Operations();

	//copy
	if (args[0].equals("copy"))
	{
	    if (args.length < 3)
	    {
		System.err.println("ERROR:too few arguments");
		System.exit(1);
	    }
	    final File[] whatToCopy = new File[args.length - 2];
	    for(int i = 1;i < args.length - 1;++i)
		whatToCopy[i- 1] = new File(args[i]).getAbsoluteFile();
	    final File copyTo = new File(args[args.length - 1]).getAbsoluteFile();
	    System.out.println("Copying:");
	    for(File f: whatToCopy)
		System.out.println(f.toString());
	    System.out.println("To:");
	    System.out.println(copyTo.toString());
	    final FilesOperation op = operations.copy(createListener(), "Testing copying", whatToCopy, copyTo);
	    op.run();
	    System.out.println(op.getResult().toString());
	    System.exit(0);
	}

	System.err.println("ERROR:unknown operation:" + args[0]);
	System.exit(1);
    }

    static private FilesOperation.Listener createListener()
    {
	return new FilesOperation.Listener() 
	    {
		@Override public void onOperationProgress(FilesOperation operation)
		{
		}
		@Override public FilesOperation.ConfirmationChoices confirmOverwrite(Path path)
		{
		    return FilesOperation.ConfirmationChoices.CANCEL;
		}
	};
    }
}
