---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: COUI
  text: A UI library for Compose MultiPlatform
  tagline: ColorOS 17 风格的组件库，支持 Android、iOS、桌面和网页
  image:
    src: /Icon.webp
    alt: Logo
  actions:
    - theme: brand
      text: 快速开始
      link: /zh_CN/guide/getting-started
    - theme: alt
      text: 组件库
      link: /zh_CN/components/index
    - theme: alt
      text: 在线体验示例
      link: /example/
      target: _blank
    - theme: alt
      text: COUI Canvas
      link: /canvas/
      target: _blank

features:
  - icon: 🎨
    title: ColorOS 17 美学
    details: 参照 COUI 17 源码和真机效果还原。
  - icon: 📦
    title: 组件丰富
    details: 设置页、对话框、菜单、选择器等常用组件都有。
    link: /zh_CN/components/index
    linkText: 浏览组件
  - icon: 📱
    title: 网页版示例
    details: 不用安装，在浏览器里直接体验所有组件。
    link: /example/
    linkText: 打开示例
    target: _blank
  - icon: 🖌️
    title: COUI Canvas
    details: 在浏览器里拖组件画页面，可以导出代码或给 AI 的提示词。
    link: /canvas/
    linkText: 打开 Canvas
    target: _blank
  - icon: 🤖
    title: 方便 AI 使用
    details: 提供 skill 和 llms.txt，让 AI 也能写出规范的 COUI 页面。
    link: /zh_CN/guide/ai
    linkText: 用 AI 写 COUI
  - icon: 🧬
    title: 基于 Miuix
    details: 基于 Miuix 构建，可以从 Miuix 迁移过来。
    link: /zh_CN/guide/migration
    linkText: 从 Miuix 迁移

nolebase:
  gitChangelog: false
  pageProperties: false
---
