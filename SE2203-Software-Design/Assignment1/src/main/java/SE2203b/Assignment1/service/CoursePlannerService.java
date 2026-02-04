package SE2203b.Assignment1.service;

import SE2203b.Assignment1.Domain.Assessment;

import java.util.ArrayList;
import java.util.List;

public class CoursePlannerService {
    //in memory storage for all assessments the user has created in the UI
    private List<Assessment> assessments = new ArrayList<>();

    /**
     * save: Creates a new Assessment and stores it in the assessments list
     *
     * @param name name of the assessment
     * @param type type of the assessment (e.g. midterm, lab, quiz)
     * @param weight contribution of the assessment to the final grade
     * @param marked whether or not the assessment has been graded yet
     * @param mark grade received, only considered if marked == true
     */
    public void save(String name, String type, double weight, boolean marked, double mark) {
        assessments.add(new Assessment(name, type, weight, marked, mark));
    }

    /**
     * delete: removes an assessment from the stored list
     *
     * @param a the Assessment object to remove
     */
    public void delete(Assessment a) {
        assessments.remove(a);
    }


}
