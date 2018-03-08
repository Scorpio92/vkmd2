package ru.scorpio92.vkmd2.tools;


import com.google.gson.Gson;

public final class JsonWorker {

    public static String getSerializeJson(Object obj){
        return new Gson().toJson(obj);
    }

    public static <T> T getDeserializeJson(String jsonString, Class<T> classOfT) throws IllegalStateException {
        return new Gson().fromJson(jsonString, classOfT);
    }
}