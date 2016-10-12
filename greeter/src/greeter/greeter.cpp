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

#include"greeter.h"
#include"Instance.h"
#include"InitialFrame.h"

void printHelp()
{
  std::cout << "greeter: a greeting application for LUWRAIN LiveCD\n\n"
    "Usage:\n"
    "\tgreeter VOICE_CMD SPEECH_CMD CONSOLE_CMD FRAME0_FILE [FRAME1_FILE [...]]\n\n"
    "\tSPEECH_CMD is a command to speak where %s is substituted with the text to be spoken.\n";
}

bool readTextFile(const std::string& fileName, StringVector& lines)
{
  std::ifstream f(fileName.c_str());
  if (!f.is_open())
    return 0;
  lines.clear();
  std::string line;
  while(std::getline(f, line))
    lines.push_back(line);
  return 1;
}

void launch(const std::string& cmd)
{
  const pid_t pid = fork();
  if (pid == (pid_t)0)
    {
      execlp("/bin/bash", "/bin/bash", "-c", cmd.c_str(), NULL);
      exit(EXIT_FAILURE);
    }
  waitpid(pid, NULL, 0);
}

int main(int argc, char* argv[])
{
  setlocale(LC_ALL, "");
  if (argc < 5)
    {
      printHelp();
    return EXIT_FAILURE;
    }
  for(int i = 1;i < argc;++i)
    if (std::string(argv[i]) == "--help"  || std::string(argv[i]) == "-h")
      {
	printHelp();
	return EXIT_SUCCESS;
      }
  const std::string voiceCmd = argv[1];
  const std::string speechCmd = argv[2];
  const std::string consoleCmd = argv[3];

  Instance instance;
  instance.screen.init();
  StringVector lines;
  FrameVector frames;
  readTextFile(argv[4], lines);
  frames.push_back(new InitialFrame(instance, voiceCmd, lines));

  frames[0]->drawScreen();
  frames[0]->introduce();

  std::ofstream s("keys.txt");
  while(1)
    {
      const int c = getch();
      s << c << std::endl;

      //265 f1
      if (c == 266)//F2
	{
	  instance.screen.close();
	  instance.player.stop();
	  launch(consoleCmd);
	  return EXIT_SUCCESS;
	}
      if (c == 10 || c == 32)
	break;
    }

  return EXIT_SUCCESS;
}
