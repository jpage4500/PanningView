PanningView
==================

PanningView is a library which implements the animated background in the *Now Playing* screen of the Play Music app.

Planned -> Sample APK either at Releases or Google Play Store.

Including in your project
-------------------------

Make sure that you have `jCenter` repository in your project's `build.gradle` file

```
buildscript {
    repositories {
		jcenter()
	}

```

Add this line in your `app/build.gradle` file to include the library.


```
	compile 'com.maelstrom.panning:panning-view:1.0.1'

```

License
-----------

    Copyright 2013 MaelStromJS 

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
  
Note
-----

The original repo is [here](https://github.com/flavienlaurent/PanningView) but it looks abandoned since 2014. So, MaelstromJS has decided to take it and support it as a Gradle module and probably make further changes in the future. We are retaining the same license as used by [@flavienlaurent](https://github.com/flavienlaurent).
