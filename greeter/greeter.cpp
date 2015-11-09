
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
  std::cout << instance.screen.getWidth() << std::endl;
  std::cout << instance.screen.getHeight() << std::endl;
  return 0;
  FrameVector frames;
  frames.push_back(new InitialFrame(instance, voiceCmd));

  return EXIT_SUCCESS;
}
