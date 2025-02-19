export default defineNuxtPlugin({
  enforce: 'pre',
  async setup() {
    const clients = useRuntimeConfig().public.openFetch
    const { token } = useAuth()
    const $fetch = useRequestFetch()

    return {
      provide: Object.entries(clients).reduce((acc, [name, options]) => ({
        ...acc,
        [name]: createOpenFetch(localOptions => ({
          ...options,
          ...localOptions,
          onRequest(ctx) {
            if (token.value) {
              ctx.options.headers.append('Authorization', token.value)
            }

            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            return localOptions?.onRequest?.(ctx)
          }
        }), $fetch)
      }), {})
    }
  }
})
