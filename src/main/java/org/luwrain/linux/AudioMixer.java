
package org.luwrain.linux;

import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;

class AudioMixer implements org.luwrain.hardware.AudioMixer
{
    private Path scriptsDir;

    AudioMixer(Path scriptsDir)
    {
	NullCheck.notNull(scriptsDir, "scriptsDir");
	this.scriptsDir = scriptsDir;
    }

    @Override public int getMasterVolume()
    {
	try {
	    final Process p = new ProcessBuilder("sudo", scriptsDir.resolve("lwr-master-volume-get").toString()).start();
	    p.getOutputStream().close();
	    final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    final String value = r.readLine();
	    p.waitFor();
	    return Integer.parseInt(value);
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return 50;
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return 50;
	}
	catch(NumberFormatException e)
	{
	    e.printStackTrace();
	    return 50;
	}
    }

    @Override public void setMasterVolume(int value)
    {
	int v = value;
	if (v > 100)
	    v = 100;
	if (v < 0)
	    v = 0;
	try {
	    final Process p = new ProcessBuilder("sudo", scriptsDir.resolve("lwr-master-volume-set").toString(), "" + v).start();
	    p.waitFor();
	    if (p.exitValue() != 0)
		Log.error("linux", "lwr-master-volume-set " + v + " failed");
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
    }
}
