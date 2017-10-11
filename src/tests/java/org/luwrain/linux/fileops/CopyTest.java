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

import org.junit.*;

import org.luwrain.core.*;

public class CopyTest extends Assert
{
    @Test public void singleFile() throws IOException
    {
	final File srcFile = createSingleFileTestingDir("testing.dat", 5123456);
	final File destDir = createDestDir();
    }

    private File createSingleFileTestingDir(String fileName, int len) throws IOException
    {
	Base.TMP_DIR.mkdir();
	final File srcDir = new File(Base.TMP_DIR, "src");
	srcDir.mkdir();
	final File file = new File(srcDir, fileName);
	final Base base = new Base();
	base.writeRandFile(file, len);
	return file;
    }

    private File createDestDir() throws IOException
    {
	Base.TMP_DIR.mkdir();
	final File destDir = new File(Base.TMP_DIR, "dest");
	destDir.mkdir();
	return destDir;
    }
}
