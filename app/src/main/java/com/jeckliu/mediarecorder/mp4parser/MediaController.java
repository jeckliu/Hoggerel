package com.jeckliu.mediarecorder.mp4parser;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import java.io.IOException;
import java.nio.ByteBuffer;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressWarnings("WrongConstant")
public class MediaController {
    private static final int KEY_BIT_RATE = 1000000;
    private static final int KEY_FRAME_RATE = 25;
    private static final int KEY_COLOR_FORMAT = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;
    private static final int KEY_I_FRAME_INTERVAL = 10;

    private static volatile MediaController Instance;
    private String mimeTypeVideo;
    private MediaFormat mediaFormatAudio;
    private int muxerTrackIndexAudio;
    private int muxerTrackIndexVideo;
    private int extractTrackIndexAudio;
    private int extractTrackIndexVideo;

    public static MediaController getInstance() {
        if (Instance == null) {
            synchronized (MediaController.class) {
                if (Instance == null) {
                    Instance = new MediaController();
                }
            }
        }
        return Instance;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void writeAudioTrack(MediaExtractor extractor, MediaMuxer mediaMuxer, MediaCodec.BufferInfo info, long start, long end) throws Exception {
        extractor.selectTrack(extractTrackIndexAudio);
        MediaFormat trackFormat = extractor.getTrackFormat(extractTrackIndexAudio);
        int maxBufferSize = trackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        if (start > 0) {
            extractor.seekTo(start, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        } else {
            extractor.seekTo(0, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);

        while (true) {
            info.size = extractor.readSampleData(buffer, 0);
            if (info.size > 0) {
                info.presentationTimeUs = extractor.getSampleTime();
                if (end < 0 || info.presentationTimeUs < end) {
                    info.offset = 0;
                    info.flags = extractor.getSampleFlags();
                    mediaMuxer.writeSampleData(muxerTrackIndexAudio, buffer, info);
                    extractor.advance();
                } else {
                    extractor.unselectTrack(extractTrackIndexAudio);
                    break;
                }
            } else {
                extractor.unselectTrack(extractTrackIndexAudio);
                break;
            }
        }
    }

    /**
     * 压缩视频
     * @param srcPath   原始视频地址
     * @param desPath   目的视频地址
     * @param startTime  开始时间   （小于零表示从零开始）
     * @param endTime    结束时间   （小于零表示源视频总时长）
     * @return true is success, false is failed
     * **/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean compressVideo(final String srcPath, String desPath, long startTime, long endTime) {
        startTime = startTime * 1000 * 1000;
        endTime = endTime * 1000 * 1000;

        int resultWidth = 720;
        int resultHeight = 1080;

        MediaMuxer mediaMuxer = null;
        MediaExtractor extractor = null;
        MediaCodec decoder;
        MediaCodec encoder;
        InputSurface inputSurface;
        OutputSurface outputSurface;

        final int TIMEOUT_USE = 2500;
        boolean outputDone = false;
        boolean inputDone = false;
        boolean decoderDone = false;
        ByteBuffer[] decoderInputBuffers = null;
        ByteBuffer[] encoderOutputBuffers = null;

        try {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            mediaMuxer = new MediaMuxer(desPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            extractor = new MediaExtractor();
            extractor.setDataSource(srcPath);
            int trackNumbers = extractor.getTrackCount();
            for (int i = 0; i < trackNumbers; i++) {
                MediaFormat mediaFormat = extractor.getTrackFormat(i);
                String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("video/")) {
                    resultHeight = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
                    resultWidth = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
                    mimeTypeVideo = mimeType;
                    extractTrackIndexVideo = i;
                } else if (mimeType.startsWith("audio/")) {
                    extractTrackIndexAudio = i;
                    mediaFormatAudio = mediaFormat;
                }
            }

            extractor.selectTrack(extractTrackIndexVideo);
            if (startTime > 0) {
                extractor.seekTo(startTime, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            } else {
                extractor.seekTo(0, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            }
            MediaFormat inputFormat = extractor.getTrackFormat(extractTrackIndexVideo);

            MediaFormat outputFormat = MediaFormat.createVideoFormat(mimeTypeVideo, resultWidth, resultHeight);
            outputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, KEY_COLOR_FORMAT);
            outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
            outputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, KEY_FRAME_RATE);
            outputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, KEY_I_FRAME_INTERVAL);

            encoder = MediaCodec.createEncoderByType(mimeTypeVideo);
            encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = new InputSurface(encoder.createInputSurface());
            inputSurface.makeCurrent();
            encoder.start();

            decoder = MediaCodec.createDecoderByType(inputFormat.getString(MediaFormat.KEY_MIME));
            outputSurface = new OutputSurface();
            decoder.configure(inputFormat, outputSurface.getSurface(), null, 0);
            decoder.start();

            if (Build.VERSION.SDK_INT < 21) {
                decoderInputBuffers = decoder.getInputBuffers();
                encoderOutputBuffers = encoder.getOutputBuffers();
            }

            while (!outputDone) {
                if (!inputDone) {
                    int inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USE);
                    if (inputBufIndex >= 0) {
                        ByteBuffer inputBuf;
                        if (Build.VERSION.SDK_INT < 21) {
                            inputBuf = decoderInputBuffers[inputBufIndex];
                        } else {
                            inputBuf = decoder.getInputBuffer(inputBufIndex);
                        }
                        int sampleSize = extractor.readSampleData(inputBuf, 0);
                        long sampleTime = extractor.getSampleTime();
                        if (sampleSize < 0) {
                            decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            inputDone = true;
                            extractor.unselectTrack(extractTrackIndexVideo);
                        } else {
                            if (endTime < 0 || sampleTime < endTime) {
                                decoder.queueInputBuffer(inputBufIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                                extractor.advance();
                            } else {
                                decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                inputDone = true;
                                extractor.unselectTrack(extractTrackIndexVideo);
                            }
                        }
                    }
                }

                boolean decoderOutputAvailable = !decoderDone;
                boolean encoderOutputAvailable = true;
                while (decoderOutputAvailable || encoderOutputAvailable) {
                    int encoderStatus = encoder.dequeueOutputBuffer(info, TIMEOUT_USE);
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        encoderOutputAvailable = false;
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        if (Build.VERSION.SDK_INT < 21) {
                            encoderOutputBuffers = encoder.getOutputBuffers();
                        }
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newFormat = encoder.getOutputFormat();
                        muxerTrackIndexVideo = mediaMuxer.addTrack(newFormat);
                        muxerTrackIndexAudio = mediaMuxer.addTrack(mediaFormatAudio);
                        mediaMuxer.start();
                    } else if (encoderStatus < 0) {
                        throw new RuntimeException("unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                    } else {
                        ByteBuffer encodedData;
                        if (Build.VERSION.SDK_INT < 21) {
                            encodedData = encoderOutputBuffers[encoderStatus];
                        } else {
                            encodedData = encoder.getOutputBuffer(encoderStatus);
                        }
                        if (encodedData == null) {
                            throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                        }
                        if (info.size > 0) {
                            if (endTime < 0 || info.presentationTimeUs < endTime) {
                                mediaMuxer.writeSampleData(muxerTrackIndexVideo, encodedData, info);
                            }
                        } else {
                            outputDone = true;
                        }
                        encoder.releaseOutputBuffer(encoderStatus, false);
                    }
                    if (encoderStatus != MediaCodec.INFO_TRY_AGAIN_LATER) {
                        continue;
                    }

                    if (!decoderDone) {
                        int decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USE);
                        if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                            decoderOutputAvailable = false;
                        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        } else if (decoderStatus < 0) {
                            throw new RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                        } else {
                            boolean doRender = info.size != 0;
                            decoder.releaseOutputBuffer(decoderStatus, doRender);
                            if (doRender) {
                                boolean errorWait = false;
                                try {
                                    outputSurface.awaitNewImage();
                                } catch (Exception e) {
                                    errorWait = true;
                                }
                                if (!errorWait) {
                                    outputSurface.drawImage(false);
                                    inputSurface.setPresentationTime(info.presentationTimeUs * 1000);
                                    inputSurface.swapBuffers();
                                }
                            }
                            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                decoderOutputAvailable = false;
                                encoder.signalEndOfInputStream();
                            }
                        }
                    }
                }
            }

//            extractor.unselectTrack(extractTrackIndexVideo);

            if (outputSurface != null) {
                outputSurface.release();
            }
            if (inputSurface != null) {
                inputSurface.release();
            }
            if (decoder != null) {
                decoder.stop();
                decoder.release();
            }
            if (encoder != null) {
                encoder.stop();
                encoder.release();
            }

            writeAudioTrack(extractor, mediaMuxer, info, startTime, endTime);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (extractor != null) {
                extractor.release();
            }
            if (mediaMuxer != null) {
                mediaMuxer.stop();
                mediaMuxer.release();
            }
        }
        return true;
    }

    /**
     * 剪切视频
     * @param srcPath   原始视频地址
     * @param desPath   目的视频地址
     * @param startTime  开始时间   （小于零表示从零开始）
     * @param endTime    结束时间   （小于零表示源视频总时长）
     * @return true is success, false is failed
     * **/
    public boolean clipVideo(String srcPath, String desPath, long startTime, long endTime){
        startTime = startTime * 1000 * 1000;
        endTime = endTime * 1000 * 1000;
        MediaExtractor mediaExtractor;
        MediaMuxer mediaMuxer = null;
        MediaFormat mediaFormat;
        int muxerTrackVideo = 0 ;
        int muxerTrackAudio = 0;
        int extractTrackVideo = 0;
        int extractTrackAudio = 0;
        int inputSize = 0;
        mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(srcPath);
            mediaMuxer = new MediaMuxer(desPath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            int trackCount = mediaExtractor.getTrackCount();
            for(int i = 0 ; i< trackCount ; i++){
                mediaFormat = mediaExtractor.getTrackFormat(i);
                String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
                if(mimeType.startsWith("video/")){
                    inputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                    extractTrackVideo = i;
                    muxerTrackVideo = mediaMuxer.addTrack(mediaFormat);
                }else if(mimeType.startsWith("audio/")){
                    extractTrackAudio = i;
                    muxerTrackAudio = mediaMuxer.addTrack(mediaFormat);
                }
            }

            ByteBuffer inputBuffer = ByteBuffer.allocate(inputSize);
            mediaMuxer.setOrientationHint(90);
            mediaMuxer.start();

            MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();
            mediaExtractor.selectTrack(extractTrackVideo);

            if(startTime < 0){
                mediaExtractor.seekTo(0,MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            }else{
                mediaExtractor.seekTo(startTime,MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            }
            while (true){
                int sampleSize = mediaExtractor.readSampleData(inputBuffer,0);
                long sampleTime = mediaExtractor.getSampleTime();
                int sampleFlags = mediaExtractor.getSampleFlags();
                if(sampleSize > 0) {
                    if (sampleTime < endTime || endTime < 0) {
                        videoInfo.offset = 0;
                        videoInfo.size = sampleSize;
                        videoInfo.flags = sampleFlags;
                        videoInfo.presentationTimeUs = sampleTime;
                        mediaMuxer.writeSampleData(muxerTrackVideo, inputBuffer, videoInfo);
                        mediaExtractor.advance();
                    } else {
                        mediaExtractor.unselectTrack(extractTrackVideo);
                        break;
                    }
                }else {
                    mediaExtractor.unselectTrack(extractTrackVideo);
                    break;
                }
            }

            MediaCodec.BufferInfo audioInfo = new MediaCodec.BufferInfo();
            mediaExtractor.selectTrack(extractTrackAudio);
            if(startTime < 0){
                mediaExtractor.seekTo(0,MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            }else{
                mediaExtractor.seekTo(startTime,MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            }
            while (true){
                int sampleSize = mediaExtractor.readSampleData(inputBuffer,0);
                long sampleTime = mediaExtractor.getSampleTime();
                int sampleFlags = mediaExtractor.getSampleFlags();
                if(sampleSize > 0) {
                    if (sampleTime < endTime || endTime < 0) {
                        audioInfo.offset = 0;
                        audioInfo.size = sampleSize;
                        audioInfo.flags = sampleFlags;
                        audioInfo.presentationTimeUs = sampleTime;
                        mediaMuxer.writeSampleData(muxerTrackAudio, inputBuffer, audioInfo);
                        mediaExtractor.advance();
                    } else {
                        mediaExtractor.unselectTrack(extractTrackAudio);
                        break;
                    }
                }else{
                    mediaExtractor.unselectTrack(extractTrackAudio);
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if(mediaMuxer != null){
                mediaMuxer.stop();
                mediaMuxer.release();
            }
            mediaExtractor.release();
        }
        return true;
    }

}