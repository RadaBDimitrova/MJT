package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class Course implements Completable, Purchasable {
    private String name;
    private String description;
    private double price;
    private Resource[] content;
    private Category category;
    private CourseDuration totalTime;
    private boolean purchased;
    private double grade = 0.0;

    public Course(String name, String description, double price, Resource[] content, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.content = content;
        this.category = category;
        totalTime = CourseDuration.of(content);
    }

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
        if (content.length == 0) {
            return 0;
        }
        int sum = 0;
        for (Resource resource : content) {
            if(resource.isCompleted()){
                sum++;
            }
        }
        return BigDecimal.valueOf((sum * 100.0) / content.length).setScale(0, RoundingMode.CEILING).intValue();
    }

    @Override
    public void purchase() {
        purchased = true;
    }

    @Override
    public boolean isPurchased() {
        return purchased;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        if(grade<2.00 || grade>6.00){
            throw new IllegalArgumentException("Invalid grade");
        }
        this.grade = grade;
    }

}
