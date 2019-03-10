package simcpux.sourceforge.net.testaudiomv.util;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by cangHX
 * on 2018/11/15  17:10
 */
public class AudioPlayManager {

    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    private static final int SAMPLE_RATE_INHZ = 44100;
    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private AudioTrack mTrack;

    private AudioPlayManager() {
    }

    public static AudioPlayManager create() {
        return new AudioPlayManager();
    }

    public void playStream(final File file) {
        final int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        mTrack = new AudioTrack(
                new AudioAttributes
                        .Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat
                        .Builder()
                        .setChannelMask(CHANNEL_CONFIG)
                        .setEncoding(AUDIO_FORMAT)
                        .setSampleRate(SAMPLE_RATE_INHZ)
                        .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);
        mTrack.play();

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                    byte[] bytes = new byte[minBufferSize];
                    while (inputStream.available() > 0) {
                        int count = inputStream.read(bytes);
                        if (count == AudioTrack.ERROR_INVALID_OPERATION || count == AudioTrack.ERROR_BAD_VALUE) {
                            continue;
                        }
                        if (count > 0) {
                            mTrack.write(bytes, 0, count);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("StaticFieldLeak")
    public void playStatic(File file) {
        final int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        new AsyncTask<File, String, byte[]>() {
            @Override
            protected byte[] doInBackground(File... files) {
                byte[] bytes = new byte[minBufferSize];
                try {
                    try (FileInputStream stream = new FileInputStream(files[0])) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        while (stream.available() > 0) {
                            int count = stream.read(bytes);
                            if (count > 0) {
                                outputStream.write(bytes, 0, count);
                            }
                        }
                        bytes = outputStream.toByteArray();
                    }
                    return bytes;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(byte[] bytes) {
                super.onPostExecute(bytes);
                if (bytes != null) {
                    mTrack = new AudioTrack(
                            new AudioAttributes
                                    .Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build(),
                            new AudioFormat
                                    .Builder()
                                    .setSampleRate(SAMPLE_RATE_INHZ)
                                    .setEncoding(AUDIO_FORMAT)
                                    .setChannelMask(CHANNEL_CONFIG)
                                    .build(),
                            bytes.length,
                            AudioTrack.MODE_STATIC,
                            AudioManager.AUDIO_SESSION_ID_GENERATE);
                    mTrack.write(bytes,0,bytes.length);
                    mTrack.play();
                }
            }
        }.execute(file);
    }

    public void destory(){
        if (mTrack!=null){
            try {
                mTrack.stop();
                mTrack.release();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                mTrack=null;
            }
        }
    }
}
