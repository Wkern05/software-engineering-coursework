package SE2203b.Assignment1.Domain;

public class Assessment {
    //Stores the basic information for a graded course item
    private String name, type;
    private double weight, mark;
    private boolean marked;

    /**
     * Assessment: Constructs an assessment object
     *
     * @param name name of the assessment
     * @param type type of the assessment (e.g. midterm, lab, quiz)
     * @param weight contribution of the assessment to the final grade
     * @param marked whether or not the assessment has been graded yet
     * @param mark grade received, only considered if marked == true
     */
    public Assessment(String name, String type, double weight, boolean marked, double mark){
        this.name = name;
        this.type = type;
        this.weight = weight;
        this.marked = marked;
        this.mark = mark;
    }

    /**
     * getters and setters for all parameters, no special conditions for any of them
     */

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getType(){return type;}
    public void setType(String type){this.type = type;}

    public double getWeight(){return weight;}
    public void setWeight(double weight){this.weight = weight;}

    public boolean getMarked(){return marked;}
    public void setMarked(boolean marked){this.marked = marked;}

    public double getMark(){return mark;}
    public void setMark(double mark){this.mark = mark;}

}
