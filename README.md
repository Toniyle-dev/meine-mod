# LARP Blocks - clientseitige Fake-Creative-Vorschau

Ein reines Client-Mod fuer Fabric (Minecraft 1.21.11). Nichts davon wird an
den Server gesendet oder von anderen Spielern gesehen - alles ist nur eine
lokale Illusion fuer dich (z.B. zum Bauten planen, LARP/Roleplay, Screenshots,
Content).

## Befehle (im Chat eintippen, werden abgefangen und NICHT gesendet)

- `.creative` - aktiviert den Fake-Creative-Modus und oeffnet sofort den
  Item-Picker (das "Fake Creative Pop-up").
- `.survival` - deaktiviert den Fake-Modus wieder, entfernt die Fake-Items
  aus der Hotbar. Bereits platzierte Ghost-Bloecke bleiben stehen.
- `.clearblocks` - entfernt alle platzierten Ghost-Bloecke.

## Steuerung

- **G** (konfigurierbar unter Optionen -> Tasten -> "LARP Blocks"): oeffnet
  den Item-Picker erneut, solange Fake-Creative aktiv ist.
- Im Picker: Item anklicken = in aktuellen Fake-Hotbar-Slot legen. Tasten
  1-9 wechseln den Ziel-Slot. Mausrad scrollt.
- Rechtsklick mit einem Fake-Block in der Hand: platziert einen Ghost-Block
  (nur lokal sichtbar).
- Linksklick auf einen Ghost-Block: entfernt ihn wieder.

Die Fake-Hotbar zeigt immer ein kleines "[LARP] Fake Creative (client-side)"
Wasserzeichen, damit du selbst nie den Ueberblick verlierst, was echt ist.

## Bauen

1. Java 21 installieren.
2. In `gradle.properties` die drei markierten Versionsstrings gegen
   https://fabricmc.net/develop/ pruefen (ich hatte beim Schreiben keinen
   Internetzugriff, daher koennen die exakten Build-Nummern fuer 1.21.11
   inzwischen leicht abweichen).
3. `./gradlew build` im Projektordner ausfuehren.
4. Die fertige Jar liegt danach in `build/libs/larpblocks-1.0.0.jar`.
5. In den `mods`-Ordner deiner Fabric-Instanz legen (zusammen mit der
   passenden Fabric-API-Jar).

## Bekannte Einschraenkungen

- **Keine Kollision**: Ghost-Bloecke sind rein visuell, man laeuft
  hindurch. Kollision liesse sich nachruesten, ist aber deutlich
  fehleranfaelliger (braucht Mixins in die World-Collision-Logik) und war
  hier bewusst ausgeklammert.
- **Mapping-Namen**: Ein paar Methodennamen (z.B. bei `BlockRenderManager`
  oder `DrawContext`) koennen sich zwischen Yarn-Mapping-Versionen leicht
  unterscheiden. Falls beim Kompilieren einzelne Methoden nicht gefunden
  werden, hilft ein Blick in die generierten Sources (`./gradlew
  genSources`) oder auf mappings.dev / Linkie fuer 1.21.11 - dort findest
  du den exakt richtigen Namen fuer deine Version.
- **Kein Server-Modus**: Das Mod ist bewusst als `"environment": "client"`
  markiert und laedt auf einem Dedicated Server gar nicht erst.

## Kurzer Hinweis

Das Ganze ist als Werkzeug fuer dich selbst gedacht (Bauplanung, Screenshots,
Roleplay). Da es rein clientseitig ist, kann es niemand anderes im Spiel
sehen - es ist also nicht "unfair" gegenueber Mitspielern. Falls du mal
per Screenshare streamst: denk dran, dass jemand, der nur deinen Bildschirm
sieht, die Fake-Items fuer echt halten koennte (deshalb das Wasserzeichen).
Nicht benutzen, um jemanden bei einem Trade zu taeuschen.
