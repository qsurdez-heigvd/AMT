<template>
  <div class="comment-box mt-4">
    <UForm
      :state="{ commentText }"
      @submit.prevent="submitComment"
    >
      <UTextarea
        v-model="commentText"
        color="gray"
        size="lg"
        :rows="4"
        placeholder="Write a comment..."
        :ui="{
          base: 'transition-shadow duration-300 border-gray-200',
          focus: 'shadow-lg ring-primary'
        }"
      />

      <div class="flex justify-end mt-4">
        <UButton
          type="submit"
          color="primary"
          variant="solid"
        >
          Envoyer
        </UButton>
      </div>
    </UForm>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{ reviewId: string }>()
const emit = defineEmits(['comment-posted'])

const { $wines } = useNuxtApp()

const toast = useToast()
const commentText = ref('')
const isSubmitting = ref(false)

async function submitComment() {
  if (!commentText.value) {
    return
  }

  isSubmitting.value = true
  try {
    const comment = await $wines('/v1/reviews/{reviewId}/comments', {
      method: 'POST',
      path: { reviewId: props.reviewId },
      body: { body: commentText.value },
      onResponseError: ({ response }) => {
        toast.add({
          title: 'Erreur lors de la publication du commentaire',
          description: response?._data?.message || 'Une erreur inconnue est survenue',
          icon: 'i-heroicons-x-circle',
          color: 'red'
        })
      }
    })

    commentText.value = ''
    emit('comment-posted', comment)
    toast.add({
      title: 'Commentaire créé',
      icon: 'i-heroicons-check-circle',
      color: 'green'
    })
  } catch (err) {
    console.error(err)
  } finally {
    isSubmitting.value = false
  }
}
</script>
