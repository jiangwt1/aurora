import mermaidPlugin from "@agoose77/markdown-it-mermaid"
import MarkdownIt from 'markdown-it'
import mkKatexExternal from 'markdown-it-katex-external'
import mkEmoji from 'markdown-it-emoji'
import mkContainer from 'markdown-it-container'
import mkSup from 'markdown-it-sup'
import mkSub from 'markdown-it-sub'
import mkFootnote from 'markdown-it-footnote'
import mkAbbr from 'markdown-it-abbr'
import mkIns from 'markdown-it-ins'
import mkMark from 'markdown-it-mark'
import mkKatex from '@iktakahiro/markdown-it-katex'

export default function markdownToHtml(content: any) {
  const md = new MarkdownIt({
    html: true
  })
    .use(mkKatexExternal)
    .use(mkEmoji)
    .use(mkContainer, 'hljs-center')
    .use(mkContainer, 'hljs-left')
    .use(mkContainer, 'hljs-right')
    .use(mkSup)
    .use(mkSub)
    .use(mkFootnote)
    .use(mkAbbr)
    .use(mkIns)
    .use(mkMark)
    .use(mkKatex)
    .use(mermaidPlugin)
  return md.render(content)
}
