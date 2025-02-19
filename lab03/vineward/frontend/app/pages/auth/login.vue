<script setup lang="ts">
import { z } from 'zod'
import type { FormSubmitEvent } from '#ui/types'

definePageMeta({ layout: 'auth' })
useSeoMeta({ title: 'Login' })

const { signIn } = useAuth()
const toast = useToast()

const schema = z.object({
  email: z.string()
    .min(1, 'Email is required')
    .email('Must be a valid email'),
  password: z.string()
    .min(8, 'Password must be at least 8 characters')
})

type Schema = z.output<typeof schema>

const fields = [{
  name: 'email',
  type: 'email',
  label: 'Email',
  placeholder: 'Enter your email'
}, {
  name: 'password',
  label: 'Password',
  type: 'password',
  placeholder: 'Enter your password'
}]

const onSubmit = async (values: FormSubmitEvent<Schema>) => {
  try {
    await signIn(values, { callbackUrl: '/' })
  } catch (error) {
    toast.add({
      title: 'Login failed',
      description: 'An error occurred while trying to sign in.',
      color: 'red'
    })
  }
}
</script>

<template>
  <UCard class="max-w-sm w-full bg-white/75 dark:bg-white/5 backdrop-blur">
    <UAuthForm
      :fields="fields"
      :schema="schema"
      title="Welcome back"
      align="top"
      icon="i-heroicons-lock-closed"
      :ui="{ base: 'text-center', footer: 'text-center' }"
      :submit-button="{ trailingIcon: 'i-heroicons-arrow-right-20-solid' }"
      @submit="onSubmit"
    >
      <template #description>
        Don't have an account?
        <NuxtLink
          to="/auth/register"
          class="text-primary font-medium"
        >Sign up</NuxtLink>
        .
      </template>
    </UAuthForm>
  </UCard>
</template>
