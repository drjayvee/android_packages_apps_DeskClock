# Building the Android Desk Clock App

This guide provides instructions for building the Android Desk Clock app from source.

## Setup

```
sdk use java 8.0.412-tem
```

### 3. Create local.properties

Create a `local.properties` file in the project root with the following content:

```properties
sdk.dir=/home/yourusername/Android/Sdk
```

Replace `/home/yourusername` with your actual home directory path.

## Building

### Using Gradle Directly

```bash
# Navigate to project directory
cd /path/to/deskclock

# Build with Gradle 4.1 and Java 8
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 8.0.412-tem
~/.local/share/mise/installs/gradle/4.1.0/gradle-4.1/bin/gradle build
```

### Using the Gradle Wrapper (if available)

```bash
# Navigate to project directory
cd /path/to/deskclock

# Make gradlew executable
chmod +x gradlew

# Build with Java 8
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 8.0.412-tem
./gradlew build
```

## Troubleshooting

### 1. Gradle Version Issues
If you get errors about Gradle version compatibility:
- Ensure you're using Gradle 4.1
- Check that `mise.toml` specifies `gradle = "4.1"`
- Verify with `gradle --version`

### 2. Java Version Issues
If you get errors about Java version:
- Ensure you're using Java 8
- Check with `java -version`
- Switch with `sdk use java 8.0.412-tem`

### 3. SDK License Issues
If you get errors about unaccepted licenses:
```bash
# Accept all licenses
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 17.0.11-tem  # sdkmanager requires Java 17
cd ~/Android/Sdk/cmdline-tools/bin
./sdkmanager --sdk_root=~/Android/Sdk --licenses
sdk use java 8.0.412-tem  # Switch back to Java 8
```

## Cleanup

To remove the build tools when done:

```bash
# Remove Android SDK
rm -rf ~/Android

# Remove Java versions (optional)
sdk uninstall java 8.0.412-tem
sdk uninstall java 17.0.11-tem

# Remove Gradle 4.1 (optional)
mise uninstall gradle@4.1

# Remove local.properties (optional)
rm local.properties
```

## Notes

- This project uses Android Support Library 26.0.0-alpha1
- The original LineageOS-specific features (ProfileManager) have been removed
- Some notification styles have been simplified for compatibility
- Lint errors are ignored to allow the build to complete

## Signing the APK

To sign the release APK, use the existing keystore:

```bash
# Sign the release APK using apksigner
~/Android/Sdk/build-tools/26.0.3/apksigner sign \
  --ks my_keystore.jks \
  --ks-pass pass:android \
  --ks-key-alias mekker_key \
  --key-pass pass:android \
  --out mekker.apk \
  app/build/outputs/apk/release/app-release-unsigned.apk
```

**Keystore Details:**
- File: `my_keystore.jks`
- Password: `android`
- Key alias: `mekker_key`
- Key password: `android`

**Important**: Always use these same keystore details for all builds to avoid signature mismatches when upgrading the app.

# Verify the signature
~/Android/Sdk/build-tools/26.0.3/apksigner verify mekker.apk
```

## Installing the APK

To install the signed APK on a connected device:

```bash
# Check if device is connected
adb devices

# Uninstall existing version if needed (to avoid signature conflicts)
adb uninstall com.android.deskclock

# Install the new APK
adb install mekker.apk

# Launch the app
adb shell am start -n com.android.deskclock/.DeskClock
```

## Output

After a successful build:
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK (unsigned): `app/build/outputs/apk/release/app-release-unsigned.apk`
- Release APK (signed): `mekker.apk` (after signing)
