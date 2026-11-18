# 用 AI 编写

COUI 附带一个 AI 编程助手用的技能 `coui-app`，教它按 ColorOS 17 的方式搭页面：可折叠顶栏的页面脚手架、分组卡片里的设置项、能模糊背后页面的弹窗与菜单、中英双语文案。不装它时，AI 常会用 `Box` 和 `Text` 自己拼出 COUI 控件的样子、手动加分割线和边距，或者在 `Scaffold` 外面弹出浮层。

## 安装技能

技能是 COUI 仓库里的一个目录：`.claude/skills/coui-app/`。请复制整个目录，里面的文件通过相对路径互相引用。

### Claude Code

复制到你项目的 `.claude/skills/`（想让所有项目都能用就放到 `~/.claude/skills/`）：

```bash
mkdir -p .claude/skills
cp -r <COUI 仓库路径>/.claude/skills/coui-app .claude/skills/
```

```powershell
New-Item -ItemType Directory -Force .claude\skills
Copy-Item -Recurse <COUI 仓库路径>\.claude\skills\coui-app .claude\skills\
```

重新开一个会话，确认技能列表（`/skills`）里出现了 `coui-app`。代码里引用了 `io.github.suqi8.coui.kmp`，或者你要求做 ColorOS 风格页面时，它会自动生效；也可以直接点名「用 coui-app 技能」。

### 其他助手

把同一个目录复制到项目里任意位置，再在助手的规则文件里引用它：

| 助手 | 规则文件 |
| --- | --- |
| Cursor | `.cursor/rules/coui.mdc` |
| GitHub Copilot | `.github/copilot-instructions.md` |
| Codex 等读取 `AGENTS.md` 的工具 | `AGENTS.md` |

```md
When writing UI with the COUI library (io.github.suqi8.coui.kmp), follow
.claude/skills/coui-app/SKILL.md and the files in its references/ folder.
```

助手读不了文件时，就粘贴下面的规则摘要。文档站点还提供了一份纯文本简短索引 `llms.txt`（`/coui/llms.txt`）。

### 更新

技能跟随它所在的库版本。升级 COUI 后，请重新复制一次这个目录。

| 文件 | 内容 |
| --- | --- |
| `SKILL.md` | 依赖配置、核心规则、页面写法、列表与卡片、材质模糊、文案、收尾检查 |
| `references/scaffold.md` | 可直接复制的页面脚手架（`CouiPage`、`CouiPageScaffold`）和模糊桥接（`panelBlur`、弹窗与按钮的材质提供者） |
| `references/components.md` | ColorOS 控件与 COUI 组件对照、Overlay 与 Window 的区别 |
| `references/pitfalls.md` | 实际踩过的坑和避免方法 |

## 规则摘要

1. 所有内容放在 COUI 的 `Scaffold` 里；`Overlay*` 对话框、面板和菜单都在它的弹出层里绘制。
2. 颜色用 `COUITheme.colorScheme`，文字样式用 `COUITheme.textStyles`，界面不写死颜色。
3. 设置页的结构是 `PreferenceCategory` + `Card` + `CardListGroup` + 设置项。分割线和圆角位置由分组负责：不要加 `HorizontalDivider`，也不要给行首图标加边距。
4. 用与 ColorOS 控件对应的库组件，不要自己拼一个长得像的。
5. 材质模糊需要接线：用 `layerBackdrop` 录制页面，交给录制层之外的顶栏光晕、对话框、弹出菜单和各类栏；设备不支持运行时着色器时保留不透明的回退。
6. 面向用户的文字放进 Compose 资源（`values/` 和 `values-zh/`）；状态里存 id，不存显示文字。
7. 自己写的可组合函数沿用库的参数顺序：必填参数、`modifier`、带默认值的参数、末尾的内容 lambda。
8. 顶栏下方可输入的搜索要像 ColorOS 设置的搜索一样与顶栏联动：用 `TopAppBarSearchBar` + `rememberTopAppBarSearchState`，页面用 `Modifier.topAppBarSearchContent` 淡出，结果用 `Modifier.topAppBarSearchResults` 进入。不要用原地过滤的静态胶囊。
9. 列表弹出菜单要和打开它的按钮一起包在一个 `Box` 里；直接放进顶栏 `actions` 行时，菜单显示期间会把按钮挤开。

## 先画草图

与其用文字描述，不如先在 [COUI Canvas 画布](./canvas) 里把屏幕摆出来，再复制它生成的提示词：每个部件都已经写明了对应的 COUI 组件。

## 可以这样提需求

- 「加一个显示设置页，有深色模式开关、字体大小下拉和壁纸入口。」
- 「把这个列表改成 ColorOS 设置首页，顶栏里放搜索胶囊。」
- 「清除缓存前弹一个带 COUI 17 材质模糊的确认对话框。」

AI 应该给出一个 `CouiPage`（或 `Scaffold` + `TopAppBar`）、分组卡片里的设置项，以及中英两套文案。
