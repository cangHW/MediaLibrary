package com.chx.livemaker.util;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/19  10:39
 */
public class Mp4ParserUtils {

    private static final String AUDIO = "soun";
    private static final String VIDEO = "vide";

    /**
     * 对Mp4文件集合进行追加合并(按照顺序一个一个拼接起来)
     *
     * @param mp4PathList [输入]Mp4文件路径的集合(支持m4a)(不支持wav)
     * @param outPutPath  [输出]结果文件全部名称包含后缀(比如.mp4)
     * @throws IOException 格式不支持等情况抛出异常
     */
    public static void appendMp4List(List<String> mp4PathList, String outPutPath) throws IOException {
        ArrayList<Movie> movies = new ArrayList<>();
        for (String s : mp4PathList) {
            movies.add(MovieCreator.build(s));
        }
        LinkedList<Track> audioTrack = new LinkedList<>();
        LinkedList<Track> videoTrack = new LinkedList<>();
        for (Movie movie : movies) {
            for (Track track : movie.getTracks()) {
                if (AUDIO.equals(track.getHandler())) {
                    audioTrack.add(track);
                } else if (VIDEO.equals(track.getHandler())) {
                    videoTrack.add(track);
                }
            }
        }
        Movie resultMovie = new Movie();
        if (!audioTrack.isEmpty()) {
            resultMovie.addTrack(new AppendTrack(audioTrack.toArray(new Track[0])));
        }
        if (!videoTrack.isEmpty()) {
            resultMovie.addTrack(new AppendTrack(videoTrack.toArray(new Track[0])));
        }
        Container container = new DefaultMp4Builder().build(resultMovie);
        FileChannel fileChannel = new RandomAccessFile(outPutPath, "rw").getChannel();
        container.writeContainer(fileChannel);
        fileChannel.close();
    }

    /**
     * 对AAC文件集合进行追加合并(按照顺序一个一个拼接起来)
     *
     * @param aacPathList [输入]AAC文件路径的集合(不支持wav)
     * @param outPutPath  [输出]结果文件全部名称包含后缀(比如.aac)
     * @throws IOException 格式不支持等情况抛出异常
     */
    public static void appendAacList(List<String> aacPathList, String outPutPath) throws IOException {
        LinkedList<Track> audioTracks = new LinkedList<>();
        for (int i = 0; i < aacPathList.size(); i++) {
            audioTracks.add(new AACTrackImpl(new FileDataSourceImpl(aacPathList.get(i))));
        }
        Movie resultMovie = new Movie();
        if (!audioTracks.isEmpty()) {
            resultMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[0])));
        }
        Container container = new DefaultMp4Builder().build(resultMovie);
        FileChannel fileChannel = new RandomAccessFile(outPutPath, "rw").getChannel();
        container.writeContainer(fileChannel);
        fileChannel.close();
    }

    /**
     * 将 AAC 和 MP4 进行混合[替换了视频的音轨]
     *
     * @param aacPath    .aac
     * @param mp4Path    .mp4
     * @param outPutPath .mp4
     */
    public static void muxAacMp4(String aacPath, String mp4Path, String outPutPath) throws IOException {
        AACTrackImpl aacTrack = new AACTrackImpl(new FileDataSourceImpl(aacPath));
        Movie movie = MovieCreator.build(mp4Path);
        Track videoTrack = null;
        for (Track track : movie.getTracks()) {
            if (VIDEO.equals(track.getHandler())) {
                videoTrack = track;
            }
        }
        Movie resultMovie = new Movie();
        if (videoTrack != null) {
            resultMovie.addTrack(videoTrack);
        }
        resultMovie.addTrack(aacTrack);
        Container container = new DefaultMp4Builder().build(resultMovie);
        FileOutputStream outputStream = new FileOutputStream(new File(outPutPath));
        container.writeContainer(outputStream.getChannel());
        outputStream.close();
    }

    /**
     * 将 M4A 和 MP4 进行混合[替换了视频的音轨]
     *
     * @param mp4Path    .m4a[同样可以使用.mp4]
     * @param mp4Path1   .mp4
     * @param outPutPath .mp4
     */
    public static void muxMp4Mp4(String mp4Path, String mp4Path1, String outPutPath) throws IOException {
        Movie audioMovie = MovieCreator.build(mp4Path);
        Track audioTrack = null;
        for (Track track : audioMovie.getTracks()) {
            if (AUDIO.equals(track.getHandler())) {
                audioTrack = track;
            }
        }

        Movie videoMovie = MovieCreator.build(mp4Path1);
        Track videoTrack = null;
        for (Track track : videoMovie.getTracks()) {
            if (VIDEO.equals(track.getHandler())) {
                videoTrack = track;
            }
        }

        Movie resultMovie = new Movie();
        if (audioTrack != null) {
            resultMovie.addTrack(audioTrack);
        }
        if (videoTrack != null) {
            resultMovie.addTrack(videoTrack);
        }
        Container container = new DefaultMp4Builder().build(resultMovie);
        FileOutputStream outputStream = new FileOutputStream(new File(outPutPath));
        container.writeContainer(outputStream.getChannel());
        outputStream.close();
    }

    /**
     * 将 Mp4 的音频和视频分离
     *
     * @param mp4Path    .mp4
     * @param outPutPath .aac
     */
    public static void splitMp4ToAudio(String mp4Path, String outPutPath) throws IOException {
        Movie videoMovie = MovieCreator.build(mp4Path);
        Track audioTrack = null;
        for (Track track : videoMovie.getTracks()) {
            if (AUDIO.equals(track.getHandler())) {
                audioTrack = track;
            }
        }
        Movie resultMovie = new Movie();
        if (audioTrack != null) {
            resultMovie.addTrack(audioTrack);
        }
        Container container = new DefaultMp4Builder().build(resultMovie);
        FileOutputStream outputStream = new FileOutputStream(new File(outPutPath));
        container.writeContainer(outputStream.getChannel());
        outputStream.close();
    }

    /**
     * 将 Mp4 的音频和视频分离
     *
     * @param mp4Path    .mp4
     * @param outPutPath .mp4
     */
    public static void splitMp4ToVideo(String mp4Path, String outPutPath) throws IOException {
        Movie videoMovie = MovieCreator.build(mp4Path);
        Track videoTrack = null;
        for (Track track : videoMovie.getTracks()) {
            if (VIDEO.equals(track.getHandler())) {
                videoTrack = track;
            }
        }
        Movie resultMovie = new Movie();
        if (videoTrack != null) {
            resultMovie.addTrack(videoTrack);
        }
        Container container = new DefaultMp4Builder().build(resultMovie);
        FileOutputStream outputStream = new FileOutputStream(new File(outPutPath));
        container.writeContainer(outputStream.getChannel());
        outputStream.close();
    }

    /**
     * 对 Mp4 添加字幕
     *
     * @param mp4Path    .mp4 添加字幕之前
     * @param outPutPath .mp4 添加字幕之后
     */
    public static void addSubTitle(String mp4Path, String outPutPath) throws IOException {
        Movie videoMovie = MovieCreator.build(mp4Path);

        TextTrackImpl textTrack = new TextTrackImpl();
        textTrack.getTrackMetaData().setLanguage("eng");
        textTrack.getSubs().add(new TextTrackImpl.Line(0, 1000, "as d"));
        textTrack.getSubs().add(new TextTrackImpl.Line(1001, 2000, "sca  aarge  ra"));
        textTrack.getSubs().add(new TextTrackImpl.Line(2001, 3000, "sss  ss"));
        textTrack.getSubs().add(new TextTrackImpl.Line(3001, 4000, "qqq"));
        textTrack.getSubs().add(new TextTrackImpl.Line(4001, 5000, "ww   w"));
        textTrack.getSubs().add(new TextTrackImpl.Line(5001, 6000, "eeeeeeeeee"));
        textTrack.getSubs().add(new TextTrackImpl.Line(6001, 7000, ""));

        videoMovie.addTrack(textTrack);

        Container container = new DefaultMp4Builder().build(videoMovie);
        FileOutputStream outputStream = new FileOutputStream(new File(outPutPath));
        container.writeContainer(outputStream.getChannel());
        outputStream.close();
    }

    /**
     * 将 MP4 切割
     *
     * @param mp4Path    .mp4
     * @param fromSample 起始位置
     * @param toSample   结束位置
     * @param outPath    .mp4
     */
    public static void cropMp4(String mp4Path, long fromSample, long toSample, String outPath) throws IOException {
        Movie videoMovie = MovieCreator.build(mp4Path);
        Track videoTrack = null;
        Track audioTrack = null;
        for (Track track : videoMovie.getTracks()) {
            if (AUDIO.equals(track.getHandler())) {
                audioTrack = track;
            } else if (VIDEO.equals(track.getHandler())) {
                videoTrack = track;
            }
        }
        Movie resultMovie = new Movie();
        if (videoTrack != null) {
            resultMovie.addTrack(new CroppedTrack(videoTrack, fromSample, toSample));
        }
        if (audioTrack != null) {
            resultMovie.addTrack(new CroppedTrack(audioTrack, fromSample, toSample));
        }
        Container container = new DefaultMp4Builder().build(resultMovie);
        FileOutputStream outputStream = new FileOutputStream(new File(outPath));
        container.writeContainer(outputStream.getChannel());
        outputStream.close();
    }

}
