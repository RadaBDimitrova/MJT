package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotCompletedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.util.Arrays;

public class EducationalAccount extends AccountBase {

    int completed;

    public EducationalAccount(String username, double balance) {
        super(username, balance);
        type = AccountType.EDUCATION;
        completed = 0;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        super.buyCourse(course);
        if (completed % 6 == 0) {
            double grade = 0.0;
            int completed = 0;
            for (int i = 0; i < boughtCourses; i++) {
                if (courses[i].getGrade() >= 2.0) {
                    grade += courses[i].getGrade();
                    completed++;
                }
            }
            grade /= completed;
            if (grade >= 4.50) {
                balance += course.getPrice() * type.getDiscount();
            }
        }
    }

    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {
        super.completeResourcesFromCourse(course, resourcesToComplete);
    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        if (course == null || grade < 2.00 || grade > 6.00) {
            throw new IllegalArgumentException("Grade is not between 2 and 6 or course is null");
        }
        super.completeCourse(course, grade);
        completed++;
    }

    @Override
    public Course getLeastCompletedCourse() {
        return super.getLeastCompletedCourse();
    }
}
