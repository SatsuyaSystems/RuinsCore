# RuinsCore

RuinsCore ist ein modular aufgebautes Paper-Plugin mit dynamischem Laden von Commands und Events.

## Features

- Modulsystem mit Lifecycle (`PluginModule`, `ModuleManager`)
- Dynamischer Command-Loader aus Package inkl. Tab-Completion
- Dynamischer Event-Loader aus Package
- SQLite-DatabaseManager mit lokaler Datei (`plugins/RuinsCore/ruinscore.db`)
- Logging-Utility (`LoggerUtil`)
- Staff-Alert-Utility (`StaffAlertUtil`)
- Permission-System über `PermissionNode`-Enum + `PermissionManager`
- Jobsystem inkl. GUI-Verwaltung (Jobs: `holzfaeller`, `miner`, `builder`, `fischer`, `bauer`)
- Holzfäller-Restriktionen für Nicht-Jobmitglieder beim Holzabbau
- Miner-Restriktion: nur Miner/Builder dürfen Erze abbauen
- Fischer-Restriktion: nur Fischer können Epic-Loot angeln
- Bauer-Restriktion: nur Bauer dürfen Nahrungspflanzen anbauen/abbauen und Essen craften

## Pakete

- Commands: `de.satsuya.ruinsCore.commands`
- Listener: `de.satsuya.ruinsCore.listeners`

## Build

```powershell
mvn clean package
```

## Neue Commands hinzufügen

1. Klasse im Package `de.satsuya.ruinsCore.commands` erstellen.
2. `CoreCommand` implementieren.
3. Optional Permission über `PermissionNode` zurückgeben.
4. Command wird beim Start automatisch geladen.

## Neue Events hinzufügen

1. Listener-Klasse im Package `de.satsuya.ruinsCore.listeners` erstellen.
2. `Listener` implementieren und `@EventHandler` Methoden setzen.
3. Listener wird beim Start automatisch registriert.

## Jobsystem

- Command: `/job <assign|remove|list|gui>`
- Jobs aktuell: `holzfaeller`, `miner`, `builder`, `fischer`, `bauer`
- Nicht-Holzfäller erhalten beim Holzabbau Lähmung und bekommen nur mit Chance Planken-Drops.
- Builder kann alles abbauen (inkl. Holz ohne Lähmung und Erze).
- Nur Miner oder Builder können Erze abbauen. Alle anderen werden beim Erzabbau blockiert.
- Nur Fischer können Epic-Loot beim Angeln behalten. Alle anderen bekommen nur Fisch.
- Nur Bauer dürfen essensrelevante Pflanzen anbauen/abbauen und essbare Items craften.
- Werte konfigurierbar in `src/main/resources/config.yml` unter `jobs.holzfaeller.restrictions`.

