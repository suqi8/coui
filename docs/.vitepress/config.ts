import { copyFileSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { defineConfig } from "vitepress";
import locales from "./locales";

// Strips what only the rendered page needs (front matter, demo iframes) so the text reads well to a model.
function plainMarkdown(source: string): string {
  return source
    .replace(/^---\r?\n[\s\S]*?\r?\n---\r?\n/, "")
    .replace(/<div[^>]*>\s*<iframe[\s\S]*?<\/iframe>\s*<\/div>\s*/g, "")
    .trim();
}

// Guide pages in llms-full.txt, in reading order; the component pages follow.
const fullGuidePages = [
  "guide/getting-started.md",
  "guide/migration.md",
  "guide/upgrading.md",
  "guide/ai.md",
  "guide/best-practices.md",
  "guide/theme.md",
  "guide/colors.md",
  "guide/textstyles.md",
  "guide/icons.md",
  "guide/utils.md",
  "guide/coui-nav.md",
  "guide/navigation3.md",
  "guide/blur.md",
  "guide/squircle.md",
  "guide/multiplatform.md",
  "guide/canvas.md",
];
import {
  GitChangelog,
  GitChangelogMarkdownSection,
} from "@nolebase/vitepress-plugin-git-changelog/vite";

export default defineConfig({
  base: "/coui/",
  title: "COUI",
  locales: locales.locales,
  head: [
    // Auto-redirect first-time visitors
    ['script', {}, `(function(){
      try {
        if (typeof window === 'undefined' || typeof localStorage === 'undefined') return;
        var KEY = 'coui-locale-auto-redirected';
        if (localStorage.getItem(KEY)) return;
        localStorage.setItem(KEY, '1');
        var lang = (navigator.language || '').toLowerCase();
        var isSimplifiedZh = lang === 'zh-cn' || lang === 'zh-sg' || lang.indexOf('zh-hans') === 0;
        if (!isSimplifiedZh) return;
        var base = '/coui/';
        var path = window.location.pathname;
        if (path.indexOf(base) !== 0) return;
        if (path.indexOf(base + 'zh_CN') === 0) return;
        var rest = path.slice(base.length);
        window.location.replace(base + 'zh_CN/' + rest + window.location.search + window.location.hash);
      } catch (e) {}
    })();`],
    ['meta', { name: 'color-scheme', content: 'light dark' }],
    ['link', { rel: 'icon', href: '/coui/Icon.webp' }],
    ['link', { rel: 'preconnect', href: 'https://cdn-font.hyperos.mi.com/font/css?family=MiSans_VF:VF:Chinese_Simplify,Latin&display=swap' }],
  ],
  markdown: {
    image: {
      lazyLoading: true,
    },
  },
  cleanUrls: true,
  // The dokka API reference lives in public/ as raw static HTML, invisible to
  // the dead-link checker which only knows VitePress pages.
  // The canvas and example web apps are copied into public/ by the docs workflow.
  ignoreDeadLinks: [/\/dokka\//, /\/(canvas|example)\//],
  // Publish every page's Markdown next to it (read by the AI page actions) and the whole English
  // guide and component reference as llms-full.txt.
  buildEnd(siteConfig) {
    for (const page of siteConfig.pages) {
      const target = join(siteConfig.outDir, page);
      mkdirSync(dirname(target), { recursive: true });
      copyFileSync(join(siteConfig.srcDir, page), target);
    }
    const components = siteConfig.pages
      .filter((page) => page.startsWith("components/") && page !== "components/index.md")
      .sort();
    const sections = [...fullGuidePages, ...components]
      .filter((page) => siteConfig.pages.includes(page))
      .map((page) => `<!-- ${page} -->\n\n${plainMarkdown(readFileSync(join(siteConfig.srcDir, page), "utf-8"))}`);
    const intro = readFileSync(join(siteConfig.srcDir, "public/llms.txt"), "utf-8").trim();
    writeFileSync(join(siteConfig.outDir, "llms-full.txt"), [intro, ...sections].join("\n\n---\n\n") + "\n");
  },
  themeConfig: {
    logo: "/Icon.webp",
    socialLinks: [
      { icon: 'github', link: 'https://github.com/suqi8/coui' }
    ],
    search: {
      provider: "local",
      options: {
        locales: {
          zh_CN: {
            translations: {
              button: {
                buttonText: "搜索",
                buttonAriaLabel: "搜索",
              },
              modal: {
                noResultsText: "无法找到相关结果",
                resetButtonTitle: "清除查询条件",
                footer: {
                  selectText: "选择",
                  navigateText: "切换",
                  closeText: "关闭",
                },
              },
            },
          },
        },
      },
    },
    docFooter: {
      prev: false,
      next: false,
    },
  },
  vite: {
    optimizeDeps: {
      exclude: [
        "@nolebase/vitepress-plugin-enhanced-readabilities/client",
        "vitepress",
        "@nolebase/ui",
      ],
    },
    ssr: {
      noExternal: [
        "@nolebase/vitepress-plugin-enhanced-readabilities",
        "@nolebase/ui",
      ],
    },
    plugins: [
      GitChangelog({
        repoURL: () => "https://github.com/suqi8/coui",
      }),
      GitChangelogMarkdownSection({
        sections: {
          disableContributors: true,
        },
      }),
    ],
  },
});
