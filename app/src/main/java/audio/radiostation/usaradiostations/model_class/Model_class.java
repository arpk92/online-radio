package audio.radiostation.usaradiostations.model_class;

import java.util.Objects;

public class Model_class {
    String station_name;
    String station_url;
    String station_image;

    public Model_class(String station_name, String station_url, String station_image) {
        this.station_name = station_name;
        this.station_url = station_url;
        this.station_image = station_image;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getStation_url() {
        return station_url;
    }

    public void setStation_url(String station_url) {
        this.station_url = station_url;
    }

    public String getStation_image() {
        return station_image;
    }

    public void setStation_image(String station_image) {
        this.station_image = station_image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Model_class)) return false;
        Model_class that = (Model_class) o;
        return Objects.equals(getStation_name(), that.getStation_name()) &&
                Objects.equals(getStation_url(), that.getStation_url());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStation_name(), getStation_url());
    }
}
