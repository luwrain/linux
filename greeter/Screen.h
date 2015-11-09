
#ifndef GREETER_SCREEN_H
#define GREETER_SCREEN_H

class Screen
{
public:
  virtual ~Screen() {close();}

public:

  void init()
  {
    initscr();
    //    init_pair(1, COLOR_WHITE, COLOR_BLACK);
    //  start_color();
    curs_set(FALSE);
    noecho();
    cbreak();
    //    keypad(stdscr, TRUE);
    ::clear();
  }

  void close()
  {
    endwin();
  }

  void begin()
  {
    ::clear();
  }

  void end()
  {
    ::refresh();
  }

  void put(int x, int y, const std::string& str)
  {
    mvprintw(y, x, str.c_str());
  }

  int getWidth()
  {
    int x, y;
    getmaxyx(stdscr, y, x);
    return x;
  }

  int getHeight()
  {
    int x, y;
    getmaxyx(stdscr, y, x);
    return y;
  }


};

#endif //GREETER_SCREEN_H
