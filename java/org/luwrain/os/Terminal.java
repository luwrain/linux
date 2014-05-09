
package org.luwrain.os;

public class Terminal
{
    private static native int openPty();
    private static native int exec(int pty, String cmd);
    private static native void close(int fd);
    private static native String errnoString();
    private static native String collect(int pty);

    private int fd = -1;
    private int pid = -1;

    public Terminal()
    {
    }

    public void open(String shellExp) throws TerminalException
    {
	if (shellExp == null || shellExp.trim().isEmpty())
	    return;
	fd = openPty(); 
	if (fd < 0)
	    throw new TerminalException("open:" + errnoString());
	pid = exec(fd, shellExp);
	if (pid < 0)
	{
	    final String message = errnoString();
	    close(fd);
	    fd = -1;
	    pid = -1;
	    throw new TerminalException(message);
	}
    }
}
