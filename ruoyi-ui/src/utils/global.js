// 设备对象
const device = {
    audioinput: true,
    audiooutput: true,
    videoinput: true,
};
// 包含音频输入、输出、视频输入的布尔值对象
export function getDeviceStatus() {
    return { ...device };
}

// 设置设备状态
export function setDeviceStatus({ audioinput, audiooutput, videoinput }) {
    device.audioinput = audioinput;
    device.audiooutput = audiooutput;
    device.videoinput = videoinput;
}
