<template>
  <div class="video-wrapper">
    <video v-if="hasVideoInput" ref="videoRef" autoplay></video>
    <div v-else class="tip">{{ tip }}</div>
    <div class="bottom">
      <!-- 显示麦克风音量 -->
      <div class="left"></div>
      <div class="middle">
        <MicroButton :disabled="!hasAudioInput" :volume="volume" @close="closeMicro" @open="openMicro" />
        <VideoButton :disabled="!hasVideoInput" @close="closeVideo" @open="openVideo" />
      </div>
      <div class="right"></div>
    </div>
  </div>
</template>

<script setup>
import MicroButton from './MicroButton.vue';
import VideoButton from './VideoButton.vue';
import { useDevice } from '@/utils/use-device';

const {
  initDevice,
  hasAudioInput,
  hasAudioOutput,
  hasVideoInput,
  tip,
  videoRef,
  volume,
  closeMicro,
  openMicro,
  closeVideo,
  openVideo,
} = useDevice();
initDevice();
</script>

<style scoped lang="scss">
.video-wrapper {
  background: #202020;
  width: 800px;
  height: 450px;
  border-radius: 10px;
  position: relative;

  video {
    width: 100%;
    height: 100%;
  }

  .tip {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    color: rgba(255, 255, 255, 0.8);
    font-size: 26px;
    position: absolute;
    top: 0;
    left: 0;
  }

  .bottom {
    position: absolute;
    bottom: 0;
    font-size: 18px;
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 80px;
    //border: 1px dashed white;

    .left {
      height: 80px;
      width: 50px;
    }

    .middle {
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 0;
      gap: 20px;
    }

    .right {
      width: 50px;
      height: 80px;
    }
  }
}
</style>
