package SE2203b.Assignment1.service;

import SE2203b.Assignment1.Domain.Assessment;
import com.vaadin.flow.component.grid.Grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoursePlannerService {
    //in memory storage for all assessments the user has created in the UI
    private final List<Assessment> assessments = new ArrayList<>();

    /**
     * save: Stores an assessment in memory, specifically in the assessments List
     *
     * @param assessment the assessment to be saved.
     */
    public void save(Assessment assessment) { assessments.add(assessment); }

    /**
     * delete: removes an assessment from the stored list
     *
     * @param a the Assessment object to remove
     */
    public void delete(Assessment a) {
        assessments.remove(a);
    }

    /**
     * isNameUnique: Validates that a proposed assessment name is unique (with removed extra white spaces)
     * If the user is editing and existing assessment and keeps the same name, it is allowed.
     *
     * @param name The proposed name
     * @param selected the currently selected assessment (null when no selected assessment)
     * @return true if the name is unique/acceptable, false otherwise
     */
    public boolean isNameUnique(String name, Assessment selected){
        String trimmed = name.trim();

        //Allows you to keep the same name when editing an assessment
        if(selected != null && name.equals(selected.getName()))
            return true;

        //Ensure no other stored assessment has the same name
        return assessments.stream().noneMatch( a ->
                a.getName().trim().equalsIgnoreCase(trimmed));
    }

    /**
     * getAll: Returns the current list of stored assessments.
     *
     * @return the in-memory List of assessments
     */
    public List<Assessment> getAll() {
        return assessments;
    }

}
