package ru.scorpio92.vkmd2.data.repository.network.base;


import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface API {

    @GET("/audio")
    Single<String> getAccountAudio(@Query("offset") int offset);

    @GET("/audio")
    Single<String> getSearchAudio(@Query("act") String action, @Query("q") String query);

    @GET
    @Streaming
    Call<ResponseBody> downloadTrack(@Url String url);

    @GET
    @Streaming
    Call<ResponseBody> getLastVersion(@Url String url);

    @GET
    @Streaming
    Call<ResponseBody> downloadLastBuild(@Url String url);
}
