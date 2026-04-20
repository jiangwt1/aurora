import { createI18n } from 'vue-i18n'
import cookies from 'js-cookie'

const localeModules = import.meta.glob('./languages/*.json', { eager: true })

function loadLocaleMessages(): {
  [key: string]: { [key: string]: { [key: string]: string } }
} {
  const messages: {
    [key: string]: { [key: string]: { [key: string]: string } }
  } = {}
  for (const path in localeModules) {
    const matched = path.match(/([A-Za-z0-9-_]+)\./i)
    if (matched && matched.length > 1) {
      const locale = matched[1]
      messages[locale] = (localeModules[path] as any).default
    }
  }
  return messages
}

export const i18n = createI18n({
  legacy: false,
  locale: cookies.get('locale') ? String(cookies.get('locale')) : 'cn',
  fallbackLocale: cookies.get('locale') ? String(cookies.get('locale')) : 'cn',
  messages: loadLocaleMessages()
})
