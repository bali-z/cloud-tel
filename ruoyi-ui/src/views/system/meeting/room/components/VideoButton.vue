<!--视频按钮-->
<template>
  <div v-if="!disabled" class="btn" @click="closeVideo">
    <svg-icon name="icon-video" color="#fff" />
  </div>
  <div v-else class="btn disabled" @click="openVideo">
    <svg-icon name="icon-video-off" color="#fff" />
    <div class="warning">!</div>
  </div>
</template>

<script setup>
const props = defineProps({
  disabled: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close', 'open']);

function closeVideo() {
  emit('close');
}

function openVideo() {
  emit('open');
}
</script>

<style scoped lang="scss">
.btn {
  width: 50px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  font-size: 26px;
  cursor: pointer;
  position: relative;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }

  &.disabled {
    background: $--error-color;
    border-color: $--error-color;

    &:hover {
      background: rgba($--error-color, 0.8);
      border-color: rgba($--error-color, 0.8);
    }
  }

  .warning {
    width: 20px;
    height: 20px;
    background: #fa7b17;
    border-radius: 50%;
    color: white;
    font-size: 14px;
    text-align: center;
    line-height: 20px;
    position: absolute;
    right: -5px;
    top: 0;
  }
}
</style>
