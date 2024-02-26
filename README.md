# Android Picture-in-Picture

Android Picture-in-Picture (PiP) 是 Android 系統提供的一項強大的多任務處理功能，允許用戶在屏幕上同時查看兩個應用程序，其中一個是縮小的畫面。這個功能特別適用於視頻播放應用、視頻通話應用和其他需要在屏幕上顯示內容的應用程序。

## manifest

```kotlin
        <activity
            android:name=".MainActivity"
            android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation"
            android:exported="true"
            android:supportsPictureInPicture="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

```

## check device is support PIP

```kotlin
     private fun checkPIP(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }

```


## onPictureInPictureModeChanged

```kotlin

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        .....

    }

```

![image]{https://github.com/Ayaa17/android-picture-in-picture/blob/master/screenshot.png}