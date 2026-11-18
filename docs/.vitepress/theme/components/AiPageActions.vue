<script setup lang="ts">
import { computed, ref } from "vue";
import { useData, withBase } from "vitepress";

const { page, lang, frontmatter } = useData();

const zh = computed(() => lang.value.startsWith("zh"));
const visible = computed(() => frontmatter.value.aiActions !== false && !page.value.isNotFound);
// buildEnd copies every Markdown source next to its page, so /guide/ai.md serves guide/ai.md.
const markdownPath = computed(() => withBase("/" + page.value.relativePath));

const t = computed(() =>
  zh.value
    ? {
        copy: "复制为 Markdown",
        copied: "已复制",
        failed: "复制失败",
        view: "查看 Markdown",
        chatgpt: "问 ChatGPT",
        claude: "问 Claude",
        prompt: (url: string, rules: string) =>
          `阅读 ${url} ，这是 COUI（ColorOS 17 风格的 Compose Multiplatform 组件库）文档中的一页。读完后回答我关于它的问题，写代码时遵循 ${rules} 里的规则。`,
      }
    : {
        copy: "Copy as Markdown",
        copied: "Copied",
        failed: "Copy failed",
        view: "View Markdown",
        chatgpt: "Ask ChatGPT",
        claude: "Ask Claude",
        prompt: (url: string, rules: string) =>
          `Read ${url}, a page of the COUI docs (a Compose Multiplatform port of the ColorOS 17 components), then answer my questions about it. When writing code, follow the rules in ${rules}.`,
      },
);

const copyState = ref<"idle" | "copied" | "failed">("idle");

function absolute(path: string): string {
  return new URL(path, window.location.origin).href;
}

async function copyMarkdown() {
  try {
    const response = await fetch(markdownPath.value);
    if (!response.ok) throw new Error(String(response.status));
    await navigator.clipboard.writeText(await response.text());
    copyState.value = "copied";
  } catch {
    copyState.value = "failed";
  }
  setTimeout(() => (copyState.value = "idle"), 2000);
}

function ask(base: string) {
  const prompt = t.value.prompt(absolute(markdownPath.value), absolute(withBase("/llms.txt")));
  window.open(base + encodeURIComponent(prompt), "_blank", "noopener");
}
</script>

<template>
  <div v-if="visible" class="ai-page-actions">
    <button type="button" class="ai-action" @click="copyMarkdown">
      {{ copyState === "copied" ? t.copied : copyState === "failed" ? t.failed : t.copy }}
    </button>
    <a class="ai-action" :href="markdownPath" target="_blank" rel="noopener">{{ t.view }}</a>
    <button type="button" class="ai-action" @click="ask('https://chatgpt.com/?hints=search&q=')">{{ t.chatgpt }}</button>
    <button type="button" class="ai-action" @click="ask('https://claude.ai/new?q=')">{{ t.claude }}</button>
    <a class="ai-action" :href="withBase('/llms.txt')" target="_blank" rel="noopener">llms.txt</a>
  </div>
</template>

<style scoped>
.ai-page-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
}

.ai-action {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 12px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 14px;
  background: var(--vp-c-bg-soft);
  color: var(--vp-c-text-2);
  font-size: 13px;
  font-weight: 500;
  line-height: 1;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s, border-color 0.2s;
}

.ai-action:hover {
  border-color: var(--vp-c-brand-1);
  color: var(--vp-c-brand-1);
}
</style>
