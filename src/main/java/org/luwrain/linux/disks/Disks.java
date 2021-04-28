
package org.luwrain.linux.disks;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;
import org.luwrain.linux.*;
import org.luwrain.util.*;

public final class Disks implements DisksPopup.Disks
{
    static private final String LOG_COMPONENT = Linux.LOG_COMPONENT;
    static private final File FILE_MOUNTS = new File("/proc/mounts");
    static private final Pattern PAT_MOUNTS = Pattern.compile("^(\\S+)\\s(\\S+)\\s(\\S+)\\s(.*)$", Pattern.CASE_INSENSITIVE);
    static private final List<String> FS_SKIP = Arrays.asList("autofs", "cgroup", "configfs", "debugfs", "devfs", "devpts", "devtmpfs", "fusectl", "hugetlbfs", "mqueue", "nsfs", "proc", "pstore", "securityfs", "sysctl", "tmpfs");

    @Override public Disk[] getDisks()
    {
	final List<Disk> res = new ArrayList();
	try {
	    final String[] mounts = FileUtils.readTextFileMultipleStrings(FILE_MOUNTS, "UTF-8", System.lineSeparator());
	    for(String line: mounts) {
		final Matcher m = PAT_MOUNTS.matcher(line);
		if (!m.find())
		    continue;
		if (FS_SKIP.contains(m.group(3)))
		    continue;
		res.add(new Disk(m.group(2)));
	    }
	}
	catch(Exception e)
	{
	    Log.error(LOG_COMPONENT, "unable to read " + FILE_MOUNTS.getPath() + ": " + e.getClass().getName() + ": " + e.getMessage());
	}
	final Disk[] r = res.toArray(new Disk[res.size()]);
	Arrays.sort(r, (d1, d2)->{
		if (d2.toString().equals("/"))
		return -1;
		if (d1.toString().equals("/"))
		return 1;

				if (d2.toString().equals("/home"))
		return -1;
		if (d1.toString().equals("/home"))
		return 1;


		
				return d1.toString().compareTo(d2.toString());

				
	    });
	return r;
    }
}
