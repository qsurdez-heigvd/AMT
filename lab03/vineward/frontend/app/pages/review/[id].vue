<script setup lang="ts">
const route = useRoute()
const config = useRuntimeConfig()
const { data: review, status } = useWines('/v1/reviews/{reviewId}', {
  path: { reviewId: route.params.id }
})
const { data: comments, refresh: refreshComments } = useWines('/v1/reviews/{reviewId}/comments', {
  path: { reviewId: route.params.id }
})

const pending = computed(() => status.value === 'pending')

useSeoMeta({ title: computed(() => review?.value?.title ?? 'Loading') })

const onCommentPosted = async () => {
  await refreshComments()
}
</script>

<template>
  <UContainer v-if="!pending && review">
    <UPageHero
      :title="review.title"
      :description="review.body"
      align="left"
      :links="[
        {
          label: 'Voir le vin',
          to: `/wine/${review.wine.id}`,
          color: 'black',
          size: 'lg'
        },
        {
          label: 'Retour aux reviews',
          to: '/review',
          color: 'gray',
          size: 'lg',
          trailingIcon: 'i-heroicons-arrow-left-20-solid'
        }
      ]"
    >
      <img
        :src="`${config.public.baseURL}/images/wines/${review.wine.id}.jpg`"
        :alt="review?.wine.name"
      >
    </UPageHero>

    <UContainer class="py-8">
      <UPageBody>
        <!-- Informations supplémentaires -->
        <div class="space-y-6">
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-4">
              <UAvatar
                :alt="review?.author.displayName"
                size="lg"
              />
              <div>
                <h3 class="font-semibold">
                  {{ review.author.displayName }}
                </h3>
              </div>
            </div>
          </div>

          <div class="border-t pt-6">
            <h3 class="text-lg font-semibold mb-4">
              Vin évalué
            </h3>
            <UCard>
              <template #header>
                <div class="flex items-center space-x-4">
                  <img
                    :src="`${config.public.baseURL}/images/wines/${review?.wine.id}.jpg`"
                    :alt="review?.wine.name"
                    class="w-16 rounded"
                  >
                  <div>
                    <h4 class="font-medium">
                      {{ review?.wine.name }}
                    </h4>
                  </div>
                </div>
              </template>
            </UCard>
          </div>
        </div>
      </UPageBody>
    </UContainer>
    <h2 class="text-2xl font-semibold mb-6">
      Comments ({{ comments.length }})
    </h2>
    <div
      v-if="comments && comments.length"
      class="mt-12"
    >
      <div class="space-y-6">
        <div
          v-for="(comment, index) in comments"
          :key="index"
          class="p-4 rounded-lg border border-gray-200 dark:border-gray-800"
        >
          <div class="font-medium mb-2 flex flex-row items-center space-x-4">
            <UAvatar
              :alt="comment?.author.displayName"
              size="lg"
            />
            <div>
              <h4 class="font-semibold">
                {{ comment.author.displayName }}
              </h4>
            </div>
          </div>
          <p class="text-gray-700 dark:text-gray-300">
            {{ comment.body }}
          </p>
        </div>
      </div>
    </div>
    <CommentForm
      :review-id="route.params.id"
      @comment-posted="onCommentPosted"
    />
  </UContainer>

  <!-- État de chargement -->
  <div
    v-else-if="pending"
    class="flex justify-center items-center h-[400px]"
  >
    <span class="text-gray-500">Chargement...</span>
  </div>
</template>
