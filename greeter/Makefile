

CXXFLAGS=-O2
FLAGS=-std=c++11 -Wall -pedantic -fno-rtti -Wno-unused-but-set-variable $(CXXFLAGS)
LIBS=-lncursesw
OBJS=greeter.o

all: greeter

clean:
	/bin/rm -f greeter *.o

greeter.o: greeter.cpp *.h
	g++ -c $(FLAGS) greeter.cpp

greeter: $(OBJS)
	g++ -ogreeter $(FLAGS) $(OBJS) $(LIBS)


