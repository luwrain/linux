/*
 * Taken from JPty - A small PTY interface for Java.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.luwrain.linux;

//import com.pty4j.unix.PtyHelpers;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
//import jtermios.JTermios;

public final class Syscalls
{
    private interface C_lib extends Library
    {
	int kill(int pid, int signal);
	int waitpid(int pid, int[] stat, int options);
	int sigprocmask(int how, IntByReference set, IntByReference oldset);
	String strerror(int errno);
	int grantpt(int fdm);
	int unlockpt(int fdm);
	int close(int fd);
	String ptsname(int fd);
	int open(String pts_name, int o_rdwr);
	int killpg(int pid, int sig);
	int fork();
	int setsid();
	int getpid();
	int setpgid(int pid, int pgid);
	void dup2(int fd, int fileno);
	int getppid();
	void unsetenv(String s);
	void chdir(String dirpath);
    }

    public interface Linux_Util_lib extends Library
    {
	int login_tty(int fd);
    }

    private static final C_lib m_Clib = Native.loadLibrary("c", C_lib.class);
  private static final Linux_Util_lib m_Utillib = Native.loadLibrary("util", Linux_Util_lib.class);

  public int kill(int pid, int signal)
    {
    return m_Clib.kill(pid, signal);
  }

  public int waitpid(int pid, int[] stat, int options)
    {
    return m_Clib.waitpid(pid, stat, options);
  }

  public int sigprocmask(int how, IntByReference set, IntByReference oldset)
    {
    return m_Clib.sigprocmask(how, set, oldset);
  }

  public String strerror(int errno)
    {
    return m_Clib.strerror(errno);
  }

  public int grantpt(int fd)
    {
    return m_Clib.grantpt(fd);
  }

  public int unlockpt(int fd)
    {
    return m_Clib.unlockpt(fd);
  }

  public int close(int fd)
    {
    return m_Clib.close(fd);
  }

  public String ptsname(int fd)
    {
    return m_Clib.ptsname(fd);
  }

  public int killpg(int pid, int sig)
    {
    return m_Clib.killpg(pid, sig);
  }

  public int fork()
    {
    return m_Clib.fork();
  }

  public int setsid()
    {
    return m_Clib.setsid();
  }

  public int getpid()
    {
    return m_Clib.getpid();
  }

  public int setpgid(int pid, int pgid)
    {
    return m_Clib.setpgid(pid, pgid);
  }

  public void dup2(int fds, int fileno)
    {
    m_Clib.dup2(fds, fileno);
  }

  public int getppid()
    {
    return m_Clib.getppid();
  }

  public void unsetenv(String s)
    {
    m_Clib.unsetenv(s);
  }

  public int login_tty(int fd)
    {
    return m_Utillib.login_tty(fd);
  }

  public void chdir(String dirpath)
    {
    m_Clib.chdir(dirpath);
  }
}
