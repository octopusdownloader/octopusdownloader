@rem build script
@rem Windows installer generator for AppVeyor

IF "%APPVEYOR_REPO_TAG%" == "true" GOTO BUILD_INSTALLER
:INFORM
@echo Not a commit tag, skipping installer build
GOTO END

:BUILD_INSTALLER
@echo Tag commit found
@echo Starting the installer generation
gradlew.bat jfxNative
GOTO END

:END
