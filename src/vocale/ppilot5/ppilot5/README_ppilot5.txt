utiliser ppilot5
----------------

ppilot permet d�utiliser des syst�mes de synth�se vocale compatibles SAPI5.

Lancement
ppilot5 -b 127.255.255.255:2010 -r Virginie -o "ScanSoft Virginie_Dri40_16kHz"

  -b adresse IP + port
  -r nom sous lequel appara�tra l�agent sous ivy
  -o nom du moteur de synth�se utilis� (ici, la TTS "ScanSoft Virginie_Dri40_16kHz")

Commandes
  * Synth�se
    - ppilot5 Say="hello" ppilot prononce "hello" et envoie ppilot Answer=Finished quand le buffer est vide

  * Commandes
    - ppilot5 Command=Stop la synth�se vocale devrait �tre stopp�e.
    - ppilot5 Command=Pause la synth�se vocale est mise en pause. ppilot renvoie ppilot Answer=Paused
    - ppilot5 Command=Resume la synth�se vocale est relanc�e si elle �tait en pause pr�c�demment. ppilot renvoie ppilot Answer=Resumed
    - ppilot5 Command=Quit l�application se ferme

  * Param�tres
    - ppilot5 Param=Pitch:value le pitch devrait �tre chang� par la valeur donn�e. (ne fonctionne pas !)
    - ppilot5 Param=Speed:value la vitesse est chang�e par la valeur donn�e. ppilot renvoie ppilot Answer=SpeedValueSet:value
    - ppilot5 Param=Volume:value le volume est chang� par la valeur donn�e. ppilot renvoie ppilot Answer=VolumeValueSet:value

    