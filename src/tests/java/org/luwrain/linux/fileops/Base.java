
package org.luwrain.linux.fileops;

import java.io.*;
import java.util.*;

class Base
{
    static final File TMP_DIR = new File("/tmp/lwr-linux-testing-data");

    final Random rand = new Random();

    void writeRandFile(File f, int len) throws IOException
    {
	if (len < 0)
	    throw new IllegalArgumentException("len (" + len + ") may not be negative");
	final FileOutputStream f = new FileOutputStream(f);
	final byte[] buf = new byte[2048];
	int written = 0;
	while (written < len)
	{
	    final int chunk = (len - written >= buf.length)?buf.length:len - written;
	    fillBuf(buf, chunk);
	    f.write(buf, 0, chunk);
	    written += chunk;
	}
	f.flush();
	f.close();
    }

    private void fillBuf(byte[] buf, int len)
    {
	for(int i = 0;i < len;++i)
	{
	    byte b = (byte)(rand.nextInt(256) - 128);
	    buf[i] = b;
	}
    }
}
