# `nomad-live-android`

Share the action, be the motion.

## Setup

- Download [Android Studio](http://developer.android.com/sdk/index.html).
- Add the following to your `~/.bashrc`

```bash
...
export ANDROID_HOME="/home/$(whoami)/Android/Sdk/"
export ANDROID_TOOLS="/home/$(whoami)/Android/Sdk/tools"
export ANDROID_PLATFORM_TOOLS="/home/$(whoami)/Android/Sdk/platform-tools"
PATH=$PATH:$ANDROID_HOME:$ANDROID_TOOLS:$ANDROID_PLATFORM_TOOLS
```
- Refresh your environment with `source ~/.bashrc`
- Run `android` and install the `android-19` SDK


## Contribute

To contribute do not hesitate to send me pull requests !

* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally
* Consider starting the commit message with an applicable emoji:
	* :lipstick: when improving the format/structure of the code
	* :racehorse: when improving performance
	* :non-potable_water: when plugging memory leaks
	* :memo: when writing docs
	* :bulb: Check out the Emoji Cheat Sheet for more ideas.
