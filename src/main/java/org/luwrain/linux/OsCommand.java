
package org.luwrain.linux;

import java.util.*;
import java.util.concurrent.*;
import java.nio.file.*;
import java.io.*;

import org.luwrain.core.*;

class OsCommand implements org.luwrain.core.OsCommand
{
    private final Executor executor = Executors.newSingleThreadExecutor();

    private FutureTask task = null;
    private Output output = null;
    private Listener listener;
    private final LinkedList<String> lines = new LinkedList<String>();

    OsCommand(Output output, Listener listener,
	      List<String> cmd, String dir)
    {
	NullCheck.notNull(cmd, "cmd");
	NullCheck.notNull(dir, "dir");
	this.output = output;
	this.listener = listener;
	run(cmd, dir);
    }

    private void run(List<String> cmd, String dir)
    {
	NullCheck.notNull(cmd, "cmd");
	NullCheck.notNull(dir, "dir");
	task = new FutureTask(()->{
		try {
		    final ProcessBuilder builder = new ProcessBuilder(cmd);
		    if (!dir.isEmpty())
		    builder.directory(new File(dir));
		    builder.redirectErrorStream(true);
		    final Process p = builder.start();
		    p.getOutputStream().close();
		    final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    String line = null;
		    while ( (line = r.readLine()) != null )
		    {
			lines.add(line);
			if (output != null)
			    output.onNewOsCommandLine(line);
		    }
		    p.getInputStream().close();
		    p.waitFor();
		    if (listener != null)
			listener.onOsCommandFinish(p.exitValue(), getOutput());
		    return;
		}
		catch(InterruptedException e)
		{
		    Thread.currentThread().interrupt();
		    return;
		}
		catch(IOException e)
		{
		    e.printStackTrace();
		    return;
		}
	    }, null);
	executor.execute(task);
    }

    @Override public boolean isRunning()
    {
	return task == null || task.isDone();
    }

    @Override public void cancel()
    {
	if (task != null)
	    task.cancel(true);
    }

    @Override public String[] getOutput()
    {
	return lines.toArray(new String[lines.size()]);
    }
}
