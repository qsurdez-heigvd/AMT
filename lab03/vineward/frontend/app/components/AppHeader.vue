<script setup lang="ts">
import type { NavItem } from '@nuxt/content'

const navigation = inject<Ref<NavItem[]>>('navigation', ref([]))

const { data: pendingReviewCount } = useWines('/v1/moderation/reviews/count')

const { status, signOut } = useAuth()

const links = [{
  label: 'Wines',
  to: '/wine'
}, {
  label: 'Reviews',
  to: '/review'
}, {
  label: 'Moderation',
  to: '/moderation'
}]
</script>

<template>
  <UHeader :links="links">
    <template #logo>
      <NuxtImg
        class="logo"
        to="/"
        src="/logo.png"
        alt="Nuxt Logo"
        width="130"
      />
    </template>

    <template #right>
      <template v-if="status === 'authenticated'">
        <UChip
          :text="pendingReviewCount ?? 0"
          size="2xl"
        >
          <UButton
            icon="i-heroicons-bell"
            color="gray"
            to="/moderation"
          />
        </UChip>
        <UButton
          color="black"
          variant="outline"
          @click="signOut"
        >
          Sign out
        </UButton>
      </template>
      <template v-else>
        <UButton
          color="gray"
          to="/auth/login"
        >
          Sign in
        </UButton>
      </template>
    </template>

    <template #panel>
      <UNavigationTree
        :links="mapContentNavigation(navigation)"
        default-open
      />
    </template>
  </UHeader>
</template>

<style scoped>
.logo {
  position: relative;
  opacity: 0.8;
  border-radius: 7px;
  filter: blur(0.3px);
}
</style>
