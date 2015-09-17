/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

#include<stdlib.h>
#include<string.h>
#include<errno.h>
#include<sys/types.h>
#include<unistd.h>
#include<fcntl.h>
#include <poll.h>
#include"org_luwrain_linux_term_Terminal.h"

#include<iostream>

#define IO_BUF_SIZE 2048
#define SHELL "/bin/sh"

JNIEXPORT jint JNICALL Java_org_luwrain_linux_term_Terminal_openPty(JNIEnv *, jclass)
{
  const int res = posix_openpt(O_RDWR);
  if (res < 0)
    return res;
  grantpt(res);
  unlockpt(res);
  return res;
}

JNIEXPORT jint JNICALL Java_org_luwrain_linux_term_Terminal_exec(JNIEnv *env, jclass, jint pty, jstring cmd)
{
  const char* ptyName = ptsname(pty);
  if (ptyName == NULL)
    return -1;
  std::cout << "ptyName=" << ptyName << std::endl;
  open(ptyName, O_WRONLY);
  const char* cmdTr = env->GetStringUTFChars(cmd, NULL);
  std::cout << "cmdTr=" << cmdTr << std::endl;
  const pid_t pid = fork();
  if (pid < (pid_t)0)
    return -1;
  if (pid == (pid_t)0)
    {
      const int fd = open(ptyName, O_WRONLY);
      if (fd < 0)
	exit(EXIT_FAILURE);
      setpgrp();
      dup2(fd, STDIN_FILENO);
      dup2(fd, STDOUT_FILENO);
      dup2(fd, STDERR_FILENO);
      //      if (execlp(SHELL, SHELL, "-c", cmdTr != NULL?cmdTr:"", NULL) == -1)
      if (execlp("/bin/bash", "/bin/bash", "-i", NULL) == -1)
	exit(EXIT_FAILURE);
    }
  return pid;
}

JNIEXPORT void JNICALL Java_org_luwrain_linux_term_Terminal_close (JNIEnv *, jclass, jint fd)
{
  close(fd);
}

JNIEXPORT jstring JNICALL Java_org_luwrain_linux_term_Terminal_errnoString(JNIEnv *env, jclass)
{
  return         env->NewStringUTF(strerror(errno));
}

JNIEXPORT jstring JNICALL Java_org_luwrain_linux_term_Terminal_collect(JNIEnv *env, jclass, jint pty)
{
  std::cout << "collect (" << pty << ")" << std::endl;
  if (pty < 0)
    return env->NewStringUTF("");;
  std::string output;
  int readCount = 0;
  bool closed = 0;
  while(1)
    {
      struct pollfd pollFd;
      pollFd.fd = pty;
      std::cout << "pollFd.fd=" << pollFd.fd << std::endl;
      pollFd.events = POLLIN /*| POLLWRBAND*/;
      const int res = poll(&pollFd, 1, 0);
      std::cout << "res=" << res << std::endl;
      if (res < 0)
	{
	  perror("poll(pty)");
	  return env->NewStringUTF("");;
	}
      if (res == 0)
	break;

      if (pollFd.revents & POLLHUP)
	std::cout << "POLLHUP" << std::endl;
      if (pollFd.revents & POLLIN )
	{
	  std::cout << "POLLIN" << std::endl;
      char buf[IO_BUF_SIZE];
      readCount = read(pty, buf, sizeof(buf));
      std::cout << "readCount=" << readCount << std::endl;
      if (readCount < 0)
	{
	  perror("read(pty):");
	  return env->NewStringUTF("");
	}
      if (readCount == 0)
	{
	  closed = 1;
	  break;
	}
    for(int i = 0;i < readCount;++i)
      output += buf[i];
    std::cout << "output=" << output << std::endl;
	}
    }
  return         env->NewStringUTF(output.c_str());
}
