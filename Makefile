dev:
	## start irinium webcam
	emulator -avd Pixel_7_API_35 -gpu host -camera-front webcam1

run:
	yarn example android

logcat:
	adb logcat | grep MLKit
	## adb logcat | grep -E '(CameraHandler|MLKit)'