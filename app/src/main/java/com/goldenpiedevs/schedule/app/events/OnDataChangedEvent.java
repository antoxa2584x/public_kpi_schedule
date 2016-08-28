package com.goldenpiedevs.schedule.app.events;

import com.goldenpiedevs.schedule.app.models.Weeks;

public class OnDataChangedEvent {

    private Weeks weeks;

    public OnDataChangedEvent(Weeks weeks) {
        this.weeks = weeks;
    }

    public Weeks getWeeks() {
        return weeks;
    }
}
