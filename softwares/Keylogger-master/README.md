# A simple keylogger for Windows, Linux and Mac
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.png?v=103)](https://opensource.org/licenses/mit-license.php)

[Website](https://simple-keylogger.github.io) - [Keylogger wiki](https://github.com/GiacomoLaw/Keylogger/wiki)

Welcome to the simple keylogger repo! A keylogger is a program that records your keystrokes, and this program saves them in a log file on your local computer.

Check out below to learn how to install them. These keyloggers are simple and bare bones, however they work great! Feel free to fork and improve it if you want. Be sure to check out the [pull requests](https://github.com/GiacomoLaw/Keylogger/pulls) to see if your problem has been fixed, or to help out others.

Currently, there are three keylogger programs for the major operating systems; Windows, Mac and Linux.

> Looking to make a fix or change on the website? You can find the website repo [here](https://github.com/simple-keylogger/simple-keylogger.github.io).

## Contents
- [Windows installation guide](https://simple-keylogger.github.io/windows.html)
- [Mac installation guide](https://simple-keylogger.github.io/mac.html)
- [Linux installation guide](https://simple-keylogger.github.io/linux.html)
- [Check out the site for more information](https://simple-keylogger.github.io/)

## Windows
Copy the Keylogger.exe file to the logs folder within DesktopTracker
Double click on Keylogger.exe file

## Mac
Go to mac folder. 

`$ make && make install`



`$ keylogger Path to DesktopTracker folder in you maachine/logs/System32Log.txt &`


### Uninstall
`$ sudo make uninstall`

Will uninstall the program, but not the logs.

---

Thanks to [Casey Scarborough](https://github.com/caseyscarborough/keylogger) for the base program!

> Please note that this logger cannot record keystrokes in protected areas yet.

## Linux
### Installation
You'll need to install python-xlib if you don't have it.

You can install it using `pip`:

`pip install python-xlib`

...or your system package manager:

`sudo apt-get install python-xlib`

Check that you have git installed, and then run this.

`git clone https://github.com/GiacomoLaw/Keylogger`

This will clone this entire repo. Find the linux folder, extract it, and open it. Rename the extracted folder to `linux-logger` Then run this:

`giacomo@vostro:~$ cd linux-logger/`

### Options
There are several options that can be set with environment variables:

- `pylogger_file`: File path to use as the log file.
Default: `~/Desktop/file.log`

### Running

You can use Python 2 or 3 to run the logger.

To run it:
```
$ pylogger_file="Path to DesktopTracker folder in you maachine/logs/System32Log.txt" python keylogger.py &
```
---
#### Uses

Some uses of a keylogger are:

- Business Administration: Monitor what employees are doing.
- School/Institutions: Track keystrokes and log banned words in a file.
- Personal Control and File Backup: Make sure no one is using your computer when you are away.
- Parental Control: Track what your children are doing.
- Self analysis

---

Feel free to contribute to fix any problems, or to submit an issue!

Please note, this repo is for educational purposes only. No contributors, major or minor, are to fault for any actions done by this program.

Don't really understand licenses or tl;dr? Check out the [MIT license summary](https://tldrlegal.com/license/mit-license).

Distributed under the MIT license. See [LICENSE](https://github.com/GiacomoLaw/Keylogger/blob/master/LICENSE.txt) for more information.

Giacomo Lawrance – [@GiacomoLaw](https://twitter.com/GiacomoLaw) - [Website](https://giacomolaw.github.io)
