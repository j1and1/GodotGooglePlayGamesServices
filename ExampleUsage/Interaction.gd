extends Node


var gps = null

# Called when the node enters the scene tree for the first time.
func _ready():
	if Engine.has_singleton("GooglePlayGamesServices"):
		gps = Engine.get_singleton("GooglePlayGamesServices")
		gps.connect("onConnected", self, "_on_Connected") # register callback for UnityAdsReady
		gps.connect("onDisconnected", self, "_on_Disconnected") # Register callback when video add is finished
		gps.initialise(true, false) # requestProfile data - True, requestEmail - False

func _on_Connected():
	$SignedOut.hide()
	$SignedIn.show()
	
	print(gps.getAccountInfo()) #returns account info in JSON or empty string if theres an error. If you didnt request email or profile data no info will be provided

func _on_Disconnected():
	$SignedOut.show()
	$SignedIn.hide()

func _on_Login_pressed():
	gps.signInSilently() # logs in silently if it cant then runs signin intent

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
