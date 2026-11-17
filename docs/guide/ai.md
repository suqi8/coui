# Writing with AI

COUI ships an agent skill, `coui-app`, that teaches AI coding assistants to build screens the way ColorOS 17 builds them: a page scaffold with a collapsing top bar, rows that are preferences inside grouped cards, overlays that blur the page behind them, and bilingual strings. Without it, assistants tend to rebuild COUI widgets out of `Box` and `Text`, add their own dividers and paddings, or open overlays outside a `Scaffold`.

## Install the skill

The skill is a folder in this repository: `.claude/skills/coui-app/`.

- **Claude Code**: copy the whole folder into your project's `.claude/skills/` (or `~/.claude/skills/` for all projects). It triggers on its own when the code imports `io.github.suqi8.coui.kmp` or when you ask for a ColorOS-style screen.
- **Other assistants** (Cursor, Copilot, Codex, …): point the assistant's rules file at `SKILL.md` and its `references/` folder, or paste the rules below. The docs site also serves a short plain-text index, `llms.txt`, next to this guide (`/coui/llms.txt`).

| File | Contents |
| --- | --- |
| `SKILL.md` | Setup, core rules, page recipe, rows and cards, material blur, strings, finishing checklist |
| `references/scaffold.md` | A copyable page scaffold (`CouiPage`, `CouiPageScaffold`) and the blur bridge (`panelBlur`, popup and button material providers) |
| `references/components.md` | ColorOS widget → COUI component map, Overlay vs Window |
| `references/pitfalls.md` | Mistakes seen in practice and how to avoid them |

## The rules in short

1. Everything sits inside a COUI `Scaffold`; `Overlay*` dialogs, sheets and menus render in its popup host.
2. Colours from `COUITheme.colorScheme`, text from `COUITheme.textStyles`; no hard-coded UI colours.
3. Settings layout is `PreferenceCategory` + `Card` + `CardListGroup` + preference rows. The group draws the dividers and corner positions: no `HorizontalDivider`s, no padding around row icons.
4. Use the library component that matches the ColorOS widget instead of a look-alike.
5. Material blur needs plumbing: record the page with `layerBackdrop`, hand it to the top bar glow, dialogs, popups and bars that sit outside the recording, and keep the opaque fallback when runtime shaders are missing.
6. User-facing text goes to Compose resources (`values/` and `values-zh/`); store ids, not display text, as state.
7. Your own composables follow the library's parameter order: required parameters, `modifier`, defaulted parameters, trailing content.

## Ask for screens like this

- "Add a display settings page with a dark mode switch, a text size dropdown and a wallpaper entry."
- "Turn this list into a ColorOS settings home with a search capsule in the top bar."
- "Show a confirmation dialog with the COUI 17 material blur before clearing the cache."

The assistant should answer with a `CouiPage` (or a `Scaffold` + `TopAppBar`), grouped cards of preferences, and strings in both languages.
