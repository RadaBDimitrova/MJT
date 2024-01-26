package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public interface Account {

    String getUsername();
    void addToBalance(double amount);
    double getBalance();
    void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException, InsufficientBalanceException;
    void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException;
    void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException;
    Course getLeastCompletedCourse();
}
