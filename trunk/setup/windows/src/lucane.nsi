# Script partially generated by the HM NIS Edit Script Wizard.
!define PRODUCT_NAME $%_PRODUCT_NAME%
!define PRODUCT_VERSION $%_PRODUCT_VERSION%
!define PRODUCT_PUBLISHER "$%_PRODUCT_PUBLISHER%"
!define WEB_SITE_URL $%_WEB_SITE_URL%
!define TRACKER_URL $%_TRACKER_URL%
!define FEATURE_REQUEST_URL $%_FEATURE_REQUEST_URL%
!define FORUM_URL $%_FORUM_URL%
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"
#!define PRODUCT_STARTMENU_REGVAL "NSIS:StartMenuDir"

!define BASE_DIR $%_BASE_DIR%
!define NSIS_FILE_DIR $%_NSIS_FILE_DIR%
!define DIST_DIR $%_DIST_DIR%
!define OUT_DIR $%_OUT_DIR%
!define LICENCE_FILE $%_LICENCE_FILE%

!define TEMP1 $R0 #Temp variable

!cd "${BASE_DIR}"

SetCompressor lzma

!include "MUI.nsh"
!include "sections.nsh"

# Macros
!macro SectionIsSelected SECTION JUMPIFSEL JUMPIFNOTSEL
  Push $R0
  SectionGetFlags "${SECTION}" $R0
  IntOp $R0 $R0 & "${SF_SELECTED}"
  IntCmp $R0 "${SF_SELECTED}" +3
  Pop $R0
  StrCmp "" "${JUMPIFNOTSEL}" +3 "${JUMPIFNOTSEL}"
  Pop $R0
  Goto "${JUMPIFSEL}"
!macroend

!macro setLanguage LANGUAGE
  ClearErrors
  FileOpen $0 "$INSTDIR\client\etc\client-config.xml" "r"
  GetTempFileName $R0
  FileOpen $1 $R0 "w"
  loop${LANGUAGE}:
    FileRead $0 $2
    IfErrors done${LANGUAGE}
    StrCmp $2 "  <language value=$\"en$\" />$\n" 0 +3
      FileWrite $1 "  <language value=$\"${LANGUAGE}$\" />$\n"
      Goto loop${LANGUAGE}
    StrCmp $2 "  <language value=$\"en$\" />" 0 +3
      FileWrite $1 "  <language value=$\"${LANGUAGE}$\" />"
      Goto loop${LANGUAGE}
    FileWrite $1 $2
    Goto loop${LANGUAGE}

  done${LANGUAGE}:
    FileClose $0
    FileClose $1
    Delete "$INSTDIR\client\etc\client-config.xml"
    CopyFiles /SILENT $R0 "$INSTDIR\client\etc\client-config.xml"
    Delete $R0
!macroend

# MUI Settings
!define MUI_HEADERIMAGE
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\win-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\win-uninstall.ico"

# Welcome page
!insertmacro MUI_PAGE_WELCOME
# License page
!define MUI_LICENSEPAGE_CHECKBOX
!insertmacro MUI_PAGE_LICENSE "${LICENCE_FILE}"
# Components page
!insertmacro MUI_PAGE_COMPONENTS
# Directory page
!insertmacro MUI_PAGE_DIRECTORY
# Shortcuts selection
Page custom SetShortcuts ValidateShortcuts
# Start menu page
#var ICONS_GROUP
!define ICONS_GROUP ${PRODUCT_NAME}
# TODO fix the no startmenu shortcuts bug
#!define MUI_STARTMENUPAGE_NODISABLE
#!define MUI_STARTMENUPAGE_DEFAULTFOLDER "${PRODUCT_NAME}"
#!define MUI_STARTMENUPAGE_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
#!define MUI_STARTMENUPAGE_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
#!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "${PRODUCT_STARTMENU_REGVAL}"
#!insertmacro MUI_PAGE_STARTMENU "Startmenu" ${ICONS_GROUP}
# Instfiles page
!insertmacro MUI_PAGE_INSTFILES
# Language selection
Page custom SetLanguage ValidateLanguage
# Finish page
!insertmacro MUI_PAGE_FINISH

# Uninstaller pages
!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

# Language files
!insertmacro MUI_LANGUAGE "English"
#!insertmacro MUI_LANGUAGE "French"

#Remember the installer language
#!define MUI_LANGDLL_REGISTRY_ROOT "HKCU"
#!define MUI_LANGDLL_REGISTRY_KEY "Software\${PRODUCT_NAME}"
#!define MUI_LANGDLL_REGISTRY_VALUENAME "Installer Language"

# Reserve files
#!insertmacro MUI_RESERVEFILE_LANGDLL
#!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS

# MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "${OUT_DIR}\${PRODUCT_NAME}-${PRODUCT_VERSION}-setup.exe"
InstallDir "$PROGRAMFILES\${PRODUCT_NAME}"
ShowInstDetails show
ShowUnInstDetails show
XPStyle on
CRCCheck on

#show the language dialog
#Function .onInit
#  !insertmacro MUI_LANGDLL_DISPLAY
#FunctionEnd

Function .onInit
  #Extract InstallOptions files
  #$PLUGINSDIR will automatically be removed when the installer closes
  InitPluginsDir
  File /oname=$PLUGINSDIR\shortcuts.ini "${NSIS_FILE_DIR}\shortcuts.ini"
  File /oname=$PLUGINSDIR\language.ini "${NSIS_FILE_DIR}\language.ini"
FunctionEnd


Section "Client" CLIENT
  SetOutPath "$INSTDIR\client"
  SetOverwrite ifnewer
  File /r "${DIST_DIR}\client\*"
SectionEnd
  Section "-Start menu icon" CLIENT_STARTMENU
    CreateDirectory "$SMPROGRAMS\${ICONS_GROUP}"
    CreateShortCut "$SMPROGRAMS\${ICONS_GROUP}\Client.lnk" "$INSTDIR\client\client.bat" ""
  SectionEnd
  Section "-Quick launch icon" CLIENT_QUICKLAUNCH
    CreateShortCut "$QUICKLAUNCH\${PRODUCT_NAME} client.lnk" "$INSTDIR\client\client.bat" ""
  SectionEnd
  Section "-Desktop icon" CLIENT_DESKTOP
    CreateShortCut "$DESKTOP\${PRODUCT_NAME} client.lnk" "$INSTDIR\client\client.bat" ""
  SectionEnd

Section "Server" SERVER
  SetOutPath "$INSTDIR\server"
  SetOverwrite ifnewer
  File /r "${DIST_DIR}\server\*"
SectionEnd
  Section "-Start menu icon" SERVER_STARTMENU
    CreateDirectory "$SMPROGRAMS\${ICONS_GROUP}"
    CreateShortCut "$SMPROGRAMS\${ICONS_GROUP}\Server.lnk" "$INSTDIR\server\server.bat"
  SectionEnd

Section "Proxy" PROXY
  SetOutPath "$INSTDIR\proxy"
  SetOverwrite ifnewer
  File /r "${DIST_DIR}\proxy\*"
SectionEnd
  Section "-Start menu icon" PROXY_STARTMENU
    CreateDirectory "$SMPROGRAMS\${ICONS_GROUP}"
    CreateShortCut "$SMPROGRAMS\${ICONS_GROUP}\Proxy.lnk" "$INSTDIR\proxy\proxy.bat"
  SectionEnd

Section "-AdditionalIcons" ADDITIONAL_ICONS
  WriteIniStr "$INSTDIR\Web site.url" "InternetShortcut" "URL" "${WEB_SITE_URL}"
  WriteIniStr "$INSTDIR\Tracker.url" "InternetShortcut" "URL" "${TRACKER_URL}"
  WriteIniStr "$INSTDIR\Feature request.url" "InternetShortcut" "URL" "${FEATURE_REQUEST_URL}"
  WriteIniStr "$INSTDIR\Forum.url" "InternetShortcut" "URL" "${FORUM_URL}"
  CreateDirectory "$SMPROGRAMS\${ICONS_GROUP}"
  CreateDirectory "$SMPROGRAMS\${ICONS_GROUP}\${PRODUCT_NAME} on the web"
  CreateShortCut "$SMPROGRAMS\${ICONS_GROUP}\${PRODUCT_NAME} on the web\Website.lnk" "$INSTDIR\Web site.url"
  CreateShortCut "$SMPROGRAMS\${ICONS_GROUP}\${PRODUCT_NAME} on the web\Tracker.lnk" "$INSTDIR\Tracker.url"
  CreateShortCut "$SMPROGRAMS\${ICONS_GROUP}\${PRODUCT_NAME} on the web\Feature request.lnk" "$INSTDIR\Feature request.url"
  CreateShortCut "$SMPROGRAMS\${ICONS_GROUP}\${PRODUCT_NAME} on the web\Forum.lnk" "$INSTDIR\Forum.url"
  CreateShortCut "$SMPROGRAMS\${ICONS_GROUP}\Uninstall.lnk" "$INSTDIR\uninst.exe"
SectionEnd

Section "-Shortcuts" shortcuts
  #Get Install Options dialog user input
  ReadINIStr ${TEMP1} "$PLUGINSDIR\shortcuts.ini" "Field 2" "state"
  ReadINIStr ${TEMP1} "$PLUGINSDIR\shortcuts.ini" "Field 3" "state"
  ReadINIStr ${TEMP1} "$PLUGINSDIR\shortcuts.ini" "Field 4" "state"
SectionEnd

Section "-Language" language
  #Get Install Options dialog user input
  ReadINIStr ${TEMP1} "$PLUGINSDIR\language.ini" "Field 2" "state"
  ReadINIStr ${TEMP1} "$PLUGINSDIR\language.ini" "Field 3" "state"
  ReadINIStr ${TEMP1} "$PLUGINSDIR\language.ini" "Field 4" "state"
SectionEnd

Section "-Post"
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "${PRODUCT_STARTMENU_REGVAL}" "${ICONS_GROUP}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd

# Section descriptions
# TODO : add a good description of the sections
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${CLIENT} "${PRODUCT_NAME} client"
  !insertmacro MUI_DESCRIPTION_TEXT ${SERVER} "${PRODUCT_NAME} server"
  !insertmacro MUI_DESCRIPTION_TEXT ${PROXY} "${PRODUCT_NAME} proxy"
!insertmacro MUI_FUNCTION_DESCRIPTION_END


# Shortcuts dialog
Function SetShortcuts
  !insertmacro MUI_HEADER_TEXT "Choose shortcuts" "Choose the shortcuts you want to create"
  Push ${TEMP1}
    InstallOptions::dialog "$PLUGINSDIR\shortcuts.ini"
    Pop ${TEMP1}
  Pop ${TEMP1}
FunctionEnd

Function ValidateShortcuts

  #----- Start menu icons -----
  ReadINIStr ${TEMP1} "$PLUGINSDIR\shortcuts.ini" "Field 2" "state"

  StrCmp ${TEMP1} 1 yes1
    no1:
      !insertmacro UnselectSection ${CLIENT_STARTMENU}
      !insertmacro UnselectSection ${SERVER_STARTMENU}
      !insertmacro UnselectSection ${PROXY_STARTMENU}
      !insertmacro UnselectSection ${ADDITIONAL_ICONS}
      goto end1
    yes1:
      !insertmacro SectionIsSelected ${CLIENT} client_yes1 client_no1
        client_yes1: !insertmacro SelectSection ${CLIENT_STARTMENU}
                     goto client_end1
        client_no1:  !insertmacro UnselectSection ${CLIENT_STARTMENU}
        client_end1:
      !insertmacro SectionIsSelected ${SERVER} server_yes1 server_no1
        server_yes1: !insertmacro SelectSection ${SERVER_STARTMENU}
                     goto server_end1
        server_no1:  !insertmacro UnselectSection ${SERVER_STARTMENU}
        server_end1:
      !insertmacro SectionIsSelected ${PROXY} proxy_yes1 proxy_no1
        proxy_yes1:  !insertmacro SelectSection ${PROXY_STARTMENU}
                     goto proxy_end1
        proxy_no1:   !insertmacro UnselectSection ${PROXY_STARTMENU}
        proxy_end1:
        !insertmacro SelectSection ${ADDITIONAL_ICONS}
    end1:

  #----- Quick launch icons
  ReadINIStr ${TEMP1} "$PLUGINSDIR\shortcuts.ini" "Field 3" "state"

  StrCmp ${TEMP1} 1 yes2
    no2:
      !insertmacro UnselectSection ${CLIENT_QUICKLAUNCH}
      goto end2
    yes2:
      !insertmacro SectionIsSelected ${CLIENT} client_yes2 client_no2
        client_yes2: !insertmacro SelectSection ${CLIENT_QUICKLAUNCH}
                     goto client_end2
        client_no2:  !insertmacro UnselectSection ${CLIENT_QUICKLAUNCH}
        client_end2:
    end2:

  #----- Desktop icons
  ReadINIStr ${TEMP1} "$PLUGINSDIR\shortcuts.ini" "Field 4" "state"

  StrCmp ${TEMP1} 1 yes3
    no3:
      !insertmacro UnselectSection ${CLIENT_DESKTOP}
      goto end3
    yes3:
      !insertmacro SectionIsSelected ${CLIENT} client_yes3 client_no3
        client_yes3: !insertmacro SelectSection ${CLIENT_DESKTOP}
                     goto client_end3
        client_no3:  !insertmacro UnselectSection ${CLIENT_DESKTOP}
        client_end3:
    end3:

FunctionEnd



# Language dialog
Function SetLanguage
  !insertmacro MUI_HEADER_TEXT "Choose language" "Choose the language of the ${PRODUCT_NAME} client interface"
  Push ${TEMP1}
    InstallOptions::dialog "$PLUGINSDIR\language.ini"
    Pop ${TEMP1}
  Pop ${TEMP1}
FunctionEnd

Function ValidateLanguage

  #----- English -----
  ReadINIStr ${TEMP1} "$PLUGINSDIR\language.ini" "Field 2" "state"

  StrCmp ${TEMP1} 0 no1
    !insertmacro setLanguage "en"
    goto end
    no1:

  #----- French -----
  ReadINIStr ${TEMP1} "$PLUGINSDIR\language.ini" "Field 3" "state"

  StrCmp ${TEMP1} 0 no2
    !insertmacro setLanguage "fr"
    goto end
    no2:

  #----- Dutch -----
  ReadINIStr ${TEMP1} "$PLUGINSDIR\language.ini" "Field 4" "state"

  StrCmp ${TEMP1} 0 no3
    !insertmacro setLanguage "nl"
    goto end
    no3:

  end:

FunctionEnd





#Function un.onInit
#  !insertmacro MUI_UNGETLANGUAGE
#FunctionEnd

Section Uninstall
#  ReadRegStr ${ICONS_GROUP} ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "${PRODUCT_STARTMENU_REGVAL}"

  Delete "$DESKTOP\${PRODUCT_NAME} client.lnk"
  Delete "$QUICKLAUNCH\${PRODUCT_NAME} client.lnk"

  RMDir /r "$SMPROGRAMS\${ICONS_GROUP}"
  RMDir /r "$INSTDIR"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  SetAutoClose true
SectionEnd
