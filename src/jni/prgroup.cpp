
#include<iostream>
#include<stdlib.h>
#include<string.h>
//#include<string.h>
#include<errno.h>
#include<sys/types.h>
#include<unistd.h>
#include<fcntl.h>
//#include <poll.h>
#include<signal.h>
#include<sys/wait.h>

#include"org_luwrain_linux_ProcessGroup.h"

#define IO_BUF_SIZE 2048
#define SHELL "/bin/sh"

JNIEXPORT jint JNICALL Java_org_luwrain_linux_ProcessGroup_execProcessGroup(JNIEnv* env,
									      jclass cl,
									      jstring cmd,
									      jstring input)
{
const char* cmdStr = env->GetStringUTFChars(cmd, NULL);
  const char* inputStr = env->GetStringUTFChars(input, NULL);

  int pp[2];
  //  const size_t inputLen = strlen(inputStr);
  if (pipe(pp) == -1)
    {
      perror("pipe()");
      return -1;
    }
  const pid_t pid = fork();
  if (pid == (pid_t)-1)
    {
      perror("fork()");
      return -1;
    }
  if (pid == (pid_t)0)
    {
      int fd = open("/dev/null", O_WRONLY);
      if (fd == -1)
	exit(EXIT_FAILURE);
      setpgrp();
      close(pp[1]);/*Closing pipe input end*/
      dup2(pp[0], STDIN_FILENO);
      dup2(fd, STDOUT_FILENO);
      dup2(fd, STDERR_FILENO);
      execlp(SHELL, "/bin/sh", "-c", cmdStr, NULL);
      exit(EXIT_FAILURE);
    }
  close(pp[0]);
  write(pp[1], inputStr, strlen(inputStr));
  write(pp[1], "\n", 1);
  close(pp[1]);
  return pid;
}

JNIEXPORT jint JNICALL Java_org_luwrain_linux_ProcessGroup_getStatus(JNIEnv* env, 
								     jclass,
								     jint pid)
{
  if (pid <= 0)
    return 0;
  while (1)
    {
      const pid_t p = waitpid(-1 * pid, NULL, WNOHANG);
      if (p == 0)
	return 1;
      if (p > 0)
	continue;
      const pid_t pp = waitpid(pid, NULL, WNOHANG);
      return pp == 0?1:0;
    }
return 0;
}

JNIEXPORT void JNICALL Java_org_luwrain_linux_ProcessGroup_killPg(JNIEnv* env,
								  jclass,
								  jint pid)
{
  if (pid <= 0)
    return;
  kill(pid, SIGKILL);
  killpg(pid, SIGKILL);
  waitpid(pid, NULL, 0);
  while(waitpid(-1 * pid, NULL, 0) >= 0);
}
