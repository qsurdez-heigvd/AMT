<script setup lang="ts">
const { data: reviews, status } = useWines('/v1/reviews')
const pending = computed(() => status.value === 'pending')
const config = useRuntimeConfig()
useSeoMeta({ title: 'Les derniers reviews' })
</script>

<template>
  <UContainer>
    <UPageHeader
      title="Les reviews des experts sur nos vins !"
      description="Découvrez ce que les fins nez du milieu pensent des vins."
      class="py-[50px]"
    />

    <UPageBody>
      <UPageGrid v-if="!pending && reviews?.length">
        <UPageCard
          v-for="review in reviews"
          :key="review.id"
        >
          <UBlogPost
            :title="review.title"
            :description="review.body"
            :to="`/review/${review.id}`"
            orientation="vertical"
            :ui="{
              image: {
                wrapper: 'relative flex justify-center overflow-hidden pointer-events-none',
                base: 'border-none object-cover object-center w-28 h-45 mx-auto transform transition-transform duration-200 group-hover:scale-105'
              },
              description: 'line-clamp-2',
              author: 'mt-4'
            }"
            :image="{
              src: `${config.public.baseURL}/images/wines/${review.wine.id}.jpg`,
              alt: review.wine.name
            }"
            :authors="[
              {
                name: review.author.displayName,
                avatar: {}
              }
            ]"
          />
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
        Aucune review disponible pour le moment.
      </div>
    </UPageBody>
  </UContainer>
</template>
