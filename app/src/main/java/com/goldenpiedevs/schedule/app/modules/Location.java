package com.goldenpiedevs.schedule.app.modules;

public class Location implements Runnable {
    private static String campusNum;
    private static String roomNum;
    String Campus;
    private double latitude;
    private double longitude;

    public String getCampusNum() {
        return campusNum;
    }

    public void setCampusNum(String campusNum) {
        Location.campusNum = campusNum;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        Location.roomNum = roomNum;
    }

    public void geolocation(String Campus) {
        this.Campus = Campus;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void run() {
        if (Campus.length() != 2 && !Campus.equals("24") && !Campus.equals("не вказано")) {
            setCampusNum(Campus.substring(0, Campus.indexOf("-")));
            setRoomNum(Campus.substring((Campus.indexOf("-") + 1), Campus.length()));
        } else if (Campus.equals("не вказано")) {
            setCampusNum("не вказано");
            setRoomNum("0");
        } else {
            setCampusNum(Campus);
            setRoomNum("0");
        }

        if (getCampusNum().matches("\\d+")) {
            switch (Integer.valueOf(getCampusNum())) {
                case 1:
                    setLatitude(50.449309);
                    setLongitude(30.460717);
                    break;
                case 2:
                    setLatitude(50.447868);
                    setLongitude(30.462836);
                    break;
                case 4:
                    setLatitude(50.450877);
                    setLongitude(30.453967);
                    break;
                case 5:
                    setLatitude(50.449023);
                    setLongitude(30.465277);
                    break;
                case 6:
                    setLatitude(50.449603);
                    setLongitude(30.456250);
                    break;
                case 7:
                    setLatitude(50.448558);
                    setLongitude(30.457236);
                    break;
                case 8:
                    setLatitude(50.452107);
                    setLongitude(30.453431);
                    break;
                case 9:
                    setLatitude(50.446971);
                    setLongitude(30.462605);
                    break;
                case 10:
                    setLatitude(50.448999);
                    setLongitude(30.453300);
                    break;
                case 11:
                    setLatitude(50.448999);
                    setLongitude(30.453300);
                    break;
                case 12:
                    setLatitude(50.448023);
                    setLongitude(30.454213);
                    break;
                case 13:
                    setLatitude(50.447787);
                    setLongitude(30.455586);
                    break;
                case 14:
                    setLatitude(50.447794);
                    setLongitude(30.456208);
                    break;
                case 15:
                    setLatitude(50.447789);
                    setLongitude(30.456667);
                    break;
                case 16:
                    setLatitude(50.447643);
                    setLongitude(30.457270);
                    break;
                case 17:
                    setLatitude(50.447639);
                    setLongitude(30.458316);
                    break;
                case 18:
                    setLatitude(50.447215);
                    setLongitude(30.456051);
                    break;
                case 19:
                    setLatitude(50.446835);
                    setLongitude(30.459227);
                    break;
                case 20:
                    setLatitude(50.447073);
                    setLongitude(30.461184);
                    break;
                case 21:
                    setLatitude(50.446814);
                    setLongitude(30.461045);
                    break;
                case 22:
                    setLatitude(50.446108);
                    setLongitude(30.453544);
                    break;
                case 23:
                    setLatitude(50.443119);
                    setLongitude(30.445979);
                    break;
                case 24:
                    setLatitude(50.442645);
                    setLongitude(30.448348);
                    break;
                case 25:
                    setLatitude(50.456732);
                    setLongitude(30.518407);
                    break;
                case 26:
                    setLatitude(50.433789);
                    setLongitude(30.535779);
                    break;
                case 27:
                    setLatitude(50.443331);
                    setLongitude(30.446909);
                    break;
                case 28:
                    setLatitude(50.447147);
                    setLongitude(30.463421);
                    break;
                case 29:
                    setLatitude(50.456413);
                    setLongitude(30.498269);
                    break;
                case 30:
                    setLatitude(50.442854);
                    setLongitude(30.443351);
                    break;
                case 31:
                    setLatitude(50.448115);
                    setLongitude(30.450668);
                    break;
                case 33:
                    setLatitude(50.446199);
                    setLongitude(30.449075);
                    break;
                case 35:
                    setLatitude(50.449667);
                    setLongitude(30.455932);
                    break;
            }
        } else if (getCampusNum().equals("Пол.") && !getRoomNum().contains("ін-т")) {
            setLatitude(50.449112);
            setLongitude(30.452651);
        } else if (getRoomNum().equals("ін-т ім. Амосова")) {
            setLatitude(50.420036);
            setLongitude(30.498677);

        } else if (getRoomNum().equals("ін-т РАКА")) {
            setLatitude(50.389653);
            setLongitude(30.483160);
        } else if (getRoomNum().equals("не вказано")) {

            setLatitude(50.449309);
            setLongitude(30.460717);
        }
    }
}
