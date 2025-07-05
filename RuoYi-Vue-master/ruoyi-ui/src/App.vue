<template>
  <div id="app">
    <router-view />
    <theme-picker />
    <AIDialog :studentId="id" :courseId="currentCourseId" />
  </div>
</template>

<script>
import ThemePicker from "@/components/ThemePicker"
import AIDialog from "@/components/AIDialog.vue"
import { mapGetters } from "vuex"

export default {
  name: "App",
  components: { ThemePicker, AIDialog },
  computed: {
    ...mapGetters(["id"]),
    currentCourseId() {
      return this.$route.params.courseId || this.$route.query.courseId || ""
    }
  }
}
</script>
<style scoped>
#app .theme-picker {
  display: none;
}
</style>
