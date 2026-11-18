---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: COUI
  text: A UI library for Compose MultiPlatform
  tagline: ColorOS 17 style components for Android, iOS, Desktop and Web
  image:
    src: /Icon.webp
    alt: Logo
  actions:
    - theme: brand
      text: Getting Started
      link: /guide/getting-started
    - theme: alt
      text: Components
      link: /components/index
    - theme: alt
      text: Try the Example
      link: /example/
      target: _blank
    - theme: alt
      text: COUI Canvas
      link: /canvas/
      target: _blank

features:
  - icon: 🎨
    title: ColorOS 17 Aesthetics
    details: Matched to the COUI 17 source and real devices.
  - icon: 📦
    title: Rich Components
    details: Settings pages, dialogs, menus, pickers and more.
    link: /components/index
    linkText: Browse components
  - icon: 📱
    title: Web Example
    details: Try every component in the browser, no install needed.
    link: /example/
    linkText: Open the example
    target: _blank
  - icon: 🖌️
    title: COUI Canvas
    details: Drag components into a screen in the browser and export code or an AI prompt.
    link: /canvas/
    linkText: Open Canvas
    target: _blank
  - icon: 🤖
    title: AI Friendly
    details: A skill and llms.txt help AI write proper COUI screens. Both are under "For AI" in the top bar.
    link: /guide/ai
    linkText: Writing with AI
  - icon: 🧬
    title: Based on Miuix
    details: Built on Miuix, with a guide for migrating from it.
    link: /guide/migration
    linkText: Migrate from Miuix

nolebase:
  gitChangelog: false
  pageProperties: false
---
