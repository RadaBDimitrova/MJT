package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotCompletedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class EducationalAccount extends AccountBase {
    private int completed;

    public EducationalAccount(String username, double balance) {
        super(username, balance);
        type = AccountType.EDUCATION;
        completed = 0;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        super.buyCourse(course);
        if (completed == 5) {
            double grade = 0.0;
            int complete = 0;
            for (int i = 0; i < boughtCourses; i++) {
                if (courses[i].getGrade() >= 2.0) {
                    grade += courses[i].getGrade();
                    complete++;
                }
            }
            grade /= complete;
            if (grade >= 4.50) {
                balance += course.getPrice() * type.getDiscount();
                completed = 0;
            }
        }
    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        if (course == null || grade < 2.00 || grade > 6.00) {
            throw new IllegalArgumentException("Grade is not between 2 and 6 or course is null");
        }
        super.completeCourse(course, grade);
        completed++;
    }
}
