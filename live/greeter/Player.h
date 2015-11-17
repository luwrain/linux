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

#ifndef GREETER_PLAYER_H

class Player
{
public:
  virtual ~Player() {stop();}

public:
  bool run(const std::string& cmd)
  {
    const pid_t p = fork();
    if (p == (pid_t)-1)
      return 0;
    if (p != (pid_t)0)
      {
	pid = p;
	return 1;
      }
    setpgrp();
    const int fd = open("/dev/null", O_RDWR);
      if (fd >= 0)
	{
      dup2(fd, STDIN_FILENO);
      dup2(fd, STDOUT_FILENO);
      dup2(fd, STDERR_FILENO);
	}
      execlp("/bin/bash", "/bin/bash", "-c", cmd.c_str(), NULL);
      return 1;
  }

  void stop()
  {
    killpg(pid, SIGINT);
    killpg(pid, SIGKILL);
    while(waitpid(-1 * pid, NULL, 0) >= 0);
  }

private:
  pid_t pid;
};

#define GREETER_PLAYER_H

#endif //GREETER_PLAYER_H
