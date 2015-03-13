# nomad-live-android

Share the action, be the motion.

# Setup

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
