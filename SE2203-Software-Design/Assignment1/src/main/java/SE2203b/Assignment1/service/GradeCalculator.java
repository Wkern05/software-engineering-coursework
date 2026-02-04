package SE2203b.Assignment1.service;

import SE2203b.Assignment1.Domain.Assessment;

import java.util.List;

public class GradeCalculator {
    private double totalWeight; //total weight of graded assignments
    private double weightedGrade; // total grade (mark * weight) for all graded assignments

    /**
     * totalMarkedWeight: Computes the total weight of all marked assessments
     *
     * @param list the list of assessments to analyze
     * @return the total weight of marked assessments
     */
    public double totalMarkedWeight(List<Assessment> list){
        totalWeight = 0;
        for(Assessment a : list)
            if(a.getMarked())
                totalWeight += a.getWeight();

        return totalWeight;
    }

    /**
     * weightedGradeSoFar: Computes the weighted grade so far using only marked assessments
     *
     * @param list the list of assessments to analyze
     * @return the sum of (mark * weight) for all marked assessments
     */
    public double weightedGradeSoFar(List<Assessment> list) {
        weightedGrade = 0;
        for(Assessment a : list)
            if(a.getMarked())
                weightedGrade += a.getMark() * a.getWeight();

        return weightedGrade;
    }

    /**
     * remainingWeight: computes how much weight is still ungraded (assumes totalMarkedWeight has already been called)
     *
     * @return the remaining weight out of 100
     */
    public double remainingWeight(){
        return 100 - totalWeight;
    }

    /**
     * requiredAverage: Computes what average mark is required on remaining assessments to reach a target
     *
     * @param target desired final grade percentage
     * @return required average on remaining assessments
     */
    public double requiredAverage(double target){
        return (target-weightedGrade)/remainingWeight()*100;
    }
}
