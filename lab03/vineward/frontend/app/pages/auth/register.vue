<script setup lang="ts">
import { z } from 'zod'
import type { FormSubmitEvent } from '#ui/types'

useSeoMeta({ title: 'Sign up' })
definePageMeta({
  layout: 'auth',
  auth: {
    unauthenticatedOnly: true,
    navigateAuthenticatedTo: '/'
  }
})

const { signUp } = useAuth()

const schema = (z.object({
  email: z.string().min(1, 'An email is required').email('Must be a valid email'),
  username: z.string().min(1, 'A display name is required'),
  password: z.string().min(8, 'Password is required'),
  canton: z.string().min(1, 'A canton is required')
}))

type Schema = z.output<typeof schema>

const fields = [{
  name: 'email',
  type: 'email',
  label: 'Email',
  placeholder: 'Enter your email'
}, {
  name: 'username',
  type: 'text',
  label: 'Display name',
  placeholder: 'Enter your display name'
}, {
  name: 'password',
  label: 'Password',
  type: 'password',
  placeholder: 'Enter your password'
}, {
  name: 'canton',
  type: 'text',
  label: 'Canton',
  placeholder: 'Enter your canton'
}]

const onSubmit = async (values: FormSubmitEvent<Schema>) => signUp(values, { callbackUrl: '/' })
</script>

<template>
  <UCard class="max-w-sm w-full bg-white/75 dark:bg-white/5 backdrop-blur">
    <UAuthForm
      :fields="fields"
      :schema="schema"
      align="top"
      title="Create an account"
      :ui="{ base: 'text-center', footer: 'text-center' }"
      :submit-button="{ label: 'Create account' }"
      @submit="onSubmit"
    >
      <template #description>
        Already have an account?
        <NuxtLink
          to="/auth/login"
          class="text-primary font-medium"
        >Login</NuxtLink>
        .
      </template>
    </UAuthForm>
  </UCard>
</template>
