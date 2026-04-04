# RuinsCore

RuinsCore ist ein modular aufgebautes Paper-Plugin (1.21.4) mit dynamischem Laden von Commands und Listenern, SQLite-Backend und einem erweiterten Jobsystem mit GUI-Verwaltung.

## ⭐ Hauptfeatures

- 🏢 **Modulsystem**: Modulare Architektur mit Lifecycle-Management
- 💼 **Jobsystem**: Umfassendes Job-Management mit GUI, Leaders und Extra-Herzen
- 💰 **Geldsystem**: Vollständiges Economy-System mit Transaktionen und Anfragen
- 🔒 **Vanish System**: Spieler-Invisibility mit persistentem Status
- ⚡ **Essentials Commands**: Beliebte Admin-Commands (Gamemode, Fly, Speed, Vanish, etc.)
- 💬 **Private Nachrichten**: Mit Admin-Spy Feature
- 📊 **SQLite Datenbank**: Persistente Speicherung aller Daten
- 🔌 **Dynamisches Laden**: Commands und Listener werden automatisch geladen
- 🔐 **Permission-System**: Granulare Kontrolle über alle Features

## Architektur

- Modulsystem mit Lifecycle über `ModuleManager` und `PluginModule`
- Dynamischer Command-Loader aus `loader.command-package` (Reflection + Auto-Registrierung)
- Dynamischer Event-Loader aus `loader.listener-package`
- SQLite-Datenhaltung über `DatabaseManager` (lokale Datei im Plugin-Datenordner)
- Utility-Layer für Logging (`LoggerUtil`) und Staff-Benachrichtigungen (`StaffAlertUtil`)
- Permission-Handling mit `PermissionNode`-Enum + `PermissionManager`

## Aktueller Funktionsumfang

### Core

- Dynamische Commands mit Tab-Completion (`CoreCommand`)
- Dynamische Listener-Registrierung beim Start
- Persistenz für Jobs und Job-Leader in SQLite
- Health-Sync-Service für Job-basierte Extra-Herzen

### Jobsystem

`/job` unterstützt:

- `assign`, `remove`, `list`
- `leader add|remove|list`
- `gui` (Übersicht -> Mitglieder -> User hinzufügen/entfernen)

Leader können das GUI ebenfalls nutzen, aber nur für ihre eigenen Jobs. Admins mit `ruinscore.job.manage` oder `ruinscore.job.gui` sehen alle Jobs.

### Jobregeln (Stand im Code)

- `holzfaeller`: Nur Holzfäller/Builder dürfen Holz normal abbauen; andere bekommen Lähmung + ggf. Planken-Drop
- `miner`: Nur Miner/Builder dürfen Erze abbauen
- `builder`: Umgeht Holz-/Erz-Restriktionen
- `fischer`: Nicht-Fischer erhalten beim Angeln keinen Epic-Loot (wird auf Fisch ersetzt)
- `bauer`: Nur Bauer dürfen essensrelevante Pflanzen anbauen/abbauen und essbare Items craften
- `schmied`: Nur Schmiede dürfen definierte Rüstung/Werkzeuge craften, upgraden und reparieren
- `lehrer`: Stock-Schaden-Multiplikator
- `bannerrist`: Nur Bannerristen dürfen Banner craften und am Webstuhl bearbeiten
- `wache`: Durchsuchen/Festhalten per Stock auf Spieler (inkl. Slowness-Festhalten)
- `berater`: Chat-Nachrichten mit `!` werden als Server-Ankündigung gesendet
- `schankwirt`: Nur Schankwirte dürfen den Braustand nutzen
- `verzauberer`: Nur Verzauberer dürfen Zaubertisch und Amboss nutzen
- `leutnant`, `ritter`, `sensenmann`, `prinz_prinzessin`: Extra-Herzen via Health-Sync
## Commands

- `/ping`
- `/staffalert` (`/sa`)
- `/job`
- `/money` - Zeige dein Guthaben an
- `/money set <Spieler> <Betrag>` (Admin) - Setze das Guthaben eines Spielers
- `/money add <Spieler> <Betrag>` (Admin) - Addiere Geld zu einem Spieler
- `/money remove <Spieler> <Betrag>` (Admin) - Entnehme Geld von einem Spieler
- `/money info <Spieler>` (Admin) - Zeige detaillierte Guthaben-Informationen
- `/pay <Spieler> <Betrag>` - Sende Geld an einen anderen Spieler
- `/request <Spieler> <Betrag>` - Fordere Geld von einem Spieler an
- `/requests [accept|decline <ID>]` - Verwalte deine ausstehenden Geldanfragen
- `/sudo <Spieler> <Command...>` (Admin) - Führe Command als anderer Spieler aus
- `/gm [Gamemode/Spieler] [Gamemode]` (Admin) - Ändere den Gamemode
- `/fly [Spieler]` (Admin) - Aktiviere/Deaktiviere Flug
- `/speed <Geschwindigkeit> [Spieler]` (Admin) - Ändere Bewegungsgeschwindigkeit
- `/flyspeed <Geschwindigkeit> [Spieler]` (Admin) - Ändere nur die Flug-Geschwindigkeit
- `/resetallspeeds [Spieler]` (Admin) - Setze alle Geschwindigkeiten zurück
- `/vanish [Spieler]` (Admin) - Mache dich oder andere unsichtbar
- `/fireball [Geschwindigkeit]` (Admin) - Werfe einen Feuerball
- `/smite <Spieler>` (Admin) - Schlage Spieler mit Blitz
- `/msg <Spieler> <Nachricht...>` - Sende private Nachricht
- `/supportmode` (Supporter) - Aktiviere Support Mode mit Godmode und Glow
- `/bc chat <Nachricht...>` (Admin) - Sende Chat-Broadcast
- `/bc display <Nachricht...>` (Admin) - Sende roten Title-Broadcast
- `/freeze <Spieler>` (Admin) - Friere Spieler ein/auf
- `/invsee <Spieler>` (Admin) - Schau Spieler-Inventar an (Live-Sync)
- `/endersee <Spieler>` (Admin) - Schau Spieler-Ender-Chest an (Live-Sync)
- `/warn <Spieler> <Grund...>` (Admin) - Verwarnt einen Spieler
- `/warns <Spieler>` (Admin) - Zeige Verwarnungen eines Spielers
- `/size <Größe>` - Ändere deine Spielergröße (0.6 - 1.1)
- `/size <Spieler> <Größe>` (Admin) - Ändere Spielergröße
- `/msg` (Spy - Admin) - Sehe private Nachrichten anderer
- `/marry marry <Spieler1> <Spieler2>` (Admin) - Verheirate zwei Spieler
- `/marry divorce <Spieler>` (Admin) - Scheiden
- `/marry info <Spieler>` (Admin) - Zeige Ehe-Information

## Permissions

Aus `plugin.yml`:

- `ruinscore.command.ping`
- `ruinscore.command.staffalert`
- `ruinscore.command.money`
- `ruinscore.command.money.admin` (set, add, remove, info)
- `ruinscore.command.pay`
- `ruinscore.command.request`
- `ruinscore.command.sudo` (execute commands as other players)
- `ruinscore.command.gamemode` (change gamemode)
- `ruinscore.command.fly` (enable/disable flight)
- `ruinscore.command.speed` (change movement speed)
- `ruinscore.command.flyspeed` (change flight speed only)
- `ruinscore.command.resetallspeeds` (reset all player speeds)
- `ruinscore.command.vanish` (become invisible)
- `ruinscore.command.fireball` (throw fireballs)
- `ruinscore.command.smite` (strike players with lightning)
- `ruinscore.command.msg` (send private messages)
- `ruinscore.command.msg.spy` (see private messages of others)
- `ruinscore.command.supportmode` (support mode with godmode and glow)
- `ruinscore.command.broadcast` (broadcast messages to all players)
- `ruinscore.command.freeze` (freeze/unfreeze players)
- `ruinscore.command.invsee` (view player inventories with live-sync)
- `ruinscore.command.endersee` (view player ender chests with live-sync)
- `ruinscore.chat.bypass` (see and send all chat messages without radius limit)
- `ruinscore.command.warn` (warn players)
- `ruinscore.command.warns` (view player warnings)
- `ruinscore.command.size` (change own player size)
- `ruinscore.command.size.other` (change other player sizes)
- `ruinscore.job.manage`
- `ruinscore.job.gui`
- `ruinscore.staff.alert.send`
- `ruinscore.staff.alert.receive`
- `ruinscore.auction.use` (use auction system)
- `ruinscore.command.marry` (marry/divorce/info players)

## Marry System

Das Geldsystem erlaubt Spielern, Guthaben zu haben und zwischen Spielern zu transferieren.

### Features

- **Guthaben verwalten**: Jeder Spieler hat ein eigenes Geldkonto (wird beim Spieler-Login initialisiert)
- **Direkte Transfers**: Mit `/pay <Spieler> <Betrag>` kann Geld direkt gesendet werden
- **Geldanfragen**: Mit `/request <Spieler> <Betrag>` kann Geld angefordert werden
- **Anfrage-Verwaltung**: Mit `/requests` können Anfragen akzeptiert oder abgelehnt werden
- **Formatierte Anzeige**: Alle Beträge werden im Format "X.XXX €" angezeigt
- **Admin-Verwaltung**: Admins mit `ruinscore.command.money.admin` können:
  - `/money set <Spieler> <Betrag>` - Guthaben direkt setzen
  - `/money add <Spieler> <Betrag>` - Geld hinzufügen
  - `/money remove <Spieler> <Betrag>` - Geld entnehmen
  - `/money info <Spieler>` - Detaillierte Spieler-Informationen anzeigen

### Datenbank

Neue Tabellen:

- `player_economy` - Speichert das Guthaben jedes Spielers
- `money_requests` - Speichert ausstehende, akzeptierte und abgelehnte Geldanfragen

### Implementierung

- `EconomyService` - Core-Logik für Guthaben (get, set, add, remove)
- `MoneyTransactionService` - Verwaltung von Geldanfragen (create, accept, decline)
- `EconomyJoinListener` - Initialisiert Konten beim Login
- Commands: `MoneyCommand`, `PayCommand`, `RequestCommand`, `RequestsCommand`

## Marry System

Das Marry System erlaubt Admins, zwei Spieler zu verheiraten und zeigt den Partner im Scoreboard und der Tablist an:

### Features

- **Verheiratung**: Admin kann zwei Spieler verheiraten
- **Scheidung**: Spieler können geschieden werden
- **Partner-Info**: Partner wird im Scoreboard und im Tablist Footer angezeigt
- **Persistenz**: Ehestatus wird in der Datenbank gespeichert
- **Auto-Laden**: Alle Ehen werden beim Start aus der DB geladen
- **Live-Updates**: Scoreboard aktualisiert sich automatisch

### Commands

#### `/marry marry <Spieler1> <Spieler2>`
- Verheirate zwei Spieler
- Beide Spieler müssen online sein
- Spieler dürfen nicht bereits verheiratet sein
- Beispiel: `/marry marry Alice Bob`
- Require: `ruinscore.command.marry`

#### `/marry divorce <Spieler>`
- Trenne ein Ehepaar
- Beispiel: `/marry divorce Alice`
- Require: `ruinscore.command.marry`

#### `/marry info <Spieler>`
- Zeige Informationen über die Ehe eines Spielers
- Beispiel: `/marry info Alice`
- Require: `ruinscore.command.marry`

### Anzeige im Scoreboard

Das Scoreboard zeigt den Partner eines verheirateten Spielers an:

```
◆ RuinsCore ◆
────────────────
Job: Schmied
Geld: 1234.50€
Partner: 💕 Bob
────────────────
Uhrzeit: 15:30:45
Ping: 45ms
```

### Anzeige in der Tablist

Der Partner wird auch im Tablist Footer angezeigt:

```
═══════════════════════════════════
Willkommen auf RuinsCore Server
═══════════════════════════════════

───────────────────────────────────
Job: Schmied │ Geld: 1234.50€
Partner: 💕 Bob
───────────────────────────────────
```

### Implementierung

- `MarryService` - Core-Logik (verheiraten, scheiden, Partner laden)
- Command: `MarryCommand`
- Datenbank-Tabelle: `player_marriages` (speichert Paare)
- Datenbank-Tabelle: `player_marriages_names` (speichert Partner-Namen offline)
- Integration in `ScoreboardService` für Anzeige

### Datenbank

**Tabelle: `player_marriages`**
- `player1_uuid` - UUID des ersten Partners
- `player2_uuid` - UUID des zweiten Partners
- `married_at` - Hochzeitsdatum

**Tabelle: `player_marriages_names`**
- `player_uuid` - UUID des Spielers
- `player_name` - Name des Partners (für Offline-Anzeige)

### Validierungen

✅ Beide Spieler müssen online sein zum Verheiraten
✅ Spieler dürfen nicht bereits verheiratet sein
✅ Partner-Namen werden für Offline-Anzeige gespeichert
✅ Automatisches Laden aus DB beim Start

## Essentials-like Commands

Das Plugin implementiert beliebte Commands aus dem Essentials Plugin:

### `/sudo <Spieler> <Command...>`
- Führt einen Command als anderer Spieler aus
- Beispiel: `/sudo JibrilPlayer say Hallo zusammen!`
- Require: `ruinscore.command.sudo`

### `/gm [Gamemode/Spieler] [Gamemode]`
- Ändert den Gamemode
- Argumente: `survival`, `creative`, `adventure`, `spectator` oder Abkürzungen (`s`, `c`, `a`, `sp`)
- Ohne Argumente: Durchschalten zwischen Gamemodes
- Beispiele:
  - `/gm creative` - Wechsle zu Creative
  - `/gm JibrilPlayer survival` - Setze Spieler auf Survival
- Require: `ruinscore.command.gamemode`

### `/fly [Spieler]`
- Aktiviert/Deaktiviert Flug-Modus
- Beispiel: `/fly JibrilPlayer`
- Require: `ruinscore.command.fly`

### `/speed <Geschwindigkeit> [Spieler]`
- Ändert die Bewegungsgeschwindigkeit (0.1 - 10.0)
- Standard: 1.0
- Beispiel: `/speed 2.0` - Doppelte Geschwindigkeit
- Require: `ruinscore.command.speed`

### `/flyspeed <Geschwindigkeit> [Spieler]`
- Ändert NUR die Flug-Geschwindigkeit (0.1 - 10.0)
- Standard: 1.0
- Beispiel: `/flyspeed 3.0` - Schneller fliegen
- Require: `ruinscore.command.flyspeed`

### `/resetallspeeds [Spieler]`
- Setzt alle Geschwindigkeiten auf Standard zurück
- Ohne Argument: Alle Spieler zurücksetzen
- Mit Argument: Nur einen Spieler zurücksetzen
- Beispiel: `/resetallspeeds` oder `/resetallspeeds JibrilPlayer`
- Require: `ruinscore.command.resetallspeeds`

### `/vanish [Spieler]`
- Macht den Spieler unsichtbar/sichtbar (offline für andere)
- Neu joinende Spieler sehen vanished Spieler nicht
- Beispiel: `/vanish JibrilPlayer`
- Require: `ruinscore.command.vanish`

### `/fireball [Geschwindigkeit]`
- Wirft einen Feuerball in die Blickrichtung
- Geschwindigkeit: 0.5 - 10.0 (Standard: 2.0)
- Beispiel: `/fireball 5.0` - Schneller Feuerball
- Require: `ruinscore.command.fireball`

### `/smite <Spieler>`
- Schlägt einen Spieler mit einem Blitz
- Verursacht Schaden
- Beispiel: `/smite JibrilPlayer`
- Require: `ruinscore.command.smite`

### `/msg <Spieler> <Nachricht...>`
- Sendet eine private Nachricht an einen Spieler
- Admins mit `ruinscore.command.msg.spy` können alle privaten Nachrichten sehen
- Beispiel: `/msg JibrilPlayer Hallo, wie geht's?`
- Require: `ruinscore.command.msg`
- Admin Spy: `ruinscore.command.msg.spy`

## Support Mode System

Das Support Mode System gibt Supportern spezielle Fähigkeiten zur Moderation:

### `/supportmode`
- Aktiviert/Deaktiviert Support Mode
- **Features beim Aktivieren:**
  - ✓ Godmode (Invulnerability + Resistance)
  - ✓ Fly (Flug aktiviert)
  - ✓ Glow-Effekt (weiß)
  - ✓ Automatische Benachrichtigungen
- **Deaktivierung:** Alle Effekte werden entfernt (nochmal `/supportmode` ausführen)
- **Auto-Cleanup:** Beim Disconnect werden alle Effekte automatisch entfernt
- Beispiel: `/supportmode` - Aktivieren/Deaktivieren
- Require: `ruinscore.command.supportmode`

### Features

- **Godmode**: Spieler kann nicht getötet werden (Invulnerability + Resistance Effect)
- **Fly**: Spieler kann frei fliegen
- **Glow**: Spieler leuchtet weiß auf für bessere Sichtbarkeit
- **State Restoration**: Ursprüngliche Zustände werden beim Deaktivieren wiederhergestellt
- **Auto-Cleanup**: Alle Effects werden entfernt beim:
  - Disconnect des Spielers
  - Plugin Disable
  - Manuelles Deaktivieren (erneut `/supportmode` ausführen)

### Implementierung

- `SupportModeService` - Zentrale Verwaltung von Support Modes
- `SupportModeListener` - Cleanup bei Player Quit
- Command: `SupportModeCommand`

## Broadcast System

Das Broadcast System erlaubt Admins, wichtige Nachrichten an alle Spieler zu senden:

### `/bc chat <Nachricht...>`
- Sendet eine Chat-Nachricht an alle Online-Spieler
- Format: `[BROADCAST] Nachricht`
- Gelb-formatiert für bessere Sichtbarkeit
- Beispiel: `/bc chat Wartungsarbeiten in 5 Minuten!`
- Require: `ruinscore.command.broadcast`

### `/bc display <Nachricht...>`
- Sendet eine rote Titel-Nachricht an alle Spieler
- Nutzt den `/title` Command
- Großformat auf dem Bildschirm
- Rot gefärbt für Aufmerksamkeit
- Beispiel: `/bc display Server wird neu gestartet!`
- Require: `ruinscore.command.broadcast`

### Implementierung

- Command: `BroadcastCommand`
- Zwei Modi: `chat` (Chat-Nachricht) und `display` (Title-Nachricht)

## Freeze System

Das Freeze System erlaubt Admins, Spieler einzufrieren (bewegungslos machen):

### `/freeze <Spieler>`
- Friere einen Spieler ein / Taue ihn auf (Toggle)
- **Features beim Einfrieren:**
  - ✓ Spieler kann sich nicht bewegen
  - ✓ Slowness Level 255 (komplette Bewegungsblockierung)
  - ✓ Jede Bewegung wird blockiert
- **Automatische Benachrichtigung:** Spieler sieht Nachricht beim Einfrieren/Auftauen
- Beispiel: `/freeze JibrilPlayer` - Einfrieren/Auftauen Toggle
- Require: `ruinscore.command.freeze`

### Implementierung

- `FreezeService` - Verwaltung gefrorener Spieler
- `FreezeInvseeListener` - Verhindert Bewegung gefrorener Spieler
- Command: `FreezeCommand`

## Invsee System

Das Invsee System erlaubt Admins, das Inventar von Spielern anzuschauen mit Live-Sync:

### `/invsee <Spieler>`
- Öffne das Inventar eines anderen Spielers
- **Features:**
  - ✓ Live-Sync: Änderungen werden sofort synchronisiert
  - ✓ Keine Duplikate: Bukkit handhabt Sync automatisch
  - ✓ Geschlossenes Inventar wird tracked
  - ✓ Auto-Cleanup beim Schließen
- **Duplikat-Schutz:** 
  - Bukkit synchronisiert Inventar-Änderungen automatisch
  - Items werden nicht geduped
  - Änderungen sind bidirektional
- Beispiel: `/invsee JibrilPlayer` - Öffne sein Inventar
- Require: `ruinscore.command.invsee`

### Implementierung

- `InvseeService` - Verwaltung offener Inventare
- `FreezeInvseeListener` - Live-Sync und Auto-Cleanup
- Command: `InvseeCommand`

## Endersee System

Das Endersee System erlaubt Admins, den Ender-Chest von Spielern anzuschauen mit Live-Sync:

### `/endersee <Spieler>`
- Öffne den Ender-Chest eines anderen Spielers
- **Features:**
  - ✓ Live-Sync: Änderungen werden sofort synchronisiert
  - ✓ Keine Duplikate: Bukkit handhabt Sync automatisch
  - ✓ Geschlossener Ender-Chest wird tracked
  - ✓ Auto-Cleanup beim Schließen
- **Duplikat-Schutz:** 
  - Bukkit synchronisiert Ender-Chest Änderungen automatisch
  - Items werden nicht geduped
  - Änderungen sind bidirektional
- Beispiel: `/endersee JibrilPlayer` - Öffne seinen Ender-Chest
- Require: `ruinscore.command.endersee`

### Implementierung

- `EnderseeService` - Verwaltung offener Ender-Chests
- `FreezeInvseeListener` - Live-Sync und Auto-Cleanup (auch für Endersee)
- Command: `EnderseeCommand`

## Chat-Radius System

Das Chat-Radius System begrenzt sichtbare Chat-Nachrichten basierend auf Entfernung:

### Features

- **Radius-Limitierung**: Nur Spieler im konfigurierten Radius (Standard: 30 Blöcke) sehen Nachrichten
- **Normale Nachrichten**: Chat-Format wird NICHT geändert
- **Konfigurierbar**: Ein/Aus und Radius-Wert in der Config änderbar
- **Bypass Permission**: Admins mit `ruinscore.chat.bypass` sehen alle Nachrichten
- **Welt-Grenzen**: Spieler in anderen Welten sehen keine Nachrichten

### Konfiguration (config.yml)

```yaml
chat:
  enabled: true    # Chat-Radius aktivieren (true/false)
  radius: 30       # Radius in Blöcken
```

### Beispiele

- **Mit Radius (enabled: true)**:
  - Spieler A schreibt in 5 Blöcken: Spieler B sieht es
  - Spieler C in 35 Blöcken: Sieht die Nachricht nicht

- **Ohne Radius (enabled: false)**:
  - Alle Spieler sehen alle Nachrichten

- **Mit Bypass Permission (ruinscore.chat.bypass)**:
  - Admin sieht alle Nachrichten überall
  - Admin kann überall schreiben, alle Spieler sehen es

### Implementierung

- `ChatRadiusService` - Radius-Logik
- `ChatRadiusListener` - AsyncPlayerChatEvent Handler
- Permission: `ruinscore.chat.bypass`

## Warning System

Das Warning System verwaltet Spieler-Verwarnungen mit automatischem Ban nach 5 Verwarnungen:

### Features

- **Verwarnungs-Tracking**: Jede Verwarnung wird gespeichert mit Admin-Name und Grund
- **Automatischer Ban**: Nach 5 Verwarnungen wird der Spieler automatisch ge-kickt
- **Verwarnungs-Info**: Zeige Verwarnungen mit verbleibenden Verwarnungen
- **Datenbank-Persistenz**: Alle Verwarnungen werden in SQLite gespeichert

### Commands

#### `/warn <Spieler> <Grund...>`
- Verwarnt einen Spieler
- Speichert Grund und Admin-Name
- Zeigt aktuelle Verwarnungsanzahl
- **Automatisch Ban nach 5 Verwarnungen**
- Beispiel: `/warn JibrilPlayer Spam`

#### `/warns <Spieler>`
- Zeigt Verwarnungsinfo eines Spielers
- Zeigt aktuelle Verwarnungen/5
- Zeigt verbleibende Verwarnungen
- Beispiel: `/warns JibrilPlayer`

### Ablauf

1. Admin gibt `/warn Spieler Grund` ein
2. Verwarn wird in Datenbank gespeichert
3. Spieler sieht Nachricht mit aktuellen Verwarnungen
4. Nach 5 Verwarnungen: **Spieler wird automatisch gekickt**
5. Admin kann `/warns Spieler` checken

### Beispiel-Ablauf

- Verwarnung 1/5: `/warn Player Spam` → Player hat 1/5 (4 verbleibend)
- Verwarnung 2/5: `/warn Player Beleidigung` → Player hat 2/5 (3 verbleibend)
- ...
- Verwarnung 5/5: `/warn Player Hack` → Player wird **automatisch gekickt**

### Implementierung

- `WarningService` - Verwaltung von Verwarnungen
- Commands: `WarnCommand`, `WarnsCommand`
- Datenbank-Tabelle: `player_warnings`

## Size Command System

Das Size Command System erlaubt Spielern, ihre Größe zu ändern (0.6 - 1.1):

### Commands

#### `/size <Größe>`
- Ändere deine eigene Spielergröße
- Größenbereich: 0.6 - 1.1
- Beispiele:
  - `/size 0.6` - Klein
  - `/size 0.8` - Etwas kleiner
  - `/size 1.0` - Normal
  - `/size 1.1` - Groß
- Require: `ruinscore.command.size`

#### `/size <Spieler> <Größe>` (Admin)
- Ändere die Größe eines anderen Spielers
- Größenbereich: 0.6 - 1.1
- Beispiel: `/size JibrilPlayer 0.6`
- Require: `ruinscore.command.size.other`

### Features

✅ **Größenbereich:** 0.6 (klein) bis 1.1 (groß)
✅ **Validierung:** Größe wird überprüft
✅ **Tab-Completion:** Vordefinierte Größen
✅ **Zwei Permissions:** Eine für sich selbst, eine für andere
✅ **Benachrichtigungen:** Spieler wird informiert

### Implementierung

- Command: `SizeCommand`
- Nutzt `player.setScale()`
- Permissions: `ruinscore.command.size` und `ruinscore.command.size.other`

## Auktionssystem

Das Auktionssystem erlaubt Spielern, Items zu verkaufen und zu kaufen:

### Features

- **Auktionen erstellen**: Spieler können Items mit Preis-GUI auktionieren
- **Auktionen kaufen**: Spieler können Items von anderen Spielern kaufen
- **Preis-GUI**: Intuitive GUI mit +1, +5, +10 und -1, -5, -10 Buttons
- **Rückgabe von eigenen Items**: Spieler können ihre eigenen Auktionen zurückholen
- **Geldsystem-Integration**: Automatischer Geldtransfer beim Kauf
- **Datenbank-Persistenz**: Alle Auktionen werden gespeichert
- **Gültigkeitsdauer**: Auktionen laufen 7 Tage (automatische Löschung abgelaufener Items)

### Commands

#### `/auction`
- Öffne die Auktionsübersicht
- Zeigt alle aktiven Auktionen auf mehreren Seiten
- Klick auf Items zum Kaufen oder auf deine Auktionen zum Zurückholen
- Require: `ruinscore.auction.use`

### Workflow - Neue Auktion erstellen

1. **Schritt 1**: Spieler gibt `/auction` ein
2. **Schritt 2**: Klick auf "§a+ Neue Auktion"
3. **Schritt 3**: Klick auf ein Item im Inventar des Spielers
   - Item wird als Kopie auf die Glasscheibe "gezogen"
4. **Schritt 4**: Klick auf "§a✓ Auktion erstellen"
5. **Schritt 5**: Preis-GUI öffnet sich
   - Aktuelle Preis-Anzeige: **Aktueller Preis: 0.00€**
   - Minus-Buttons: **-10€**, **-5€**, **-1€** (rot/orange/gelb)
   - Plus-Buttons: **+1€**, **+5€**, **+10€** (grün/dunkelgrün)
   - Buttons: **✓ Auktion erstellen**, **✗ Abbrechen**
6. **Schritt 6**: Spieler stellt Preis ein und klickt "✓ Auktion erstellen"
   - **Original-Item wird aus Inventar entfernt**
   - Auktion wird erstellt
   - Spieler sieht Bestätigungsmeldung
   - GUI schließt sich automatisch

### Workflow - Item kaufen

1. **Schritt 1**: Spieler gibt `/auction` ein
2. **Schritt 2**: Sieht die Auktionsübersicht mit allen Items
   - Jedes Item zeigt: **Verkäufer**, **Preis**
3. **Schritt 3**: Spieler klickt auf ein fremdes Item
   - **Geld wird abgezogen** (wenn vorhanden)
   - **Geld wird zum Verkäufer addiert**
   - **Item landet im Inventar**
   - Auktion wird gelöscht
   - Verkäufer wird benachrichtigt (wenn online)

### Workflow - Eigene Auktion zurückbekommen

1. **Schritt 1**: Spieler gibt `/auction` ein
2. **Schritt 2**: Sieht die Auktionsübersicht
3. **Schritt 3**: Spieler klickt auf sein **eigenes** Auktions-Item
   - **Item wird sofort ins Inventar zurückgelegt**
   - **Auktion wird gelöscht**
   - Spieler sieht Bestätigungsmeldung: "§aAuktion beendet! X Item wurde zurück ins Inventar gelegt."

### GUI-Layouts

#### Preis-GUI (36 Slots)
- **Slot 13**: Item-Vorschau (links, nicht anklickbar)
- **Slots 2-7 (Reihe 1)**: Preis-Buttons
  - Slot 2: **-10€** (Rot)
  - Slot 3: **-5€** (Orange)
  - Slot 4: **-1€** (Gelb)
  - Slot 5: **+1€** (Lime)
  - Slot 6: **+5€** (Grün)
  - Slot 7: **+10€** (Dunkelgrün)
- **Slot 31**: Preis-Display mit aktuellem Wert
- **Slot 33**: ✓ Auktion erstellen (Bestätigung)
- **Slot 34**: ✗ Abbrechen

#### Item-Auswahl GUI (27 Slots)
- **Slot 13**: Glasscheibe (Zielplatz) - nicht anklickbar
- **Spieler-Inventar**: Klick auf Items zum Auswählen
- **Slot 20**: ✗ Abbrechen
- **Slot 24**: ✓ Auktion erstellen

#### Auktionsübersicht (54 Slots)
- **Slots 0-44**: Auktions-Items (max. 45 pro Seite)
- **Slot 45**: ← Zurück (wenn nicht auf Seite 1)
- **Slot 49**: + Neue Auktion
- **Slot 53**: Weiter → (wenn nicht auf letzter Seite)
- **Seiten-Navigation**: Automatisch berechnet

### Implementierung

- `AuctionService` - Core-Logik (erstellen, löschen, abrufen)
- `AuctionGuiService` - GUI-Verwaltung (Übersicht, Erstellung, Preis)
- `AuctionListener` - Event-Handler für GUI-Interaktionen
- `AuctionCreateGuiHolder` - InventoryHolder für Item-Auswahl
- `AuctionPriceGuiHolder` - InventoryHolder für Preis-GUI
- `AuctionOverviewGuiHolder` - InventoryHolder für Auktionsübersicht
- Command: `AuctionCommand`
- Datenbank-Tabelle: `auctions`

### Datenbank

**Tabelle: `auctions`**
- `id` - Eindeutige ID
- `seller_uuid` - UUID des Verkäufers
- `seller_name` - Name des Verkäufers
- `item_data` - Serialisierte Item-Daten (Base64)
- `price` - Auktionspreis
- `created_at` - Erstellungszeitstempel
- `expires_at` - Ablaufzeitstempel (7 Tage)

### Validierungen

✅ Spieler muss Geld haben zum Kaufen
✅ Preis muss > 0€ sein
✅ Item muss ausgewählt sein
✅ Spieler kann eigene Items nicht kaufen (sondern nur zurückholen)
✅ Abgelaufene Auktionen werden nicht mehr angezeigt
✅ Maximale Item-Menge wird respektiert

### Konfiguration

Aktuell fest im Code:
- **Auktionsgültigkeitsdauer**: 7 Tage
- **Maximale Auktionen pro Seite**: 45 Items
- **Preis-Buttons**: ±1€, ±5€, ±10€

## Konfiguration

Datei: `src/main/resources/config.yml`

Wichtige Bereiche:

- `database.file` -> SQLite-Dateiname (im Plugin-Datenordner)
- `loader.command-package` -> Package für dynamische Commands
- `loader.listener-package` -> Package für dynamische Listener
- `alerts.prefix` -> Prefix für Staff-Alerts
- `jobs.*` -> Job-Regeln, Nachrichten und Multiplikatoren

## Build und Deployment

Voraussetzungen:

- Java 21
- Maven 3.9+

Build:

```powershell
mvn clean package
```

Artefakt (shaded):

- `target/RuinsCore-1.0-SNAPSHOT-shaded.jar`

Für den Servereinsatz die JAR in den `plugins`-Ordner kopieren und Server neu starten.

## Datenbanktabellen

Beim Start werden automatisch erstellt:

- `staff_alert_log` - Speichert Staff-Alert Logs
- `player_jobs` - Speichert Job-Zuweisungen pro Spieler
- `job_leaders` - Speichert Job-Leader
- `player_economy` - Speichert das Guthaben jedes Spielers
- `money_requests` - Speichert Geldanfragen (PENDING/ACCEPTED/DECLINED)
- `player_warnings` - Speichert Verwarnungen pro Spieler
- `auctions` - Speichert alle aktiven Auktionen
- `player_marriages` - Speichert verheiratete Spieler-Paare
- `player_marriages_names` - Speichert Partner-Namen für Offline-Anzeige

## Vanish System

Das Vanish-System versteckt Spieler vollständig für andere Spieler:

### Features

- **Unsichtbar**: Spieler sind für andere Spieler komplett offline
- **Persistent**: Neu joinende Spieler sehen vanished Spieler nicht
- **Auto-Hide**: VanishJoinListener versteckt alle vanished Spieler automatisch
- **Keine Effecte**: Nutzt nur `hidePlayer()` / `showPlayer()` (kein Invisibility-Effect)

### Implementierung

- `VanishService` - Zentrale Verwaltung vanished Spieler
- `VanishJoinListener` - Versteckt vanished Spieler bei neuen Joins
- Command: `VanishCommand`

## Erweiterung

### Neue Commands

1. Klasse im Package `de.satsuya.ruinsCore.commands` anlegen
2. `CoreCommand` implementieren
3. Optional Permission via `PermissionNode` zurückgeben
4. Wird beim Start automatisch geladen

### Neue Listener

1. Klasse im Package `de.satsuya.ruinsCore.listeners` anlegen
2. `Listener` implementieren und `@EventHandler` setzen
3. Wird beim Start automatisch registriert

## Hinweise

- Die Listener-/Command-Loader arbeiten package-basiert; falsche Package-Namen in `config.yml` verhindern Auto-Load.
- Vanish-Status wird NICHT persistiert über Restarts (nur im RAM während des Servers)
- Admin-Features sind standardmäßig auf `op` gesetzt (siehe `plugin.yml`)

