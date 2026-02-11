package SE2203b.Assignment1.service;

import SE2203b.Assignment1.Domain.Assessment;

import java.util.List;

public class GradeCalculator {
    /**
        GradeCalculator provides static helper methods for computing grade
        summaries from a list of Assessment objects.

        Assessments only contribute if it is marked and not null
     */

    /**
     * totalMarkedWeight: Sums the weights of all marked, non-null assessments
     *
     * @param list the List of assessments to evaluate
     * @return the total weight of marked assessments
     */
    public static double totalMarkedWeight(List<Assessment> list){
        double totalWeight = 0.0;
        for(Assessment a : list)
            if(a.getMarked() && a.getMark() != null)
                totalWeight += a.getWeight();
        return totalWeight;
    }

    /**
     * weightedGradeSoFar: Computes the current weighted grade contribution using only marked assessments.
     * The output is out of 1, not 100
     *
     * @param list the List of assessments to evaluate
     * @return the sum of (mark * weight)/100 for all marked assessments
     */
    public static double weightedGrade(List<Assessment> list) {
        double weightedGrade = 0.0;
        for(Assessment a : list)
            if(a.getMarked() && a.getMark() != null)
                weightedGrade += a.getMark() * a.getWeight();
        return weightedGrade/100;
    }

    /**
     * remainingWeight: computes remaining ungraded weight out of 100
     *
     * @param list the List of assessments to evaluate
     * @return the remaining weight out of 100
     */
    public static double remainingWeight(List<Assessment> list){
        return 100 - totalMarkedWeight(list);
    }

    /**
     * requiredAverage: Computes what average mark is required on remaining assessments to reach a target
     * The output is out of 100
     *
     * @param target desired final grade percentage
     * @param list the List of assessments to evaluate
     * @return required average on remaining assessments
     */
    public static Double requiredAverage(double target, List<Assessment> list){
        return (target- weightedGrade(list))/remainingWeight(list)*100;
    }
}
