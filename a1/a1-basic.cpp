#include <iostream>
#include <cstdlib>
#include <list>
#include <string>
#include <sstream>
#include <cmath>
#include <vector>

#include <sys/time.h>
#include <unistd.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>

using namespace std;

#include "simon.h"

const int BufferSize = 10;
const int FPS = 80;
int sequenceNum = 0;
string status;
vector <int> click;

/*
 * Information to draw on the window.
 */
struct XInfo {
	Display	*display;
	int	screen;
	Window	window;
	GC	gc;

	int height;
	int width;
};

// get microseconds
unsigned long now() {
	timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000000 + tv.tv_sec;
}

/*
 * An abstract class representing displayable things.
 */
class Displayable {
public:
	virtual void paint(XInfo &xinfo) = 0;
};


/*
 * A text displayable
 */
class Text : public Displayable {
public:
	virtual void paint(XInfo& xinfo) {
		XDrawImageString( xinfo.display, xinfo.window, xinfo.gc,
				  this->x, this->y, this->s.c_str(), this->s.length() );
	}

	// constructor
	Text(int x, int y, string s): x(x), y(y), s(s) {}

private:
	int x;
	int y;
	string s;	// string to show
};

/*
 * A Button displayable
 */
class Button : public Displayable {
public:
	virtual void paint(XInfo& xinfo) {
		// create a simple graphics context
		GC gc = XCreateGC(xinfo.display, xinfo.window, 0, 0);
		int screen = DefaultScreen( xinfo.display );
		XSetForeground(xinfo.display, gc, BlackPixel(xinfo.display, screen));
		XSetBackground(xinfo.display, gc, WhitePixel(xinfo.display, screen));
		XSetFillStyle(xinfo.display, gc, FillSolid);
		XSetLineAttributes(xinfo.display, gc, 3, LineSolid, CapButt, JoinRound);

		XFillArc(xinfo.display, xinfo.window, gc, x1 - (d1/2), y1 - (d1/2), d1, d1, 0, 360 * 64);

		if(dynamic == false) {
			XSetForeground(xinfo.display, gc, WhitePixel(xinfo.display, screen));
			XFillArc(xinfo.display, xinfo.window, gc, x2 - (d2/2), y2 - (d2/2), d2, d2, 0, 360 * 64);
			XSetForeground(xinfo.display, gc, BlackPixel(xinfo.display, screen));
			XDrawImageString(
				xinfo.display,
				xinfo.window,
				xinfo.gc,
				this->x2 - d2/20,
				this->y2 + d2/10,
				this->num.c_str(),
				this->num.length()
			);
		} else {
			XSetForeground(xinfo.display, gc, WhitePixel(xinfo.display, screen));
			XDrawArc(xinfo.display, xinfo.window, gc, x2 - (d2/2), y2 - (d2/2), d2, d2, 0, 360*64);
		}
	}

	// constructor
	Button(int x1, int y1, int d1, int x2, int y2, int d2, bool dynamic, string num):
		x1(x1), y1(y1), d1(d1), x2(x2), y2(y2), d2(d2), dynamic(dynamic), num(num) {}

private:
	float x1;
	float y1;
	float d1;	// diameter 1
	float x2;
	float y2;
	float d2; // diameter 2
	bool dynamic;
	string num;	// the number in the button

	int offset() {
		return sin(now());
	}
};

vector<Displayable*> dList;

// Function to put out a message on error and exits
void error( string str ) {
	cerr << str << endl;
	exit(0);
}

/*
 * Function to repaint a display list
 */
void repaint(XInfo& xinfo) {
	vector<Displayable*>::const_iterator begin = dList.begin();
	vector<Displayable*>::const_iterator end = dList.end();
	XClearWindow(xinfo.display, xinfo.window);
	while ( begin != end ) {
		Displayable* d = *begin;
		d->paint(xinfo);
		begin++;
	}
	XFlush(xinfo.display);
}

void paintAnimation(XInfo &xinfo, int n, int index, string paintType) {
	XWindowAttributes attr;
	XGetWindowAttributes(xinfo.display, xinfo.window, &attr);

	stringstream ss1;
	ss1 << sequenceNum;
	string sn = ss1.str();

	dList.clear();
	dList.push_back(new Text(50, 50, sn));
	dList.push_back(new Text(50, 90, status));

	int init_x = xinfo.width/8;
	stringstream ss;
	string str_i;
	int x1, y1, d1, x2, y2, d2, x3, y3, d3;
	int c;
	// vector<Displayable*>::iterator it = dList.begin();
	for(int i = 0; i < n; i++) {
		x1 = (attr.width/(n+1))*(i+1);
		y1 = attr.height/2;
		d1 = 100;
		x2 = x1 + .5;
		y2 = y1 + .5;

		// convert i to type of string
		ss << (i + 1);
		str_i = ss.str();
		if(paintType == "mousehover" && index == i) {
			d2 = 90;
		} else {
			d2 = 98;
		}
		dList.push_back(new Button(x1, y1, d1, x2, y2, d2, false, str_i));
		ss.str("");
	}
	if(paintType == "inGame" or paintType == "mouseclick") {
		unsigned long lastPaint = 0;
		int frame = 0;
		x1 = (attr.width/(n+1))*(index+1);
		y1 = attr.height/2;
		d1 = 100;
		x2 = x1 + .5;
		y2 = y1 + .5;
		d2 = 98;
		while(true) {
			unsigned long end = now();
			dList[index + 2] = new Button(x1, y1, d1, x2, y2, d2, true, "-1");
			repaint(xinfo);
			XFlush(xinfo.display);
			lastPaint = now();
			x2 += .5;
			y2 += .5;
			d2 -= 2;

			if(d2 == 0) {
				ss << index + 1;
				str_i = ss.str();
				dList[index + 2] = new Button(x1, y1, d1, x1 + .5, y1 + .5, d1 - 2, false, str_i);
				break;
			}

			if(XPending(xinfo.display) == 0) {
				usleep(1000000/FPS - (end - lastPaint));
			}
		}
	}
	repaint(xinfo);
}


/*
 * Exit when 'q' is typed.
 */
void handleKeyPress(XInfo &xinfo, XEvent &event, int n, Simon &simon) {
	if(!(simon.getState() == Simon::COMPUTER)) {
		KeySym key;
		char text[BufferSize];

		int i = XLookupString(
			(XKeyEvent*)&event,
			text,
			BufferSize,
			&key,
			NULL
		);
		if( i == 1 && text[0] == 'q' ) {

			// flush all pending requests to the X server.
			XFlush(xinfo.display);

			error("Terminated normally.");
			XCloseDisplay(xinfo.display);
			return;
		} else if( i == 1 && text[0] == ' ' ) {
			// start a new round with a new sequence;
			if(status == "You lose. Press SPACE to play again") sequenceNum = 0;
			simon.newRound();
			click.clear();
			status = "Watch what I do ...";
			while(simon.getState() == Simon::COMPUTER) {
				paintAnimation(xinfo, n, simon.nextButton(), "inGame");
				usleep(250000);
			}

			status = "Now it's your turn";
			paintAnimation(xinfo, n, -1, "null");
		}
	}
}

void handleResize(XInfo &xinfo, XEvent &event, int n) {
	XConfigureEvent xce = event.xconfigure;
	if(xce.width != xinfo.width || xce.height != xinfo.height) {
		xinfo.width = xce.width;
		xinfo.height = xce.height;
	}
	paintAnimation(xinfo, n, -1, "null");
}

void handleExpose(XInfo &xinfo, XEvent &event) {
	if( event.xexpose.count == 0 ) {
		repaint(xinfo);
	}
}

void handleMotion(XInfo &xinfo, XEvent &event, int n, Simon &simon) {
	if(!(simon.getState() == Simon::COMPUTER)) {
		XWindowAttributes attr;
		XGetWindowAttributes(xinfo.display, xinfo.window, &attr);

		float dis_sqr, dis;
		int i = 0;
		string in = "null";
		stringstream ss;
		string str_i;
		for(vector<Displayable*>::iterator it = dList.begin(); it != dList.end(); ++it) {
			dis_sqr = pow((event.xmotion.x - (attr.width/(n+1))*(i+1)), 2) + pow((event.xmotion.y - attr.height/2), 2);
			dis = sqrt(dis_sqr);
			if(dis <= 50) {
				in = "mousehover";
				break;
			}
		++i;
		}
		paintAnimation(xinfo, n, i, in);
	}
}

void handleButtonPress(XInfo &xinfo, XEvent &event, int n, Simon &simon) {
	if(!(simon.getState() == Simon::COMPUTER)) {
		XWindowAttributes attr;
		XGetWindowAttributes(xinfo.display, xinfo.window, &attr);

		float dis_sqr, dis;
		int i = 0;
		string in = "null";
		stringstream ss;
		string str_i;
		for(int j = 0; j < n; j++) {
			dis_sqr = pow((event.xbutton.x - (attr.width/(n+1))*(j+1)), 2) + pow((event.xbutton.y - attr.height/2), 2);
			dis = sqrt(dis_sqr);
			if(dis <= 50) {
				in = "mouseclick";
				break;
			}
		++i;
		}
		if(in == "mouseclick") click.push_back(i);
		paintAnimation(xinfo, n, i, in);

		bool cont = true;
		if(click.size() == simon.getScore() + 1) {
			while(simon.getState() == Simon::HUMAN) {
				for(int m = 0; m < click.size(); m++){
					if(!simon.verifyButton(click[m])) {
						cont = false;
					}
				}
				if(cont == true) ++sequenceNum;
			}
		}
	}
}

/*
 * The loop responding to events from the user
 */
void eventloop(XInfo& xinfo, int n, Simon& simon) {
	XEvent event;
	KeySym key;
	char text[BufferSize];

	while(true) {
		switch(simon.getState()) {
			case Simon::START:
				status = "Press SPACE to plays";
				break;
			case Simon::WIN:
				status = "You won! Press SPACE to continue";
				break;
			case Simon::LOSE:
				status = "You lose. Press SPACE to play again";
				break;
			default:
				break;
		}
		paintAnimation(xinfo, n, -1, "null");

		XNextEvent( xinfo.display, &event );

		switch( event.type ) {

			// repaint the window on expose events
			case Expose:
				handleExpose(xinfo, event);
				break;

			// handle the configure notify event, window resize
			case ConfigureNotify:
				handleResize(xinfo, event, n);
				break;

			// handle the motion notify event, detect the cursor current location
			case MotionNotify:
				handleMotion(xinfo, event, n, simon);
				break;

			// handle the key press event, close x window after pressing 'q'
			case KeyPress:
				handleKeyPress(xinfo, event, n, simon);
				break;

			// handle the button press event
			case ButtonPress:
				handleButtonPress(xinfo, event, n, simon);
				break;
		}
		usleep(1000000/FPS);
	}
}

// Initialize X and create a window
void initX(int argc, char *argv[], XInfo &xinfo) {
	XSizeHints hints;
	unsigned long white, black;
	// Display opening uses the DISPLAY    environment variable.
	// It can go wrong if DISPLAY isn't set, or you don't have permission.
	xinfo.display = XOpenDisplay( "" );
	if ( !xinfo.display )    {
		error( "Can't open display." );
	}
	// Find out some things about the display you're using.
	xinfo.screen = DefaultScreen( xinfo.display );
	white = XWhitePixel( xinfo.display, xinfo.screen );
	black = XBlackPixel( xinfo.display, xinfo.screen );
	hints.x = 100;
	hints.y = 100;
	hints.width = 800;
	hints.height = 400;
	hints.flags = PPosition | PSize;
	xinfo.window = XCreateSimpleWindow(
			xinfo.display,                // display where window appears
			DefaultRootWindow( xinfo.display ), // window's parent in window tree
			hints.x, hints.y,            // upper left corner location
			hints.width, hints.height,    // size of the window
			5,                        // width of window's border
			black,                        // window border colour
			white );                    // window background colour
	XSetStandardProperties(
			xinfo.display,        // display containing the window
			xinfo.window,        // window whose properties are set
			"a1",            // window's title
			"SD",                // icon's title
			None,                // pixmap for the icon
			argv, argc,            // applications command line args
			&hints );            // size hints for the window

	// Tell the window manager what input events you want
	XSelectInput(
		xinfo.display,
		xinfo.window,
		ButtonPressMask | KeyPressMask | ExposureMask | ButtonMotionMask | StructureNotifyMask | PointerMotionMask
	);

	// Put the window on the screen.
	XMapRaised( xinfo.display, xinfo.window );
	XFlush(xinfo.display);

	// give server time to setup before sending drawing commands
	sleep(1);
}

int main(int argc, char* argv[]) {
	// Get the number of buttons from args (1<= N <= 6)
	// default to 4 if args
	int n = 4;
	if(argc > 1) {
		n = atoi(argv[1]);
	}
	if(n < 0 or n > 6) n = 4;

	// Create the Simon game object
	Simon simon = Simon(n, true);

	XInfo xinfo;
	initX(argc, argv, xinfo);

	// create a simple graphics context
	GC gc = XCreateGC(xinfo.display, xinfo.window, 0, 0);
	int screen = DefaultScreen( xinfo.display );
	XSetForeground(xinfo.display, gc, BlackPixel(xinfo.display, screen));
	XSetBackground(xinfo.display, gc, WhitePixel(xinfo.display, screen));

	// load a larger font
	XFontStruct * font;
	font = XLoadQueryFont (xinfo.display, "12x24");
	XSetFont (xinfo.display, gc, font->fid);
	xinfo.gc = gc;

	// wait for user input
	eventloop(xinfo, n, simon);
}
