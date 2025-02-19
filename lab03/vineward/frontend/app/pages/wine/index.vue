<script setup lang="ts">
const { data: winesList, status } = useWines('/v1/wines')
const pending = computed(() => status.value === 'pending')
const config = useRuntimeConfig()

useSeoMeta({ title: 'Les vins' })
</script>

<template>
  <UContainer>
    <UPageHeader
      title="Nos vins préférés"
      description="Découvrez nos sélections et nos coups de cœur du moment"
      class="py-[50px]"
    />

    <UPageBody>
      <UPageGrid v-if="!pending && winesList?.length">
        <UPageCard
          v-for="wine in winesList"
          :key="wine.id"
          :title="wine.name"
          orientation="horizontal"
          :hoverable="true"
        >
          <template #icon>
            <img
              :src="`${config.public.baseURL}/images/wines/${wine.id}.jpg`"
              :alt="wine.name"
              class="mx-auto w-30 h-auto object-cover rounded-md"
            >
          </template>

          <template #footer>
            <router-link
              :to="`/wine/${wine.id}`"
              class="text-blue-600 underline"
            >
              Voir les détails
            </router-link>
          </template>
        </UPageCard>
      </UPageGrid>

      <div
        v-else-if="pending"
        class="flex justify-center items-center h-[400px]"
      >
        <span class="text-gray-500">Chargement...</span>
      </div>

      <div
        v-else
        class="text-center text-gray-500"
      >
        Aucun vin disponible pour le moment.
      </div>
    </UPageBody>
  </UContainer>
</template>
