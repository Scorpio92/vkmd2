package ru.scorpio92.vkmd2.domain.entity;

public class BroadcastData {

    public static final String BROADCAST_EVENT_PARAM_NAME = "EVENT_NAME";
    public static final String BROADCAST_DATA_PARAM_NAME = "DATA";
    public static final String BROADCAST_DATA_CLASS_PARAM_NAME = "DATA_CLASS";

    private String event;
    private String serializedData;
    private Class dataType;

    public BroadcastData(String event, String serializedData, Class dataType) {
        this.event = event;
        this.serializedData = serializedData;
        this.dataType = dataType;
    }

    public String getEvent() {
        return event;
    }

    public String getSerializedData() {
        return serializedData;
    }

    public Class getDataType() {
        return dataType;
    }
}
