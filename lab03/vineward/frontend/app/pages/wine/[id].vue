<script setup lang="ts">
const { $wines } = useNuxtApp()
const toast = useToast()
const route = useRoute()
const { data: wine, status } = useWines('/v1/wines/{id}', {
  path: { id: route.params.id }
})

const pending = computed(() => status.value === 'pending')
const config = useRuntimeConfig()

// Computed property to filter verified reviews
const verifiedReviews = computed(() => {
  if (!wine.value?.reviews) return []
  return wine.value.reviews.filter(review => review.status === 'VERIFIED')
})

// New review modal state and form data
const isReviewModalOpen = ref(false)
const newReview = ref({
  title: '',
  body: ''
})

useSeoMeta({ title: computed(() => wine?.value?.name ?? 'Loading') })

// Form submission handling
const submitReview = async () => {
  try {
    const reviewCreated = await $wines('/v1/wines/{wineId}/reviews', {
      method: 'post',
      path: { wineId: route.params.id },
      body: {
        ...newReview.value
      }
    })

    await $wines('/v1/moderation/reviews/{reviewId}/submit', {
      method: 'post',
      path: { reviewId: reviewCreated.id }
    })

    isReviewModalOpen.value = false
    newReview.value = { title: '', body: '' }
    console.log('Votre avis a été soumis avec succès')
    
    await refreshNuxtData()
  } catch (error) {
    console.log('Une erreur est survenue lors de la soumission')
    toast.add({
      title: "Vous n'avez pas les droits de faire cette action",
      description: error.response?.data,
      icon: 'i-heroicons-x-circle',
      color: 'red'
    })
  }
}
</script>

<template>
  <UContainer v-if="!pending && wine">
    <UPageHeader
      :title="wine.name"
      :description="`${wine.region}, ${wine.origin}`"
      class="py-[50px]"
    />

    <UPageBody>
      <UPageGrid class="xl:grid-cols-2">
        <div class="flex justify-center">
          <img
            :src="`${config.public.baseURL}/images/wines/${wine.id}.jpg`"
            :alt="wine.name"
            class="max-h-[400px] w-auto object-cover rounded-md"
          >
        </div>

        <div class="space-y-6">
          <div>
            <h2 class="text-xl font-semibold mb-4">
              Informations sur le vin
            </h2>
            <div class="space-y-2">
              <p><strong>Vigneron:</strong> {{ wine.vintner }}</p>
              <p><strong>Région:</strong> {{ wine.region }}</p>
              <p><strong>Origine:</strong> {{ wine.origin }}</p>
              <div class="mt-4">
                <strong>Cépages:</strong>
                <div class="flex flex-wrap gap-2 mt-2">
                  <UBadge
                    v-for="variety in wine.varieties"
                    :key="variety"
                    :label="variety"
                    variant="subtle"
                    color="gray"
                  />
                </div>
              </div>
            </div>
          </div>

          <div>
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-semibold">
                Avis
              </h2>
              <UButton
                icon="i-heroicons-plus"
                color="primary"
                @click="isReviewModalOpen = true"
              >
                Ajouter un avis
              </UButton>
            </div>
          </div>

          <div v-if="verifiedReviews.length">
            <ul class="space-y-4">
              <li
                v-for="(review, index) in verifiedReviews"
                :key="index"
              >
                <router-link
                  :to="`/review/${review.id}`"
                  class="block p-4 border border-gray-200 rounded-lg bg-white shadow-sm
                         hover:shadow-md hover:bg-gray-50 transition duration-200 cursor-pointer"
                >
                  <div class="flex justify-between items-center mb-2">
                    <span class="font-semibold text-gray-700 text-lg">
                      {{ review.title }}
                    </span>
                    <span class="text-gray-500 text-sm">
                      {{ review.author.displayName ?? 'Auteur inconnu' }}
                    </span>
                  </div>

                  <!-- Un aperçu du contenu, si tu as du texte plus long -->
                  <p class="text-gray-600 line-clamp-2">
                    {{ review.body }}
                  </p>
                </router-link>
              </li>
            </ul>
          </div>
        </div>
      </UPageGrid>
    </UPageBody>

    <!-- Review Modal -->
    <UModal v-model="isReviewModalOpen">
      <UCard>
        <template #header>
          <div class="flex items-center justify-between">
            <h3 class="text-xl font-semibold">
              Ajouter un avis
            </h3>
            <UButton
              color="gray"
              variant="ghost"
              icon="i-heroicons-x-mark"
              @click="isReviewModalOpen = false"
            />
          </div>
        </template>

        <form
          class="space-y-4"
          @submit.prevent="submitReview"
        >
          <UFormGroup label="Titre">
            <UInput
              v-model="newReview.title"
              placeholder="Donnez un titre à votre avis"
              required
            />
          </UFormGroup>

          <UFormGroup label="Contenu">
            <UTextarea
              v-model="newReview.body"
              placeholder="Partagez votre expérience avec ce vin"
              required
              rows="4"
            />
          </UFormGroup>
        </form>

        <template #footer>
          <div class="flex justify-end gap-3">
            <UButton
              color="gray"
              variant="soft"
              type="button"
              @click="isReviewModalOpen = false"
            >
              Annuler
            </UButton>
            <UButton
              color="primary"
              type="button"
              @click="submitReview"
            >
              Publier
            </UButton>
          </div>
        </template>
      </UCard>
    </UModal>
  </UContainer>

  <div
    v-else-if="pending"
    class="flex justify-center items-center h-[400px]"
  >
    <span class="text-gray-500">Chargement...</span>
  </div>
</template>