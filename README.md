# ElytrymFly

Плагин полёта на элитре для Paper/Spigot 1.21+: даёт игрокам с надетой элитрой полёт «как в креативе», снимает его при уходе в PvP-бой и тратит прочность элитры во время полёта. Умеет работать как в одиночку (по ванильным ударам), так и в связке с **CombatLogX** или **GreatCombat** — сам определяет, какой из них установлен.

## Возможности

- **`/ffly`** — включает/выключает полёт, если на игроке надета элитра.
- **PvP-интеграция** — полёт автоматически снимается, когда игрок попадает в бой, и (по желанию) его нельзя включить, пока бой идёт. Источник информации о бое настраивается:
  - `NONE` — ваниль: прямой удар игрок-игрок или снаряд (стрела/трезубец), выпущенный игроком;
  - `COMBATLOGX` — тег боя из [CombatLogX](https://www.spigotmc.org/resources/combatlogx.31689/);
  - `GREATCOMBAT` — тег боя из [GreatCombat](https://github.com/Enc0urager/GreatCombat);
  - `AUTO` (по умолчанию) — сам определяет, что из этого установлено на сервере.
- **Прочность элитры** — во время полёта элитра тратит прочность по интервалу: фиксированное количество очков или процент от максимума за тик, с учётом чар Unbreaking. Есть порог "нельзя взлететь при низкой прочности", предупреждение о низкой прочности и выбор — просто снять полёт при обнулении прочности или по-настоящему сломать элитру.
- **Ограничения полёта** — запрет по мирам, кулдаун между переключениями `/ffly`, автоматическое снятие при смене режима игры.
- **Все сообщения** вынесены в `messages.yml`, поддерживаются лёгаси-цвета (`&a`) и hex (`&#RRGGBB`).
- **Мягкие зависимости** — плагин работает и без CombatLogX/GreatCombat, просто переходит в ванильный режим.

## Требования

- Java 21+
- Paper 1.21+ (или форк на его основе)
- Опционально: [CombatLogX](https://www.spigotmc.org/resources/combatlogx.31689/) или [GreatCombat](https://github.com/Enc0urager/GreatCombat) для более точного PvP-детекта

## Установка

1. Скачайте `elytrymfly-<версия>.jar` из [Releases](../../releases) или соберите сами (см. ниже).
2. Положите jar в папку `plugins/` сервера.
3. Перезапустите сервер — создадутся `config.yml` и `messages.yml`.
4. Настройте `config.yml` под себя и перезагрузите: `/elytrymfly reload`.

## Команды и права

| Команда | Описание | Право |
|---|---|---|
| `/ffly` | Включить/выключить полёт (нужна надетая элитра) | `elytrymfly.use` |
| `/elytrymfly reload` | Перезагрузить `config.yml` и `messages.yml` | `elytrymfly.admin` |
| `/elytrymfly status` | Показать, какая PvP-интеграция активна сейчас | `elytrymfly.admin` |

| Право | Описание | По умолчанию |
|---|---|---|
| `elytrymfly.use` | Доступ к `/ffly` | `op` |
| `elytrymfly.bypass` | Полёт без элитры, игнорирует бой/мир/кулдаун/прочность | `op` |
| `elytrymfly.admin` | Доступ к `/elytrymfly` | `op` |

## Конфигурация

### config.yml — настройки

```yaml
combat:
  plugin: AUTO                      # AUTO | NONE | COMBATLOGX | GREATCOMBAT
  disable-flight-on-tag: true       # снимать полёт при входе в бой
  block-flight-while-tagged: true   # нельзя взлететь во время боя
  apply-hit-effects-on-tag: true    # выдавать hit-effects при снятии полёта

flight:
  restricted-worlds: []             # миры, где /ffly запрещён
  toggle-cooldown-seconds: 0        # кулдаун между переключениями, 0 = выкл
  disable-on-gamemode-change: true  # снимать при смене режима игры

durability:
  enabled: true
  mode: FIXED                       # FIXED | PERCENT
  interval-ticks: 100               # как часто тратить прочность
  fixed-amount: 1                   # очков за интервал (FIXED)
  percent-amount: 2.0               # % от максимума за интервал (PERCENT)
  respect-unbreaking: true          # учитывать шанс от Unbreaking
  min-durability-percent-to-fly: 5  # ниже этого % взлететь нельзя
  low-durability-warning-percent: 15
  warning-cooldown-seconds: 10
  on-break: DISABLE_FLIGHT          # DISABLE_FLIGHT | BREAK_ELYTRA
```

Полный файл с комментариями — в [`src/main/resources/config.yml`](src/main/resources/config.yml).

### messages.yml — все текстовые сообщения

Поддерживаются коды `&a` и `&#RRGGBB`. Полный список ключей и плейсхолдеров — в [`src/main/resources/messages.yml`](src/main/resources/messages.yml).

## Сборка из исходников

```bash
mvn clean package
```

Готовый jar появится в `target/elytrymfly-<версия>.jar`.

## Совместимость

Плагин компилируется под Paper API 1.21 и использует Adventure API для сообщений. CombatLogX и GreatCombat подключены как soft-depend (`provided` в pom.xml) — их классы не попадают в итоговый jar, интеграция активируется только если соответствующий плагин реально установлен на сервере.
