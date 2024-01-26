package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotCompletedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

import java.util.Arrays;

public abstract class AccountBase implements Account {
    private String username;
    protected double balance;
    protected Course[] courses;
    protected int boughtCourses;
    private static final int MAX_COURSES = 100;
    protected AccountType type;

    public AccountBase(String username, double balance) {
        this.username = username;
        this.balance = balance;
        this.courses = new Course[MAX_COURSES];
    }
    public int getIdxOfCourse(Course course){
        for (int i = 0; i < courses.length; i++) {
            if(courses[i].isEqual(course)){
                return i;
            }
        }
        return -1;
    }
    public void addToBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Adding negative amount to account:" + username);
        }
        balance += amount;
    }

    public double getBalance() {
        return balance;
    }

    public String getUsername() {
        return username;
    }

    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if (course.getPrice() > balance) {
            throw new InsufficientBalanceException("Buying course failed. Insufficient balance, account:" + username);
        }
        if (boughtCourses >= MAX_COURSES) {
            throw new MaxCourseCapacityReachedException("Buying course failed. Max courses reached, account:" + username);
        }
        for (int i = 0; i < boughtCourses; i++) {
            if (course.isEqual(courses[i])) {
                throw new CourseAlreadyPurchasedException("Buying course failed. Course already purchased, account:" + username);
            }
        }
        courses[boughtCourses] = course;
        courses[boughtCourses++].purchase();
        balance-=course.getPrice();
    }

    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {
        if (course == null || resourcesToComplete == null) {
            throw new IllegalArgumentException("Null course or resources to complete, account" + username);
        }
        if (getIdxOfCourse(course) == -1 || !course.isPurchased()) {
            throw new CourseNotPurchasedException("Course not purchased, account" + username);
        }
        for (Resource value : resourcesToComplete) {
            if (!Arrays.stream(course.getContent()).toList().contains(value)) {
                throw new ResourceNotFoundException("Resource " + value.getName() + " not found in course " + course.getName());
            }
        }
        for (Resource resource : resourcesToComplete) {
            resource.complete();
            course.completeResource(resource);
        }
    }

    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        if (course == null || grade < 2.00 || grade > 6.00) {
            throw new IllegalArgumentException("Grade is not between 2 and 6 or course is null");
        }
        if (getIdxOfCourse(course) == -1 || !course.isPurchased()) {
            throw new CourseNotPurchasedException("Course not purchased, account" + username);
        }
        if (course.isCompleted()) {
            throw new CourseNotCompletedException("Course not purchased, account" + username);
        }
        try {
            completeResourcesFromCourse(courses[getIdxOfCourse(course)], course.getContent());
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
        courses[getIdxOfCourse(course)].setGrade(grade);
    }

    public Course getLeastCompletedCourse() {
        int min = 100;
        int idx = 0;
        for (int i = 0; i < courses.length; i++) {
            if (courses[i].getCompletionPercentage() < min) {
                min = courses[i].getCompletionPercentage();
                idx = i;
            }
        }
        return courses[idx];
    }
}

