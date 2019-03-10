package simcpux.sourceforge.net.testaudiomv.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cangHX
 * on 2018/11/14  17:52
 */
public class AudioRecordManager {

    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    private static final int SAMPLE_RATE_INHZ = 44100;
    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord mAudioRecord;
    private final int mBufferSize;
    private byte[] mBuffer;
    private boolean isRecording = false;

    public static AudioRecordManager create() {
        return new AudioRecordManager();
    }

    private AudioRecordManager() {
        mBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, mBufferSize);
        mBuffer = new byte[mBufferSize];
    }

    public void startRecord(final File outFile) {
        if (mAudioRecord == null) {
            return;
        }
        mAudioRecord.startRecording();
        isRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream stream = null;
                try {
                    stream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (stream == null) {
                    return;
                }
                while (isRecording) {
                    int read = mAudioRecord.read(mBuffer, 0, mBuffer.length);
                    if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                        try {
                            stream.write(mBuffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopRecord() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            isRecording = false;
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    public void decoder(final File oldFile, final File newFile) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileInputStream mInputStream = null;
                FileOutputStream mOutputStream = null;
                try {
                    mInputStream = new FileInputStream(oldFile);
                    mOutputStream = new FileOutputStream(newFile);
                    byte[] data = new byte[mBufferSize];

                    long audioChannelCount = mInputStream.getChannel().size();
                    long audioDataLen = audioChannelCount + 36;
                    int channels = CHANNEL_CONFIG == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
                    long byteRates = 16 * SAMPLE_RATE_INHZ * channels / 8;
                    writeHeader(mOutputStream, audioChannelCount, audioDataLen, channels, byteRates, SAMPLE_RATE_INHZ);

                    while (mInputStream.read(data) != -1) {
                        mOutputStream.write(data);
                    }
                    mInputStream.close();
                    mOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void writeHeader(FileOutputStream out, long audioChannelCount, long audioDataLen, int channels, long byteRates, int rate) throws IOException {
        byte[] header = new byte[44];
        // RIFF/WAVE header
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (audioDataLen & 0xff);
        header[5] = (byte) ((audioDataLen >> 8) & 0xff);
        header[6] = (byte) ((audioDataLen >> 16) & 0xff);
        header[7] = (byte) ((audioDataLen >> 24) & 0xff);
        //WAVE
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        // 'fmt ' chunk
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // 4 bytes: size of 'fmt ' chunk
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // format = 1
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (rate & 0xff);
        header[25] = (byte) ((rate >> 8) & 0xff);
        header[26] = (byte) ((rate >> 16) & 0xff);
        header[27] = (byte) ((rate >> 24) & 0xff);
        header[28] = (byte) (byteRates & 0xff);
        header[29] = (byte) ((byteRates >> 8) & 0xff);
        header[30] = (byte) ((byteRates >> 16) & 0xff);
        header[31] = (byte) ((byteRates >> 24) & 0xff);
        // block align
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        // bits per sample
        header[34] = 16;
        header[35] = 0;
        //data
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (audioChannelCount & 0xff);
        header[41] = (byte) ((audioChannelCount >> 8) & 0xff);
        header[42] = (byte) ((audioChannelCount >> 16) & 0xff);
        header[43] = (byte) ((audioChannelCount >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
