<!--麦克风按钮-->
<template>
  <div v-if="!disabled" class="btn" @click="closeMicro">
    <svg width="26" height="26" viewBox="0 0 40 40" fill="#fff" xmlns="http://www.w3.org/2000/svg">
      <path
        d="M28.75 17.5V21.25C28.75 23.5706 27.8281 25.7962 26.1872 27.4372C24.5462 29.0781 22.3206 30 20 30C17.6794 30 15.4538 29.0781 13.8128 27.4372C12.1719 25.7962 11.25 23.5706 11.25 21.25V17.5H8.75V21.25C8.75131 24.0163 9.77182 26.6852 11.6165 28.7467C13.4612 30.8081 16.0008 32.1176 18.75 32.425V35H13.75V37.5H26.25V35H21.25V32.425C23.9992 32.1176 26.5388 30.8081 28.3835 28.7467C30.2282 26.6852 31.2487 24.0163 31.25 21.25V17.5H28.75Z"
        fill="#fff"
      />
    </svg>
    <div class="box">
      <div class="value" :style="{ transform: `translateY(${16 * (1 - volume * 0.01)}px)` }"></div>
    </div>
  </div>
  <div v-else class="btn micro-btn disabled" @click="openMicro">
    <svg-icon name="icon-micro-off" color="#fff" />
    <div class="warning">!</div>
  </div>
</template>

<script setup>
const props = defineProps({
  disabled: {
    type: Boolean,
    default: false,
  },
  volume: {
    type: Number,
    default: 0,
  },
});
const emit = defineEmits(['close', 'open']);

function closeMicro() {
  emit('close');
}

function openMicro() {
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

.box {
  width: 8px;
  height: 16px;
  //background: linear-gradient(white 50%, #4cd964 50%);
  position: absolute;
  top: 13px;
  border-radius: 5px;
  left: 50%;
  margin-left: -4px;
  transition: background 0.3s;
  overflow: hidden;
  background-color: white;

  .value {
    width: 100%;
    height: 30px;
    position: relative;
    transform: translateY(16px);
    background-color: $--success-color;
    transition: transform 0.1s;
  }
}
</style>
