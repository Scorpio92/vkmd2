package ru.scorpio92.vkmd2.tools;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.entity.VkTrack;

public class VkmdUtils {

    private final static String VK_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN0PQRSTUVWXYZO123456789+/=";

    public static String decode(String encryptedUrl, int vkID) {
        String[] vals = encryptedUrl.split("\\?extra=")[1].split("#");
        String tstr = vk_o(vals[0]);
        String ops = vk_o(vals[1]);
        String[] ops_arr = ops.split(Character.toString((char) 9));
        for (int i = ops_arr.length - 1; i >= 0; i--) {
            List<String> args_arr = new ArrayList<>(Arrays.asList(ops_arr[i].split(Character.toString((char) 11))));
            String op_ind = args_arr.get(0);
            args_arr.remove(0);
            switch (op_ind) {
                case "v":
                    tstr = vk_v(tstr);
                    break;
                case "r":
                    tstr = vk_r(tstr, Integer.valueOf(args_arr.get(0)));
                    break;
                case "x":
                    tstr = vk_x(tstr, args_arr.get(0));
                    break;
                case "s":
                    tstr = vk_s(tstr, Integer.valueOf(args_arr.get(0)));
                    break;
                case "i":
                    tstr = vk_i(tstr, Integer.valueOf(args_arr.get(0)), vkID);
                    break;
            }
        }
        return tstr;
    }

    private static String vk_o(String str) {
        int i = 0;
        String result = "";
        int index2 = 0;
        for (int s = 0; s < str.length(); s++) {
            int sym_index = VK_STR.indexOf(str.charAt(s));
            if (sym_index != -1) {
                i = ((index2 % 4) != 0) ? ((i << 6) + sym_index) : sym_index;
                if (index2 % 4 != 0) {
                    index2++;
                    int shift = -2 * index2 & 6;
                    result = result.concat(Character.toString((char) (0xFF & (i >> shift))));
                } else {
                    index2++;
                }
            }
        }

        return result;
    }

    private static String vk_v(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    private static String vk_r(String str, int i) {
        String result = "";
        String vk_str2 = VK_STR.concat(VK_STR);
        for (int s = 0; s < str.length(); s++) {
            int index = vk_str2.indexOf(str.charAt(s));
            if (index != -1) {
                int offset = index - i;
                if (offset < 0) {
                    offset += vk_str2.length();
                }
                result = result.concat(String.valueOf(vk_str2.charAt(offset)));
            } else {
                result = result.concat(String.valueOf(str.charAt(s)));
            }
        }
        return result;
    }

    private static String vk_x(String str, String ii) {
        String result = "";
        int xor_val = (char) ii.charAt(0);
        for (int i = 0; i < str.length(); i++) {
            result = result.concat(String.valueOf((char) ((int) (str.charAt(i)) ^ xor_val)));
        }
        return result;
    }

    private static String vk_s(String str, int start) {

        if (str.length() > 0) {
            StringBuilder sb = new StringBuilder(str);

            int cur = Math.abs(start);
            List<Integer> shuffle_pos = new ArrayList<>();
            for (int i = str.length() - 1; i >= 0; i--) {
                cur = ((str.length() * (i + 1)) ^ cur + i) % str.length();
                shuffle_pos.add(cur);
            }
            Collections.reverse(shuffle_pos);
            for (int i = 1; i < str.length(); i++) {
                int offset = shuffle_pos.get(str.length() - i - 1);
                char prev = sb.charAt(i);
                sb.setCharAt(i, sb.charAt(offset));
                sb.setCharAt(offset, prev);
            }

            return sb.toString();
        }

        return str;
    }

    private static String vk_i(String str, int i, int vk_id) {
        return vk_s(str, i ^ vk_id);
    }

    public static List<ru.scorpio92.vkmd2.domain.entity.Track> getTrackListFromPageCode(String sourceCode) {
        String jString = null;

        Pattern p = Pattern.compile("\\[.*https.*jpg\\).*]");
        Matcher m = p.matcher(sourceCode);
        if (m.find()) {
            jString = m.group().replaceAll("^.*cache\":", "") + "}";
        }

        final List<ru.scorpio92.vkmd2.domain.entity.Track> trackList = new ArrayList<>();
        //int idx = 1;

        if (jString != null) {
            try {
                JSONObject JObject = new JSONObject(jString);
                JSONArray array = JObject.names();
                for (int i = 0; i < array.length(); i++) {
                    JSONArray jsonTrack = JObject.getJSONArray(array.get(i).toString());
                    try {
                        String[] tmpArr = jsonTrack.getString(1).split("_");
                        ru.scorpio92.vkmd2.domain.entity.Track track = new ru.scorpio92.vkmd2.domain.entity.Track();
                        track.setUserId(tmpArr[0]);
                        track.setTrackId(tmpArr[1]);
                        track.setArtist(jsonTrack.getString(3));
                        track.setName(jsonTrack.getString(4));
                        track.setDuration(Integer.valueOf(jsonTrack.get(5).toString()));
                        track.setUrlAudio(jsonTrack.getString(2));
                        String imageUrl = jsonTrack.getString(8);
                        if (imageUrl != null && !imageUrl.isEmpty())
                            track.setUrlImage(imageUrl.split("\\(")[1].split("\\)")[0]);
                        else
                            track.setUrlImage("");

                        trackList.add(track);
                        //idx++;
                    } catch (Exception e) {
                        Logger.error(e);
                    }
                }
            } catch (Exception e) {
                Logger.error(e);
            }
        }

        return trackList;
    }


    private static String extractUrl(String text) {
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        if (urlMatcher.find()) {
            return text.substring(urlMatcher.start(0),
                    urlMatcher.end(0));
        }

        return null;
    }

    public static List<ru.scorpio92.vkmd2.domain.entity.Track> getTrackListFromSearchPageCode(String uid, String sourceCode) throws Exception {
        final List<ru.scorpio92.vkmd2.domain.entity.Track> trackList = new ArrayList<>();

        List<String> ids = new ArrayList<>();
        List<String> trackArtists = new ArrayList<>();
        List<String> trackNames = new ArrayList<>();
        List<String> trackDurations = new ArrayList<>();
        List<String> audioUrls = new ArrayList<>();
        //List<String> imageUrls = new ArrayList<>();

        Pattern p = Pattern.compile(".*https.*audio_api_unavailable.*\">");
        Matcher m = p.matcher(sourceCode);
        while (m.find()) {
            audioUrls.add(extractUrl(m.group()));
        }

        p = Pattern.compile("data-id=\"(.*?)_search");
        m = p.matcher(sourceCode);
        while (m.find()) {
            ids.add(m.group(1));
        }

        p = Pattern.compile("ai_artist\">(.*?)</span>");
        m = p.matcher(sourceCode);
        while (m.find()) {
            trackArtists.add(m.group(1).replaceAll("em class=\"found\">", "").replaceAll("</em>", "").replaceAll("<", ""));
        }

        p = Pattern.compile("ai_title\">(.*?)</span>");
        m = p.matcher(sourceCode);
        while (m.find()) {
            if (m.group(1) != null && !m.group(1).isEmpty()) {
                trackNames.add(m.group(1).replaceAll("em class=\"found\">", "").replaceAll("</em>", "").replaceAll("<", ""));
            }
        }

        p = Pattern.compile("data-dur=\"(.*?)\"");
        m = p.matcher(sourceCode);
        while (m.find()) {
            trackDurations.add(m.group(1));
        }

           /* p = Pattern.compile("background-image.*jpg");
            m = p.matcher(sourceCode);
            while (m.find()) {
                imageUrls.add(extractUrl(m.group()));
            }

            Logger.log("ST", "ids.size(): " + ids.size());
            Logger.log("ST", "trackDurations.size(): " + trackDurations.size());
            Logger.log("ST", "audioUrls.size(): " + audioUrls.size());

            Logger.log("ST", "trackArtists.size(): " + trackArtists.size());
            Logger.log("ST", "trackNames.size(): " + trackNames.size());
            Logger.log("ST", "imageUrls.size(): " + imageUrls.size());*/

        int trackCount = audioUrls.size();
        //int idx = 1;

        if (ids.size() == trackCount && trackArtists.size() == trackCount && trackNames.size() == trackCount && trackDurations.size() == trackCount) {
            for (int i = 0; i < trackCount; i++) {
                ru.scorpio92.vkmd2.domain.entity.Track track = new ru.scorpio92.vkmd2.domain.entity.Track();
                //track.setId(idx);
                track.setUserId(uid);
                track.setTrackId(ids.get(i).split("_")[1].trim());
                track.setArtist(trackArtists.get(i));
                track.setName(trackNames.get(i));
                track.setDuration(Integer.valueOf(trackDurations.get(i)));
                track.setUrlAudio(audioUrls.get(i));

                trackList.add(track);
                //idx++;
            }
        }
        return trackList;
    }

    public static Track convertOnlineTrackToBase(OnlineTrack onlineTrack) throws Exception {
        Track track = new Track();
        track.setId(onlineTrack.getId());
        track.setUserId(onlineTrack.getUserId());
        track.setTrackId(onlineTrack.getTrackId());
        track.setArtist(onlineTrack.getArtist());
        track.setName(onlineTrack.getName());
        track.setDuration(onlineTrack.getDuration());
        track.setUrlAudio(onlineTrack.getUrlAudio());
        return track;
    }

    public static Track convertCachedTrackToBase(CachedTrack cachedTrack) throws Exception {
        Track track = new Track();
        track.setId(cachedTrack.getId());
        track.setUserId(cachedTrack.getUserId());
        track.setTrackId(cachedTrack.getTrackId());
        track.setArtist(cachedTrack.getArtist());
        track.setName(cachedTrack.getName());
        track.setDuration(cachedTrack.getDuration());
        track.setUrlAudio(cachedTrack.getUrlAudio());
        track.setSaved(cachedTrack.isSaved());
        track.setSavedPath(cachedTrack.getSavedPath());
        track.setDownloadError(cachedTrack.getStatus() == CachedTrack.TRACK_DOWNLOAD_ERROR);
        return track;
    }

    public static CachedTrack convertOnlineTrackToCached(OnlineTrack onlineTrack) throws Exception {
        CachedTrack track = new CachedTrack();
        track.setId(onlineTrack.getId());
        track.setUserId(onlineTrack.getUserId());
        track.setTrackId(onlineTrack.getTrackId());
        track.setArtist(onlineTrack.getArtist());
        track.setName(onlineTrack.getName());
        track.setDuration(onlineTrack.getDuration());
        track.setUrlAudio(onlineTrack.getUrlAudio());
        return track;
    }

    public static CachedTrack convertTrackToCached(Track track) throws Exception {
        CachedTrack cachedTrack = new CachedTrack();
        cachedTrack.setId(track.getId());
        cachedTrack.setUserId(track.getUserId());
        cachedTrack.setTrackId(track.getTrackId());
        cachedTrack.setArtist(track.getArtist());
        cachedTrack.setName(track.getName());
        cachedTrack.setDuration(track.getDuration());
        cachedTrack.setUrlAudio(track.getUrlAudio());
        return cachedTrack;
    }
}
