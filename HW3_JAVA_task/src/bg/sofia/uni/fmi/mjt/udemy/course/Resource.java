package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;

public class Resource implements Completable{
    private int completion;
    private String name;
    private ResourceDuration duration;
    public Resource(String name, ResourceDuration duration){
        this.name=name;
        this.duration = duration;
        completion = 0;
    }

    public String getName() {
        return name;
    }

    public ResourceDuration getDuration() {
        return duration;
    }

    public void complete() {
        completion = 100;
    }

    @Override
    public boolean isCompleted() {
        return completion==100;
    }

    @Override
    public int getCompletionPercentage() {
        return completion;
    }
}
