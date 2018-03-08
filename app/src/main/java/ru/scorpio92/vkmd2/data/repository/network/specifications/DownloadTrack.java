package ru.scorpio92.vkmd2.data.repository.network.specifications;

import ru.scorpio92.vkmd2.data.repository.network.core.RequestSpecification;


public class DownloadTrack extends RequestSpecification {

    public DownloadTrack(String url) {
        super(url);
        setConnectionTimeout(3000);
    }
}
