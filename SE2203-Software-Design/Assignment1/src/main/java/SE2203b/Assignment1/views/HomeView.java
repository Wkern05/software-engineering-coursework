package SE2203b.Assignment1.views;

import SE2203b.Assignment1.Domain.Assessment;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class HomeView extends VerticalLayout {
    // Navigation buttons for switching between UI sections
    private Button home, profile, help;

    // Top menu bar containing the navigation buttons
    private HorizontalLayout menu;

    // Page title and current section indicator
    private H1 title;
    private NativeLabel currentSection;

    // Content area where the current screen gets placed
    private HorizontalLayout content;

    public HomeView() {
        init();

    }

    /**
     * reset: Updates the title and current section label and clears the content area so a new screen can be displayed
     *
     * @param menuName the screen name to display
     */
    private void reset(String menuName){
        title.setText(menuName);
        currentSection.setText("Current section: " + menuName);
        content.removeAll();
    }

    /**
     * init: initializes the main navigation UI and calls methods to build each screen
     */
    private void init(){
        // Navigation buttons for switching between sections
        home = new Button("Planner");
        profile = new Button("Summary");
        help = new Button("Help");

        // Top menu bar containing the navigation buttons
        menu = new HorizontalLayout(home, profile, help);

        // Page title and current section indicator
        title = new H1("Profile");
        currentSection = new NativeLabel("Current section: Profile");

        //main content container that will hold the active screen (Planner, Summary, or help)
        content = new HorizontalLayout();
        content.setWidthFull();

        //add base UI elements to the page
        add(menu, title, currentSection, content);

        //build each UI page
        initPlanner();
        initSummary();
        initHelp();
    }

    /**
     * initPlanner: Creates the Planner screen UI and adds it to the main UI, includes a form for creating a new
     *  assessment and a grid for viewing all current assessments
     */
    private void initPlanner(){
        //form fields for creating/editiing an assessment
        TextField name = new TextField("Assessment name*");
        ComboBox<String> type = new ComboBox<>("Type");
        NumberField weight = new NumberField("Weight (%)"),
                mark = new NumberField("Mark (%)");
        Checkbox marked = new Checkbox("Marked?");

        //UI sizing so fields look better
        name.setWidth("250px");
        type.setWidth("250px");
        weight.setWidth("250px");
        mark.setWidth("250px");

        //mark is disabled by default, and only enabled when marked is checked
        mark.setEnabled(false);
        //dropdown options for assessment category
        type.setItems("Lab", "Quiz", "Assignment", "Midterm", "Final", "Project", "Other");
        //arrange form fields vertically
        VerticalLayout form = new VerticalLayout(name, type, weight, marked, mark);
        form.setWidth("360px");

        //grid to display assessment objects
        Grid<Assessment> display = new Grid<>();
        display.addColumn(Assessment::getName).setHeader("Name");
        display.addColumn(Assessment::getType).setHeader("Type");
        display.addColumn(Assessment::getWeight).setHeader("Weight (%)");
        display.addColumn(Assessment::getMarked).setHeader("Marked?");
        display.addColumn(Assessment::getMark).setHeader("Mark (%)");

        display.setWidthFull();

        //puts the form on the left and the grid of assessments on the right
        HorizontalLayout plannerLayout = new HorizontalLayout(form, display);
        plannerLayout.setWidthFull();

        //adds this screen to the content area
        content.add(plannerLayout);
    }

    /**
     * initSummary: Creates the Summary screen UI, this is where grade totals and required averages are shown
     */
    private void initSummary(){

    }

    /**
     * initHelp: Creates the Help screen UI, explains how to use the grade calculator
     */
    private void initHelp(){

    }
}
