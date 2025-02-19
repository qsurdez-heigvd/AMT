<script setup lang="ts">
const { data: page } = await useAsyncData('index', () =>
  queryContent('/').findOne()
)

if (!page.value) {
  throw createError({ statusCode: 404, statusMessage: 'Page not found', fatal: true })
}

definePageMeta({ auth: false })
useSeoMeta({ title: 'Home' })
</script>

<template>
  <div v-if="page">
    <!-- ===================== HERO ===================== -->
    <ULandingHero
      :title="page.hero.title"
      :description="page.hero.description"
      :links="page.hero.links"
    >
      <div class="absolute inset-0 z-[-1]">
        <div
          class="absolute inset-0 bg-cover bg-center"
          :style="`background-image: url(${page.hero.background});`"
        />
        <div class="absolute inset-0 bg-white/60" />
      </div>
    </ULandingHero>

    <!-- ===================== SECTIONS DYNAMIQUES ===================== -->
    <ULandingSection
      v-for="(section, sectionIndex) in page.sections || []"
      :key="sectionIndex"
      :title="section.title"
      :description="section.description"
      :align="section.align"
      :features="section.features"
    >
      <img
        v-if="sectionIndex === 0"
        src="https://media-cdn.tripadvisor.com/media/attractions-splice-spp-674x446/0b/25/34/a5.jpg"
        class="rounded-lg shadow-md"
      >
    </ULandingSection>

    <!-- ===================== FEATURES ===================== -->
    <ULandingSection
      v-if="page.features"
      :title="page.features.title"
      :description="page.features.description"
    >
      <UPageGrid>
        <ULandingCard
          v-for="(feat, i) in page.features.items"
          :key="i"
          v-bind="feat"
        />
      </UPageGrid>
    </ULandingSection>
  </div>
</template>

<style scoped>
.ULandingHero .title {
  font-size: 8.5rem;
}

.bg-cover {
  background-repeat: no-repeat;
  background-position: center;
}
</style>
