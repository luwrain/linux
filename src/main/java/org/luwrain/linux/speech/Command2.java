
package org.luwrain.linux.speech;

import java.util.*;
import java.io.*;
import javax.sound.sampled.AudioFormat;

import org.luwrain.speech.*;
import org.luwrain.core.*;

public class Command2 implements Channel
{
    private interface RegOptions
    {
	String getName();
	String getCommand();
    }

    private String name;
    private String command;

    @Override public boolean init(String[] cmdLine, Registry registry, String path)
    {
	try {
	    final RegOptions options = RegistryProxy.create(registry, path, RegOptions.class);
	    name = options.getName();
	    command = options.getCommand();//FIXME:With default value
	    return true;
	}
	catch (Exception e)
	{
	    Log.error("linux", "unexpected error while initializing a command speech channel:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    @Override public Voice[] getVoices()
    {
	return new Voice[0];
    }

    @Override public String getChannelName()
    {
	return name;
    }

    @Override public Set<Features>  getFeatures()
    {
	return null;
	//	final EnumSet<Features> res = EnumSet<Features>.noneOf(Features.class);
	//	res.add(Features.CAN_SYNTH_TO_STREAM);
	//	return res;
    }

@Override public boolean isDefault()
    {
	return false;
    }

    @Override public void setDefaultVoice(String name)
    {
    }

    @Override public void setDefaultPitch(int value)
    {
    }

    @Override public void setDefaultRate(int value)
    {
    }

    @Override public void speak(String text)
    {
    }

    @Override public void speak(String text, int relPitch, int relRate)
    {
    }

    @Override public void speak(String text, Listener listener, int relPitch, int relRate)
    {
    }

    @Override public boolean synth(String text, 
				   int pitch, int rate, 
				   AudioFormat format, OutputStream stream)
    {
	System.out.println("synth " + text);
	try {
	    final Process p = new ProcessBuilder("/bin/bash", "-c", command).start();
	    final Writer w = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
	    w.write(text);
	    w.close();

	    final InputStream in = p.getInputStream();
	    final byte[] buf = new byte[2048];
	    int length;
	    while ( (length = in.read(buf)) >= 0 )
		stream.write(buf, 0, length);

	    p.waitFor();
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	}
	catch (IOException e)
	{
	    Log.error("linux", "unable to launch a speech synthesizer  of the channel \'" + name + "\':" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    @Override public AudioFormat[] getSynthSupportedFormats()
    {
	return new AudioFormat[]{
	    new AudioFormat(
			    16000, //Sample rate
			    16, //Sample size in bits
			    1, //Channels
			    false, //Signed
			    false //BigEndian
)};
    }

    @Override public void silence()
    {
    }
}
