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
- 🎂 **Auktionssystem**: Spieler können Items kaufen und verkaufen
- 👰 **Marry System**: Spieler können verheiratet werden mit Partner-Anzeige
- 📋 **Report System**: Spieler-Meldungen für Supporter-Verwaltung
- 🚪 **Door Lock System**: Türen mit Whitelist-Verwaltung
- ✨ **Pentagram System**: Visuelle Feuer-Effekte

## Architektur

- Modulsystem mit Lifecycle über `ModuleManager` und `PluginModule`
- Dynamischer Command-Loader aus `loader.command-package` (Reflection + Auto-Registrierung)
- Dynamischer Event-Loader aus `loader.listener-package`
- SQLite-Datenhaltung über `DatabaseManager` (lokale Datei im Plugin-Datenordner)
- Utility-Layer für Logging (`LoggerUtil`) und Staff-Benachrichtigungen (`StaffAlertUtil`)
- Permission-Handling mit `PermissionNode`-Enum + `PermissionManager`

| System | Package | Features |
|--------|---------|----------|
| **Report System** | `de.satsuya.ruinsCore.core.report` | Spieler-Meldungen, Supporter-GUI, Verwaltung |
| **Door Lock System** | `de.satsuya.ruinsCore.core.turlock` | Tür-Sperren, Whitelist, Admin-Management |
| **Pentagram System** | `de.satsuya.ruinsCore.core.pentagram` | Feuer-Effekte, 10s Animation |

### Report System - 3 Dateien
- `ReportService` - Datenbank-Verwaltung
- `ReportGuiService` - GUI-Erstellung
- `ReportGuiListener` - Event-Handling
- `ReportCommand` - Command-Logik
- `ReportGuiHolder` - InventoryHolder

### Door Lock System - 5+ Dateien
- `DoorLockService` - Core-Logik
- `DoorLockGuiService` - GUI-Management
- `DoorLockListener` - Shift+Rechtsklick Handler
- `DoorLockGuiListener` - GUI Click Handler
- `DoorLockGuiHolder` - Hauptmenü InventoryHolder
- `DoorLockWhitelistGuiHolder` - Whitelist InventoryHolder

### Pentagram System - 2 Dateien
- `PentagramService` - Partikel-Logik
- `PentagramCommand` - Command

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
- `/playtime` - Zeige deine Spielzeit an
- `/playtime <Spieler>` (Admin) - Zeige Spielzeit eines anderen Spielers
- `/report <Spieler> <Grund...>` - Melde einen Spieler
- `/report gui` (Supporter) - Öffne Report-Verwaltungs-GUI
- `/pentagram` (Admin) - Erstelle ein Feuer-Pentagramm

## Welcome System

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
- `ruinscore.command.playtime` (view own playtime)
   - `ruinscore.command.playtime.other` (view other player playtimes)
   - `ruinscore.command.report.use` (use report system)
   - `ruinscore.command.report.view` (view reports via GUI)
   - `ruinscore.command.pentagram` (create pentagrams)
   - `ruinscore.command.turlock.admin` (manage all door locks)

## Playtime Tracking System

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

## Playtime Tracking System

Das Playtime Tracking System verfolgt die Spielzeit jedes Spielers:

### Features

- **Automatisches Tracking**: Spielzeit wird beim Login/Logout automatisch gemessen
- **Persistente Speicherung**: Alle Spielzeiten werden in SQLite gespeichert
- **Formatierte Ausgabe**: Spielzeit in Tagen, Stunden, Minuten anzeigen
- **Permission System**: Eigene Spielzeit sichtbar, andere Spielzeiten mit Permission

### Commands

#### `/playtime`
- Zeige deine eigene Spielzeit an
- Beispiel: `/playtime`
- Require: `ruinscore.command.playtime`

#### `/playtime <Spieler>` (Admin)
- Zeige die Spielzeit eines anderen Spielers
- Beispiel: `/playtime Alice`
- Require: `ruinscore.command.playtime.other`

### Format

Die Spielzeit wird so angezeigt:
- **Mit vielen Tagen**: `5 Tage, 12 Stunden, 30 Minuten`
- **Mit Stunden**: `15 Stunden, 45 Minuten`
- **Mit Minuten**: `45 Minuten, 30 Sekunden`
- **Nur Sekunden**: `120 Sekunden`

### Implementierung

- `PlaytimeService` - Core-Logik (Tracking, Speicherung, Formatierung)
- `PlaytimeListener` - Event-Handler für Login/Quit
- Command: `PlaytimeCommand`
- Datenbank-Tabelle: `player_playtime`

### Datenbank

**Tabelle: `player_playtime`**
- `player_uuid` - UUID des Spielers (Primary Key)
- `playtime_millis` - Gesamte Spielzeit in Millisekunden

## Welcome System

Das Welcome System begrüßt neue Spieler mit einer detaillierten Welcome Message:

### Features

- **Automatische Erkennung**: Neue Spieler werden automatisch erkannt
- **Schöne Willkommens-Nachricht**: Eine formatierte Nachricht mit Tipps
- **Server-Broadcast**: Alle Spieler werden über den neuen Spieler benachrichtigt
- **Persistente Speicherung**: Neue Spieler werden in der DB markiert
- **Nur einmal pro Spieler**: Die Welcome Message wird nur beim ersten Login gezeigt

### Willkommens-Nachricht

Die Welcome Message zeigt:
- Große Willkommens-ASCII-Box
- Personalisierte Begrüßung mit Spielernamen
- Wichtige Commands (`.job`, `/money`, `/playtime`, `/help`)
- Informationen über Berufe
- Info über die Wirtschaft und Auktionen
- Wie man Hilfe bekommt

### Broadcast

Alle Online-Spieler sehen:
```
✨ Alice hat den Server zum ersten Mal betreten! Willkommen! 🎉 ✨
```

### Implementierung

- `WelcomeService` - Verwaltung von Welcome Messages
- `WelcomeListener` - Listener für PlayerJoinEvent
- Datenbank-Tabelle: `player_welcome`

### Datenbank

**Tabelle: `player_welcome`**
- `player_uuid` - UUID des Spielers (Primary Key)
- `player_name` - Name des Spielers
- `first_join` - Timestamp des ersten Logins

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
- `player_sizes` - Speichert die Spielergröße
- `auctions` - Speichert alle aktiven Auktionen
- `player_marriages` - Speichert verheiratete Spieler-Paare
- `player_marriages_names` - Speichert Partner-Namen für Offline-Anzeige
- `player_playtime` - Speichert die Spielzeit jedes Spielers
- `player_welcome` - Speichert welche Spieler bereits begrüßt wurden
- `door_locks` - Speichert alle Türschlösser mit Einstellungen
- `door_lock_whitelist` - Speichert Whitelist-Spieler für jede Tür
- `reports` - Speichert alle Spieler-Meldungen mit Status

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

## Report System

Das Report System erlaubt Spielern, andere Spieler zu melden, und Supportern, diese Reports über ein GUI zu verwalten:

### Features

- **Spieler-Meldungen**: Spieler können andere melden mit Grund
- **Report-GUI**: Supporter können alle Reports in einem GUI einsehen
- **Detail-Ansicht**: Klick auf Report zeigt alle Informationen
- **Als bearbeitet markieren**: Reporter können Reports als bearbeitet markieren und löschen
- **Datenbank-Persistenz**: Alle Reports werden in SQLite gespeichert
- **Status-Tracking**: Reports haben Status (OPEN/GESCHLOSSEN)

### Commands

#### `/report <Spieler> <Grund...>`
- Melde einen Spieler
- Grund wird in DB gespeichert
- Gemeldeter Spieler sieht Benachrichtigung
- Beispiel: `/report JibrilPlayer Spam in Chat`
- Require: `ruinscore.command.report.use`

#### `/report gui`
- Öffne die Report-Übersicht (nur für Supporter)
- Zeigt alle offenen Reports
- Klick auf Report zur Detail-Ansicht
- Require: `ruinscore.command.report.view`

### Report-Übersicht-GUI

Das Report-GUI zeigt alle offenen Reports in einer übersichtlichen Liste:

**Layout:**
- **Slots 0-44**: Report-Items mit Reporter, Beschuldigt, Grund
- **Slot 53 (oder letzter Slot)**: Zurück-Button
- **Dynamische Größe**: GUI vergrößert sich automatisch basierend auf Anzahl der Reports

**Report-Item anzeigen:**
```
Beschuldigt: JibrilPlayer
Von: OtherPlayer  
Grund: Spam in...
ID: 42
Klick zum Einsehen
```

### Report-Detail-GUI

Die Detail-Ansicht zeigt alle Informationen zu einem Report:

**Layout (27 Slots):**
- **Slot 10**: Reporter-Info (PLAYER_HEAD mit Name und UUID)
- **Slot 12**: Pfeiltrenner (→)
- **Slot 14**: Beschuldigter-Info (PLAYER_HEAD mit Name und UUID)
- **Slot 19**: Grund-Info (BOOK mit vollständiger Grund)
- **Slot 23**: Zeit-Info (CLOCK mit Erstellungszeit)
- **Slot 24**: ✓ Bearbeitet (Grüner Block - Report löschen)
- **Slot 26**: ← Zurück (Barrier)

**Funktionen:**
- Klick auf "Bearbeitet" markiert Report als bearbeitet
- Report wird aus DB gelöscht
- Spieler sieht Bestätigung
- Zurück zur Übersicht

### Ablauf

1. **Spieler meldet**: `/report Hacker Speedhack`
2. **Report wird gespeichert**: In `reports` Tabelle mit Status OPEN
3. **Supporter öffnet GUI**: `/report gui`
4. **Supporter sieht Reports**: List aller offenen Reports
5. **Supporter klickt auf Report**: Detail-View öffnet sich
6. **Supporter liest Informationen**: Reporter, Beschuldigt, Grund, Zeit
7. **Supporter markiert als bearbeitet**: Klick auf grünen Block
8. **Report wird gelöscht**: Aus DB entfernt, GUI aktualisiert

### Implementierung

- `ReportService` - Core-Logik (create, getOpenReports, closeReport)
- `ReportGuiService` - GUI-Verwaltung (Übersicht, Detail)
- `ReportGuiListener` - Event-Handler für GUI-Interaktionen
- `ReportGuiHolder` - InventoryHolder für Reports
- Command: `ReportCommand`
- Datenbank-Tabelle: `reports`

### Datenbank

**Tabelle: `reports`**
- `id` - Eindeutige Report-ID
- `reporter_uuid` - UUID des Reporters
- `reporter_name` - Name des Reporters
- `reported_uuid` - UUID des Beschuldigten
- `reported_name` - Name des Beschuldigten
- `reason` - Grund der Meldung
- `created_at` - Erstellungszeit
- `status` - Status (OPEN/CLOSED)

### Validierungen

✅ Spieler kann sich nicht selbst melden
✅ Spieler wird benachrichtigt bei Meldung
✅ Nur Supporter mit Permission können Reports einsehen
✅ Alle Reports werden persistent gespeichert

## Door Lock System

Das Door Lock System erlaubt Spielern, Türen zu schließen und Zugriff zu verwalten:

### Features

- **Türschlösser**: Spieler können Türen mit SHIFT+Rechtsklick schließen
- **Öffentlich/Privat**: Türen können öffentlich oder privat sein
- **Whitelist-System**: Private Türen können mit Whitelist verwaltet werden
- **Admin-Override**: Admins mit Permission können alle Türen verwalten
- **Wache-Bypass**: Die Wache kann alle Türen öffnen
- **GUI-Verwaltung**: Intuitive GUI für Türschloss-Verwaltung
- **Datenbank-Persistenz**: Alle Türschlösser werden gespeichert

### Commands

#### Türschloss-Verwaltung (Shift + Rechtsklick)
- **Auf verschlossene Tür**: Öffnet Verwaltungs-GUI
- **Auf neue Tür**: Erstellt neues Türschloss und öffnet GUI
- Nur Besitzer oder Admins können verwalten
- Require: Keine (jeder kann seine Türen sperren)

### Türschloss-GUI

**Hauptmenü (27 Slots):**
- **Slot 11**: Öffentlich/Privat Button (Toggle)
- **Slot 13**: Info-Anzeige (Besitzer, Status)
- **Slot 15**: Whitelist-Button (nur wenn privat)
- **Slot 26**: Zurück-Button

**Whitelist-GUI (54 Slots):**
- **Slots 0-47**: Spieler auf Whitelist (klick zum Entfernen)
- **Slot 48**: ➕ Spieler Hinzufügen
- **Slot 49**: ← Zurück

**Spieler-Auswahl-GUI (54 Slots):**
- **Slots 0-48**: Online-Spieler (nicht auf Whitelist)
- **Slot 49**: ← Zurück

### Ablauf

1. **Tür erstellen**: Shift + Rechtsklick auf neue Tür
2. **GUI öffnet sich**: Hauptmenü mit Optionen
3. **Status ändern**: Klick auf Öffentlich-Button
   - Grüner Button = Öffentlich (alle können öffnen)
   - Roter Button = Privat (nur Whitelist)
4. **Whitelist verwalten** (nur bei privaten Türen):
   - Klick auf Whitelist-Button
   - Klick auf Spieler zum Entfernen oder ➕ zum Hinzufügen

### Features beim Öffnen

- **Öffentlich**: Jeder kann die Tür öffnen
- **Privat ohne Whitelist**: Nur der Besitzer kann öffnen
- **Privat mit Whitelist**: Besitzer + Whitelist-Spieler
- **Wache-Bypass**: Wache kann immer öffnen
- **Admin-Override**: Admins können immer verwalten

### Implementierung

- `DoorLockService` - Core-Logik (erstellen, Whitelist, Permissions)
- `DoorLockGuiService` - GUI-Verwaltung (Hauptmenü, Whitelist)
- `DoorLockListener` - Event-Handler (Shift+Rechtsklick)
- `DoorLockGuiListener` - Event-Handler für GUI-Clicks
- `DoorLockGuiHolder` - InventoryHolder für Hauptmenü
- `DoorLockWhitelistGuiHolder` - InventoryHolder für Whitelist-GUIs
- Datenbank-Tabellen: `door_locks`, `door_lock_whitelist`

### Datenbank

**Tabelle: `door_locks`**
- `door_key` - Eindeutiger Schlüssel (world_x_y_z)
- `world`, `x`, `y`, `z` - Tür-Position
- `owner_uuid` - UUID des Besitzers
- `owner_name` - Name des Besitzers
- `is_public` - Öffentlich oder privat
- `created_at` - Erstellungszeit

**Tabelle: `door_lock_whitelist`**
- `door_key` - Referenz zur Tür
- `player_uuid` - UUID des Spielers
- `player_name` - Name des Spielers

### Validierungen

✅ Nur Besitzer oder Admins können Türschloss verwalten
✅ Wache kann alle Türen öffnen
✅ Whitelist nur bei privaten Türen
✅ Spieler können nicht zweimal auf Whitelist sein
✅ Tür-Position wird korrekt gespeichert

## Pentagram System

Das Pentagram System erzeugt Feuer-Pentagramme um die Position eines Spielers:

### Features

- **Feuer-Pentagramme**: Erzeugt 5-zackige Sterne aus Feuer-Partikeln
- **Automatische Löschung**: Pentagramm hält 10 Sekunden
- **Pro Spieler**: Jeder Spieler kann nur sein eigenes Pentagramm haben
- **Visuelle Effekte**: FLAME-Partikel in stabilen Linien
- **Live-Animation**: Pentagramm wird 5x pro Sekunde neu gezeichnet

### Commands

#### `/pentagram`
- Erstelle ein Pentagramm um deine Position
- Hält 10 Sekunden lang
- Nur eine Pentagramm pro Spieler aktiv
- Erneutes Ausführen überschreibt vorheriges
- Beispiel: `/pentagram`
- Require: `ruinscore.command.pentagram`

### Visuelle Darstellung

Das Pentagramm besteht aus:
- **5 Spitzen** in einem Kreis (Radius: ca. 2 Blöcke)
- **FLAME-Partikel** (Feuer)
- **Verbindungslinien** zwischen den Spitzen (5-zackiger Stern)
- **Animierte Anzeige** (wird alle 0.1 Sekunden neu gezeichnet)

### Ablauf

1. Spieler gibt `/pentagram` ein
2. Pentagramm wird um seine Position erzeugt
3. Visuelle Effekte sind sofort sichtbar
4. Nach 10 Sekunden verschwindet das Pentagramm automatisch
5. Spieler kann neues Pentagramm erstellen

### Implementierung

- `PentagramService` - Core-Logik (Erstellung, Animation, Löschung)
- Command: `PentagramCommand`
- Nutzt `BukkitScheduler` für repeating Tasks
- Berechnet Pentagramm-Spitzen mathematisch
- Spawnt FLAME-Partikel in Linien

### Konfiguration

Aktuell fest im Code:
- **Radius**: ca. 2 Blöcke
- **Update-Rate**: Alle 2 Ticks (0.1 Sekunde)
- **Dauer**: 10 Sekunden (200 Ticks)
- **Partikel-Abstand**: 0.4 Blöcke pro Partikel
- **Partikel-Typ**: FLAME

### Technische Details

- Verwendet trigonometrische Funktionen (Math.cos/Math.sin)
- Berechnet 5 Spitzen à 72° Winkel-Abstand
- Verbindet jede Spitze mit den nächsten zwei Spitzen
- Spawnt Partikel entlang aller Linien
- Cleanup erfolgt automatisch nach 10 Sekunden

## Hinweise

- Die Listener-/Command-Loader arbeiten package-basiert; falsche Package-Namen in `config.yml` verhindern Auto-Load.
- Vanish-Status wird NICHT persistiert über Restarts (nur im RAM während des Servers)
- Admin-Features sind standardmäßig auf `op` gesetzt (siehe `plugin.yml`)

