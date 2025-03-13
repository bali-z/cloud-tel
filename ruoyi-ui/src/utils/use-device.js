import {setDeviceStatus} from '@/utils/global';

export function useDevice() {

    const hasAudioInput = ref(true);
    const hasAudioOutput = ref(true);
    const hasVideoInput = ref(false);

    const tip = ref('');
    const videoRef = ref();

    const localStream = {
        videoStream: null,
        audioStream: null,
    };

    let animationId = 0;
    const volume = ref(0);

    onUnmounted(() => {
        cancelAnimationFrame(animationId);
        localStream.audioStream?.getTracks().forEach((track) => track.stop());
        localStream.videoStream?.getTracks().forEach((track) => track.stop());
        localStream.audioStream = null;
        localStream.videoStream = null;
    });

    /**
     *  初始化设备
     * @returns {Promise<void>}
     */
    async function initDevice() {
        tip.value = '摄像头正在启动...';
        const devices = await navigator.mediaDevices.enumerateDevices();
        const audioinput = devices.find((item) => item.kind === 'audioinput');
        const audiooutput = devices.find((item) => item.kind === 'audiooutput');
        const videoinput = devices.find((item) => item.kind === 'videoinput');
        // 如果设备已经授权，可以拿到设备的deviceId
        hasAudioInput.value = !!audioinput?.deviceId;
        hasAudioOutput.value = !!audiooutput?.deviceId;
        hasVideoInput.value = !!videoinput?.deviceId;
        if (hasVideoInput.value) {
            try {
                tip.value = '摄像头正在启动';
                localStream.videoStream = await navigator.mediaDevices.getUserMedia({video: true});
                videoRef.value.srcObject = localStream.videoStream;
            } catch (e) {
                hasVideoInput.value = false;
                if (e.message === 'Device in use') {
                    tip.value = '摄像头被占用';
                }
            }
        } else {
            tip.value = '摄像头处于关闭状态';
        }
        if (hasAudioInput.value) {
            localStream.audioStream = await navigator.mediaDevices.getUserMedia({audio: true});
            watchMicro(localStream.audioStream);
        }
    }

    /**
     * 关闭麦克风
     */
    function closeMicro() {
        hasAudioInput.value = false;
        localStream.audioStream?.getTracks().forEach((track) => track.stop());
        localStream.audioStream = null;
        cancelAnimationFrame(animationId);
        setDeviceStatus({audioinput: false, audiooutput: hasAudioOutput.value, videoinput: hasVideoInput.value});
    }

    /**
     *  打开麦克风
     * @returns {Promise<void>}
     */
    async function openMicro() {
        try {
            localStream.audioStream = await navigator.mediaDevices.getUserMedia({audio: true});
            console.log('麦克风权限已授予。');
            hasAudioInput.value = true;
            setDeviceStatus({audioinput: true, audiooutput: hasAudioOutput.value, videoinput: hasVideoInput.value});
            // 检测麦克风音量
            watchMicro(localStream.audioStream);
        } catch (error) {
            console.error(error);
            let message;
            if (error.name === 'NotAllowedError' || error.name === 'PermissionDeniedError') {
                message = '用户拒绝授予麦克风权限';
            } else if (error.name === 'NotFoundError' || error.name === 'DevicesNotFoundError') {
                message = '没有找到音频输入设备。';
            } else {
                console.error('获取麦克风权限时发生其他错误：', error);
                message = '未知的异常！';
            }
            $notify({
                title: '提示',
                message,
                type: 'warning',
            });
        }
    }

    /**
     *  检测麦克风音量
     * @param stream
     */
    function watchMicro(stream) {
        const audioContext = new window.AudioContext();
        // 将麦克风的声音输入这个对象
        const mediaStreamSource = audioContext.createMediaStreamSource(stream);
        // 创建分析节点
        const analyser = audioContext.createAnalyser();
        // 连接节点
        mediaStreamSource.connect(analyser);
        analyser.fftSize = 1024;
        // 可以实时听到麦克风采集的声音
        // analyserNode.connect(audioContext.destination)
        const bufferLength = analyser.frequencyBinCount;
        // 获取音量数据
        const dataArray = new Uint8Array(bufferLength);

        function set() {
            analyser.getByteFrequencyData(dataArray);
            let value = 0;
            for (let i = 0; i < bufferLength; i++) {
                value += dataArray[i];
            }
            // 计算平均音量，并放大两倍
            value = (value / bufferLength) * 2;
            volume.value = value > 100 ? 100 : value;
            animationId = requestAnimationFrame(set);
        }

        set();
    }

    /**
     * 关闭摄像头
     */
    function closeVideo() {
        tip.value = '摄像头处于关闭状态';
        hasVideoInput.value = false;
        localStream.videoStream?.getTracks().forEach((track) => track.stop());
        localStream.videoStream = null;
        setDeviceStatus({audioinput: hasAudioInput.value, audiooutput: hasAudioOutput.value, videoinput: false});
    }

    /**
     *  打开摄像头
     * @returns {Promise<void>}
     */
    async function openVideo() {
        try {
            tip.value = '摄像头正在启动...';
            localStream.videoStream = await navigator.mediaDevices.getUserMedia({video: true});
            console.log('摄像头权限已授予。');
            hasVideoInput.value = true;
            setDeviceStatus({audioinput: hasAudioInput.value, audiooutput: hasAudioOutput.value, videoinput: true});
            await nextTick();
            videoRef.value.srcObject = localStream.videoStream;
        } catch (error) {
            let message;
            if (error.name === 'NotAllowedError' || error.name === 'PermissionDeniedError') {
                message = '用户拒绝授予摄像头权限';
                tip.value = '用户拒绝授予摄像头权限';
            } else if (error.name === 'NotFoundError' || error.name === 'DevicesNotFoundError') {
                message = '没有找到视频输入设备。';
                tip.value = '没有找到视频输入设备';
            } else if (error.message === 'Device in use') {
                tip.value = '摄像头被占用';
                message = '摄像头被占用';
            } else {
                console.error('获取摄像头权限时发生其他错误：', error);
                message = '启动失败！';
                tip.value = '启动失败';
            }
            $notify({
                title: '提示',
                message,
                type: 'warning',
            });
        }
    }

    return {
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
        localStream,
    };
}
