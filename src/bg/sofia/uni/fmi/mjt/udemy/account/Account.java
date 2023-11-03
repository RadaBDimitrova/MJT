package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public interface Account {

    String getUsername();
    void addToBalance(double amount);
    double getBalance();
    void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException;
    void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException;

    void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException;

    /**
     * Gets the course with the least completion percentage.
     * Returns null if the account does not have any purchased courses.
     */
    Course getLeastCompletedCourse();
}
