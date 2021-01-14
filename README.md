# WEID
Wallpaper Engine Idle Detector

Basically lets you use your beautiful wallpaper from Wallpaper Engine as a screen saver.
It doesn't actually hook into Wallpaper Engine **AT ALL**.
I only made it because I wanted to use my pretty wallpaper engine wallpaper as a "screensaver" of sorts.
Also works well with Rainmeter.

> **NOTE TO EVERYONE**
> 
> USE AT YOUR OWN RISK
> This has been tested, however I can tell you that it may not be 100% working
> I haven't had issues in my tests, but I cannot promise you the same.
> I have 0 liability when it comes to any issues you have, but I will try to help if you submit an issue.

> **Note to devs**
> 
> I made this really late when I was exhausted. Don't judge this mess of code.

### Requirements
- Java 8
- Windows *Only tested with Windows 10 x64*
- A good pretty wallpaper (optional)

### How to Use
1. Download the latest jar file from the [Releases](https://github.com/ItsErikSquared/WEID/releases) page.
2. Put it somewhere (Might I recommend, C:\Users\*username*\WEID\). There are no requirements on where it has to be.
3. Run it! A message will appear just letting you know it's running.
4. With your favorite text editor, open a new file that was created (WEID.properties)
   HideTaskbar - True will attempt to hide the taskbar when the timer runs (currently no multi-monitor support).
   IdleTime - The time (in seconds) for it to go from your apps to just your wallpaper.
   AwayTime - The time (in seconds) after IdleTime to lock the computer when it detects someone comes back (more of a security measure, but again, use at your own risk). `-1` will disable this.
    ```properties
   HideTaskbar=true
   IdleTime=300
   AwayTime=300
   ```
5. Open your System Tray (it's the arrow on the lower right of your screen, near the WiFi/Network select menu) and double click the WEID icon to reload the config.
6. (I'll tell you how to make this startup automatically when I am more confident that it won't be buggy. For now if you have problems, restart your comptuer.)
   Please for the love of god don't make it a startup app until this message is removed.

### Other Stuff
If you open your System Tray (it's the arrow on the lower right of your screen, near the WiFi/Network select menu):
- Double Clicking will reload the config
- Right clicking will open a menu that allows you to stop, reload, temp-pause, and preview the app.

### TODO
- Multi-monitor Support
- Option to Hide Desktop Icons
- Option to Hide Rainmeter Items
