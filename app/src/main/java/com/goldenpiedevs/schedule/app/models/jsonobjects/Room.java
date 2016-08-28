
package com.goldenpiedevs.schedule.app.models.jsonobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Room {

    @SerializedName("room_id")
    @Expose
    public String roomId;
    @SerializedName("room_name")
    @Expose
    public String roomName;
    @SerializedName("room_latitude")
    @Expose
    public String roomLatitude;
    @SerializedName("room_longitude")
    @Expose
    public String roomLongitude;

}
