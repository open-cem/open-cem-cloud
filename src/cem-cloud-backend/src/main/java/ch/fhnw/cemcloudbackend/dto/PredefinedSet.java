package ch.fhnw.cemcloudbackend.dto;

import java.util.List;

public class PredefinedSet {
    private HeaderInfo headerinfo;
    private List<Step> steps;

    public HeaderInfo getHeaderinfo() {
        return headerinfo;
    }

    public void setHeaderinfo(HeaderInfo headerinfo) {
        this.headerinfo = headerinfo;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}

