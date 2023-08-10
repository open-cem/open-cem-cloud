package ch.fhnw.cemcloudbackend.dto;

import java.util.List;

public class Step {
    private int number;
    private String step_type;
    private String name;
    private String image;
    private String description;
    private List<DataItem> data;

    private boolean hide_step;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStep_type() {
        return step_type;
    }

    public void setStep_type(String step_type) {
        this.step_type = step_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DataItem> getData() {
        return data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
    }


    public boolean isHide_step() {
        return hide_step;
    }

    public void setHide_step(boolean hide_step) {
        this.hide_step = hide_step;
    }
}
