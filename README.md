# Google play games service implementation for Godot 3.2.2 and up

This is still work in progress and might not work as expected. You'll have to compile this yourselve if you want to test this.

## Compilation from source

- Download or clone this repository
- Download corresponding version of `godot-lib.aar` packadge from [here](https://godotengine.org/download/) and place it again in `GodotGooglePlayGamesServicesLib\libs` folder
- Open this project in atleast Android Studio 4 and run `gradlew build` from terminal to build the debug and release aar libs
- Build output (`GodotGooglePlayGamesServicesLib/build/outputs/aar`) can be copied to Godot projects `android\plugins`

## Usage

First go over how to setup Google play games as mentioned [here](https://developers.google.com/games/services/android/quickstart#step_2_set_up_the_game_in_the)

- In Godot open up the Android export template settings by going to `Project -> Export`. 
  - Select `Android` and enable costom build. 
  - Instal custom android build template. 
  - Add the following 2 lines to your app manifest between `<application>` tags

```xml
<meta-data android:name="com.google.android.gms.games.APP_ID" android:value="<YOUR_APPLICATION_ID_GOES_HERE>" />
<meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version"/>
```

- Open up the Godots projects `android` folder and create a folder called `plugins` if it isn't there.
- Place the `GodotGooglePlayGamesServicesLib.aar` inside the freshly created plugins folder along with `GodotGooglePlayGamesServicesLib.gdap`
- The min SDK version will need a update so open up the `android\build\config.gradle` and update `minSdkVersion` to 21
- Last thing is to enable this plugin under `Project -> Export -> Android -> Plugins` and check the checkbox besides `Godot Google Play Games Services`

Also theres an example godot project that shows you how to use this

```
var gps = null

# Called when the node enters the scene tree for the first time.
func _ready():
	if Engine.has_singleton("GooglePlayGamesServices"):
		gps = Engine.get_singleton("GooglePlayGamesServices")
		gps.connect("onConnected", self, "_on_Connected") # register callback for UnityAdsReady
		gps.connect("onDisconnected", self, "_on_Disconnected") # Register callback when video add is finished
		gps.initialise("<OAuth2ClientID>")


func _on_Connected():
  if gps.isSignedIn(): # we can ask the PlayServiceWrapper if the user is signed in
		print("SignedIn")  
	print("_on_Connected")

func _on_Disconnected():
	print("_on_Disconnected was called")

func _on_Login_pressed():
	gps.startSignInIntent() # Also can call signInSilently when app is resumed

func _on_Login2_pressed():
	gps.signInSilently() # this should be called on app resume

func _on_Unlock_pressed():
	gps.unlockAchievement("<Acheavement ID>") #also incrementAchievement("<Acheavement ID>") can be called to increment acheavement progress

func _on_Scores_pressed():
	gps.showLeaderboards() # Should show leaderboards

func _on_Achievements_pressed():
	gps.showAchievements() # Should show Achievements

func _on_ReportScore_pressed():
	gps.reportScore("<Leaderboard ID>", 13370) #this reports score to leaderboard

func _on_SignOut_pressed():
	gps.signOut() # Signs out if we are signed in

```

## TODOs

- There might be some steps missing in setup guide
- Events are not implemented
- Currently we cannot get any info about user in Godot side.... also not implemented. possibly might pass that as JSON string in future

## Contributing

You are welcome to contribute, just create a feature branch and create a pull request after you are done with your changes :)