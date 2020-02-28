utiliser ppilot5
----------------

ppilot permet d’utiliser des systèmes de synthèse vocale compatibles SAPI5.

Lancement
ppilot5 -b 127.255.255.255:2010 -r Virginie -o "ScanSoft Virginie_Dri40_16kHz"

  -b adresse IP + port
  -r nom sous lequel apparaîtra l’agent sous ivy
  -o nom du moteur de synthèse utilisé (ici, la TTS "ScanSoft Virginie_Dri40_16kHz")

Commandes
  * Synthèse
    - ppilot5 Say="hello" ppilot prononce "hello" et envoie ppilot Answer=Finished quand le buffer est vide

  * Commandes
    - ppilot5 Command=Stop la synthèse vocale devrait être stoppée.
    - ppilot5 Command=Pause la synthèse vocale est mise en pause. ppilot renvoie ppilot Answer=Paused
    - ppilot5 Command=Resume la synthèse vocale est relancée si elle était en pause précédemment. ppilot renvoie ppilot Answer=Resumed
    - ppilot5 Command=Quit l’application se ferme

  * Paramètres
    - ppilot5 Param=Pitch:value le pitch devrait être changé par la valeur donnée. (ne fonctionne pas !)
    - ppilot5 Param=Speed:value la vitesse est changée par la valeur donnée. ppilot renvoie ppilot Answer=SpeedValueSet:value
    - ppilot5 Param=Volume:value le volume est changé par la valeur donnée. ppilot renvoie ppilot Answer=VolumeValueSet:value

    