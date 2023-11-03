package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.util.Arrays;

public class Course implements Completable, Purchasable {
    String name;
    String description;
    double price;
    Category category;
    Resource[] content;
    CourseDuration totalTime;
    boolean purchased;
    double grade = 0.0;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public Resource[] getContent() {
        return content;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public CourseDuration getTotalTime() {
        return totalTime;
    }

    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {
        if (resourceToComplete == null) {
            throw new IllegalArgumentException("Null resource to complete.");
        }
        boolean change = false;
        for (Resource resource : content) {
            if (resourceToComplete.equals(resource)) {
                change = true;
                resource.complete();
                break;
            }
        }
        if (!change) {
            throw new ResourceNotFoundException("Resource not found -> not completed.");
        }
    }

    @Override
    public boolean isCompleted() {
        for (Resource resource : content) {
            if (!resource.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCompletionPercentage() {
        int sum = 0;
        for (Resource resource : content) {
            sum += resource.getCompletionPercentage();
        }
        return sum / content.length;
    }

    @Override
    public void purchase() {
        purchased = true;
    }

    @Override
    public boolean isPurchased() {
        return purchased;
    }

    public boolean isEqual(Course course) {
        return this.name.equals(course.getName()) && this.category.equals(course.getCategory()) &&
                this.description.equals(course.getDescription()) && Arrays.equals(this.content, course.getContent()) &&
                this.totalTime.equals(course.getTotalTime()) && this.price == course.getPrice();

    }

    public double getGrade() { return grade;    }
}
