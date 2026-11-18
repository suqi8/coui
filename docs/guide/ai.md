# Writing with AI

COUI ships an agent skill, `coui-app`, that teaches AI coding assistants to build screens the way ColorOS 17 builds them: a page scaffold with a collapsing top bar, rows that are preferences inside grouped cards, overlays that blur the page behind them, and bilingual strings. Without it, assistants tend to rebuild COUI widgets out of `Box` and `Text`, add their own dividers and paddings, or open overlays outside a `Scaffold`.

## Install the skill

The skill is a folder in the COUI repository: `.claude/skills/coui-app/`. Copy the whole folder; the files reference each other by relative path.

### Claude Code

Copy it into your project's `.claude/skills/` (or `~/.claude/skills/` to use it in every project):

```bash
mkdir -p .claude/skills
cp -r <path-to-COUI>/.claude/skills/coui-app .claude/skills/
```

```powershell
New-Item -ItemType Directory -Force .claude\skills
Copy-Item -Recurse <path-to-COUI>\.claude\skills\coui-app .claude\skills\
```

Restart the session, then check that `coui-app` appears in the skill list (`/skills`). It loads on its own when the code imports `io.github.suqi8.coui.kmp` or when you ask for a ColorOS-style screen; you can also ask for it by name ("use the coui-app skill").

### Other assistants

Copy the same folder anywhere in your project and reference it from the assistant's rules file:

| Assistant | Rules file |
| --- | --- |
| Cursor | `.cursor/rules/coui.mdc` |
| GitHub Copilot | `.github/copilot-instructions.md` |
| Codex and tools that read `AGENTS.md` | `AGENTS.md` |

```md
When writing UI with the COUI library (io.github.suqi8.coui.kmp), follow
.claude/skills/coui-app/SKILL.md and the files in its references/ folder.
```

If the assistant cannot read files, paste the rules below. The docs site also serves a short plain-text index, `llms.txt` (`/coui/llms.txt`).

### Updating

The skill follows the library version it ships with. When you upgrade COUI, copy the folder again.

| File | Contents |
| --- | --- |
| `SKILL.md` | Setup, core rules, page recipe, rows and cards, material blur, strings, finishing checklist |
| `references/scaffold.md` | A copyable page scaffold (`CouiPage`, `CouiPageScaffold`) and the blur bridge (`panelBlur`, popup and button material providers) |
| `references/components.md` | ColorOS widget → COUI component map, Overlay vs Window |
| `references/pitfalls.md` | Mistakes seen in practice and how to avoid them |
| `references/migration.md` | Miuix / COUI 1.x → 2.0 renames, for porting old code |

## The rules in short

1. Everything sits inside a COUI `Scaffold`; `Overlay*` dialogs, sheets and menus render in its popup host.
2. Colours from `COUITheme.colorScheme`, text from `COUITheme.textStyles`; no hard-coded UI colours.
3. Settings layout is `PreferenceCategory` + `Card` + `CardListGroup` + preference rows. The group draws the dividers and corner positions: no `HorizontalDivider`s, no padding around row icons.
4. Use the library component that matches the ColorOS widget instead of a look-alike.
5. Material blur needs plumbing: record the page with `layerBackdrop`, hand it to the top bar glow, dialogs, popups and bars that sit outside the recording, and keep the opaque fallback when runtime shaders are missing.
6. User-facing text goes to Compose resources (`values/` and `values-zh/`); store ids, not display text, as state.
7. Your own composables follow the library's parameter order: required parameters, `modifier`, defaulted parameters, trailing content.
8. A search typed into under a top bar is linked with the bar like the ColorOS Settings search: `TopAppBarSearchBar` + `rememberTopAppBarSearchState`, the page fading out with `Modifier.topAppBarSearchContent` and the results coming in with `Modifier.topAppBarSearchResults`. No static capsule that filters in place.
9. A list popup shares a `Box` with the button that opens it; directly in a top bar `actions` row it pushes the button aside while it is shown.

## Without the skill

Every docs page has buttons above the title: **Copy as Markdown** puts the page source on the clipboard, **View Markdown** opens it, and **Ask ChatGPT** / **Ask Claude** open a chat that reads the page first. For an assistant that reads the web, point it at:

- <a href="/coui/llms.txt" target="_blank">llms.txt</a>: the rules in short and the main links.
- <a href="/coui/llms-full.txt" target="_blank">llms-full.txt</a>: the whole English guide and component reference in one file.
- Any page with `.md` added to its address, for example <a href="/coui/guide/migration.md" target="_blank">guide/migration.md</a>.

To port an existing Miuix or COUI 1.x project, ask it to follow [Migrating from Miuix](./migration) or [Upgrading](./upgrading); the skill carries the same tables in `references/migration.md`.

## Sketch first

To show an assistant what you want instead of describing it, lay the screens out in [COUI Canvas](./canvas) and copy its prompt: every part is already named after its COUI component. To check how a finished component looks and behaves, open the <a href="/coui/example/" target="_blank">example app in the browser</a>.

## Ask for screens like this

- "Add a display settings page with a dark mode switch, a text size dropdown and a wallpaper entry."
- "Turn this list into a ColorOS settings home with a search capsule in the top bar."
- "Show a confirmation dialog with the COUI 17 material blur before clearing the cache."

The assistant should answer with a `CouiPage` (or a `Scaffold` + `TopAppBar`), grouped cards of preferences, and strings in both languages.
