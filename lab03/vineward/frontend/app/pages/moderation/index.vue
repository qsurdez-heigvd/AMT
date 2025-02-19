<script setup lang="ts">
const { $wines } = useNuxtApp()

const { data: nextReviewToModerate, status, refresh: refreshPendingReview } = useWines('/v1/moderation/reviews/peek')
type Review = typeof nextReviewToModerate

const pending = computed(() => status.value === 'pending')

const onModeratedReview = async () => {
  await refreshPendingReview()
}

const onApproval = async (id: string) => {
  console.log('approving the review...')

  try {
    await $wines('/v1/moderation/reviews/{reviewId}/approve', {
      method: 'post',
      path: { reviewId: id }
    })
  } catch (error) {
    console.error('Error approving the review: ', error)
  }

  await onModeratedReview()
}

const onRefusal = async (id: string) => {
  console.log('refusing the review...')

  try {
    await $wines('/v1/moderation/reviews/{reviewId}/deny', {
      method: 'post',
      path: { reviewId: id }
    })
  } catch (error) {
    console.error('Error refusing the review: ', error)
  }

  await onModeratedReview()
}
</script>

<template>
  <UContainer>
    <UPageHeader
      title="Page de modération des reviews"
      description="Acceptez ou refusez la prochaine review"
      class="py-[50px]"
    />

    <UPageBody>
      <UPageGrid>
        <UPageCard
          v-if="!pending && nextReviewToModerate"
          :key="nextReviewToModerate.id"
          :title="nextReviewToModerate.title"
          :description="nextReviewToModerate.body"
          orientation="vertical"
          :hoverable="true"
          :ui="{
            description: 'line-clamp-2'
          }"
        >
          <!-- Add a footer slot for the buttons -->
          <template #footer>
            <div class="flex gap-4 mt-4">
              <UButton
                v-if="nextReviewToModerate.status === 'PENDING_REVIEW'"
                color="green"
                variant="soft"
                icon="i-heroicons-check"
                @click="onApproval(nextReviewToModerate.id)"
              >
                Approve
              </UButton>
              <UButton
                v-if="nextReviewToModerate.status === 'PENDING_REVIEW'"
                color="red"
                variant="soft"
                icon="i-heroicons-x-mark"
                @click="onRefusal(nextReviewToModerate.id)"
              >
                Deny
              </UButton>
            </div>
          </template>
        </UPageCard>

        <!-- Show message when there are no reviews -->
        <UAlert
          v-else-if="!pending && !nextReviewToModerate"
          icon="i-heroicons-information-circle"
          color="gray"
          title="Aucun avis à modérer"
          description="Tous les avis ont été modérés. Revenez plus tard !"
        />

        <div
          v-else-if="pending"
          class="flex justify-center items-center h-[400px]"
        >
          <span class="text-gray-500">Chargement...</span>
        </div>
      </UPageGrid>
    </UPageBody>
  </UContainer>
</template>
