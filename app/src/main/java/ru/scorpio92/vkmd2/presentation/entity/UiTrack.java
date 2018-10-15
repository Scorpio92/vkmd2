package ru.scorpio92.vkmd2.presentation.entity;

import ru.scorpio92.vkmd2.domain.entity.Track;

public class UiTrack extends Track {

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
