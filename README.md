# RuinsCore

RuinsCore ist ein modular aufgebautes Paper-Plugin (1.21.4) mit dynamischem Laden von Commands und Listenern, SQLite-Backend und einem erweiterten Jobsystem mit GUI-Verwaltung.

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

## Permissions

Aus `plugin.yml`:

- `ruinscore.command.ping`
- `ruinscore.command.staffalert`
- `ruinscore.job.manage`
- `ruinscore.job.gui`
- `ruinscore.staff.alert.send`
- `ruinscore.staff.alert.receive`

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

- `staff_alert_log`
- `player_jobs`
- `job_leaders`

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
- `WacheDoorListener` ist aktuell nur als Platzhalter vorhanden (keine aktive Logik).

