

#include<stdlib.h>
#include<string.h>
#include<errno.h>
#include<sys/types.h>
#include<unistd.h>
#include<fcntl.h>
#include"org_luwrain_os_Terminal.h"

#include<iostream>

#define SHELL "/bin/sh"

JNIEXPORT jint JNICALL Java_org_luwrain_os_Terminal_openPty(JNIEnv *, jclass)
{
  const int res = posix_openpt(O_RDWR);
  if (res < 0)
    return res;
  grantpt(res);
  unlockpt(res);
  return res;
}

JNIEXPORT jint JNICALL Java_org_luwrain_os_Terminal_exec(JNIEnv *env, jclass, jint pty, jstring cmd)
{
  const char* ptyName = ptsname(pty);
  if (ptyName == NULL)
    return -1;
  const char* cmdTr = env->GetStringUTFChars(cmd, NULL);
  const pid_t pid = fork();
  if (pid < (pid_t)0)
    return -1;
  if (pid == (pid_t)0)
    {
      const int fd = open(ptyName, O_WRONLY);
      if (fd == -1)
	exit(EXIT_FAILURE);
      setpgrp();
      dup2(fd, STDIN_FILENO);
      dup2(fd, STDOUT_FILENO);
      dup2(fd, STDERR_FILENO);
      if (execlp(SHELL, SHELL, "-c", cmdTr != NULL?cmdTr:"", NULL) == -1)
	exit(EXIT_FAILURE);
    }
  return pid;
}

JNIEXPORT void JNICALL Java_org_luwrain_os_Terminal_close (JNIEnv *, jclass, jint fd)
{
  close(fd);
}

JNIEXPORT jstring JNICALL Java_org_luwrain_os_Terminal_errnoString(JNIEnv *env, jclass)
{
  return         env->NewStringUTF(strerror(errno));
}

JNIEXPORT jstring JNICALL Java_org_luwrain_os_Terminal_collect(JNIEnv *env, jclass, jint pty)
{
  char buf[2048];
  read(pty, buf, sizeof(buf));
  std::string output(buf);
  return         env->NewStringUTF(output.c_str());
}
