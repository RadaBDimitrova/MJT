package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;

public class Resource implements Completable {
    int completion;
    String name;
    ResourceDuration duration;
    public Resource(String name, ResourceDuration duration){
        this.name=name;
        this.duration = duration;
        completion = 0;
    }
    public String getName(){ return name;}


    @Override
    public boolean isCompleted() {
        return completion == 100;
    }

    @Override
    public int getCompletionPercentage() {
        return completion;
    }

    public int getResourceDuration(){ return duration.minutes(); }

    public void complete() {
        completion = 100;
    }
}
