

package org.luwrain.linux;

class ProcessGroup
{
    public static native int execProcessGroup(String cmd, String input);
    public static native int getStatus(int pid);
    public static native void killPg(int pid);
}
