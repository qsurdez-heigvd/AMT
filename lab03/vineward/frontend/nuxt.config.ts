// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  extends: ['@nuxt/ui-pro'],

  modules: [
    '@nuxt/content',
    '@nuxt/eslint',
    '@nuxt/fonts',
    '@nuxt/image',
    '@nuxt/ui',
    '@nuxthq/studio',
    '@sidebase/nuxt-auth',
    '@vueuse/nuxt',
    'nuxt-og-image',
    'nuxt-open-fetch'
  ],

  ssr: false,

  devtools: {
    enabled: true
  },

  colorMode: {
    disableTransition: true,
    preference: 'light'
  },

  runtimeConfig: {
    public: {
      baseURL: `${process.env.API_BASE_URL}/api`
    }
  },

  build: {
    transpile: [
      'jsonwebtoken'
    ]
  },

  routeRules: {
    '/api/search.json': { prerender: true },
    '/docs': { redirect: '/docs/getting-started', prerender: false }
  },

  future: {
    compatibilityVersion: 4
  },

  compatibilityDate: '2024-07-11',

  nitro: {
    prerender: {
      routes: ['/'],
      crawlLinks: true
    }
  },

  typescript: {
    strict: false
  },

  hooks: {
    // Define `@nuxt/ui` components as global to use them in `.md` (feel free to add those you need)
    'components:extend': (components) => {
      const globals = components.filter(c => ['UButton'].includes(c.pascalName))

      globals.forEach(c => c.global = true)
    }
  },

  auth: {
    isEnabled: true,
    baseURL: `${process.env.API_BASE_URL}/api/v1/auth`,
    provider: {
      type: 'local',
      endpoints: {
        signIn: { path: '/login', method: 'post' },
        signUp: { path: '/register', method: 'post' },
        signOut: { path: '/logout', method: 'post' },
        getSession: { path: '/@me', method: 'get' }
      },
      token: {
        signInResponseTokenPointer: '/accessToken',
        maxAgeInSeconds: 86400,
        sameSiteAttribute: 'strict'
      },
      refresh: {
        isEnabled: true,
        endpoint: { path: '/refresh-token', method: 'post' },
        refreshOnlyToken: false,
        token: {
          signInResponseRefreshTokenPointer: '/refreshToken',
          refreshRequestTokenPointer: '/refreshToken',
          maxAgeInSeconds: 604800
        }
      },
      pages: {
        login: '/auth/login'
      },
      session: {
        dataType: {
          email: 'string',
          username: 'string',
          roles: `('ADMINISTRATOR' | 'MODERATOR' | 'EDITOR' | 'REVIEWER' | 'USER')[]`,
          origin: 'string'
        }
      }
    },
    sessionRefresh: {
      enableOnWindowFocus: true,
      enablePeriodically: 600000
    },
    globalAppMiddleware: true
  },

  eslint: {
    config: {
      stylistic: {
        commaDangle: 'never',
        braceStyle: '1tbs'
      }
    }
  },

  openFetch: {
    disableNuxtPlugin: true,
    clients: {
      wines: {
        baseURL: `${process.env.API_BASE_URL}/api`,
        schema: '../openapi/api-docs.json'
      }
    }
  }
})
