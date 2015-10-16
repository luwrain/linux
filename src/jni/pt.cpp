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
#include<sys/ioctl.h>
#include<fcntl.h>
#include <poll.h>
#include"org_luwrain_linux_term_PT.h"

#include<iostream>

#define IO_BUF_SIZE 2048
#define SHELL "/bin/sh"

JNIEXPORT jint JNICALL Java_org_luwrain_linux_term_PT_createImpl(JNIEnv*, jclass)
{
  const int res = posix_openpt(O_RDWR);
  if (res < 0)
    return res;
  grantpt(res);
  unlockpt(res);
  return res;
}

JNIEXPORT jint JNICALL Java_org_luwrain_linux_term_PT_launchImpl(JNIEnv *env, jclass, jint pty, jstring cmd, jstring dir)
{
  const char* ptyName = ptsname(pty);
  if (ptyName == NULL)
    return -1;
  const char* cmdTr = env->GetStringUTFChars(cmd, NULL);
  const char* dirTr = env->GetStringUTFChars(dir, NULL);
  const int slaveFd = open(ptyName, O_RDWR);
  if (slaveFd < 0)
    return slaveFd;
  const pid_t pid = fork();
  if (pid < (pid_t)0)
    return -1;
  if (pid == (pid_t)0)
    {
      close(pty);
      close(0);
      close(1);
      close(2);
      setenv("TERM", "linux", 1);
      dup(slaveFd);
      dup(slaveFd);
      dup(slaveFd);
      //close(slaveFd);
      //      const std::string devName = ttyname(0);
      //      setpgrp();
      setsid();
      ioctl(0, TIOCSCTTY, 1);
      chdir(dirTr);
      //      if (execlp(SHELL, SHELL, "-c", cmdTr != NULL?cmdTr:"", NULL) == -1)
      if (execlp("/bin/bash", "/bin/bash", "-i", NULL) == -1)
	exit(EXIT_FAILURE);
    }
  close(slaveFd);
  return pid;
}

/*
Returns:
null - descriptor is closed
an empty array - no data, should wait more
array with data - there is some data, maybe there is more, should try once again
*/
JNIEXPORT jbyteArray JNICALL Java_org_luwrain_linux_term_PT_readImpl(JNIEnv *env, jclass, jint fd)
{
  //  std::cout << "collect (" << fd << ")" << std::endl;
  struct pollfd pollFd;
  pollFd.fd = fd;
  //  std::cout << "pollFd.fd=" << pollFd.fd << std::endl;
  pollFd.events = POLLIN;
  const int pollRes = poll(&pollFd, 1, 0);
  //  std::cout << "pollRes=" << pollRes << std::endl;
  if (pollRes < 0)
    {
      perror("poll(pt):");
	return NULL;
    }
  if (pollRes == 0)
    return env->NewByteArray(0);//No data, must wait more
  if (pollFd.revents & POLLHUP)
    return NULL;//Descriptor is closed, we should stop reading
  if (pollFd.revents & POLLIN )
    {
      //      std::cout << "POLLIN" << std::endl;
      char buf[IO_BUF_SIZE];
      const int readCount = read(fd, buf, sizeof(buf));
      //      std::cout << "readCount=" << readCount << std::endl;
      if (readCount < 0)
	{
	  perror("read(pt):");
	  return NULL;//Something wrong with the descriptor, it is better to interrupt
	}
      if (readCount == 0)
	return NULL;//The descriptor is closed
      const jbyteArray res = env->NewByteArray(readCount);
      if (res == NULL)
	return NULL;//No memory, we must stop
      jbyte buf2[IO_BUF_SIZE];
      for (int i = 0;i < readCount;++i)
	buf2[i] = buf[i];
      env->SetByteArrayRegion(res, 0, readCount, buf2);
      return res;
    }
  //Actually should never be here;
return env->NewByteArray(0);
}

JNIEXPORT jint JNICALL Java_org_luwrain_linux_term_PT_writeImpl(JNIEnv* env, jclass,
								 jint fd, jbyteArray data)
{
  const int size = env->GetArrayLength(data);
  const int effectiveSize = size < IO_BUF_SIZE?size:IO_BUF_SIZE;
  jbyte jbuf[IO_BUF_SIZE];
  env->GetByteArrayRegion(data, 0, effectiveSize, jbuf);                                                         
  char buf[IO_BUF_SIZE];
  for(int i = 0;i < effectiveSize;++i)
    buf[i] = jbuf[i];
  //FIXME:Check for unblocking;
  return write(fd, buf, effectiveSize); 
}

JNIEXPORT void JNICALL Java_org_luwrain_linux_term_PT_closeImpl(JNIEnv *, jclass, jint fd)
{
  close(fd);
}

JNIEXPORT jstring JNICALL Java_org_luwrain_linux_term_PT_errnoString(JNIEnv *env, jclass)
{
  return         env->NewStringUTF(strerror(errno));
}

