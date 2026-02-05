package SE2203b.Assignment1.views;

import SE2203b.Assignment1.Domain.Assessment;
import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class HomeView extends VerticalLayout {
    // Navigation buttons for switching between UI sections
    private Button planner, summary, help;

    // Top menu bar containing the navigation buttons
    private HorizontalLayout menu;

    // Page title and current section indicator
    private H1 title;
    private NativeLabel currentSection;

    // Content area where the current screen gets placed
    private HorizontalLayout content;

    //layouts for the 2 pages and the help popup
    HorizontalLayout plannerLayout;
    VerticalLayout summaryLayout;
    Dialog helpDialog;

    public HomeView() {
        init();

        planner.addClickListener(e -> {
            reset("Planner");
            content.add(plannerLayout);
        });

        summary.addClickListener(e -> {
            reset("Summary");
            content.add(summaryLayout);
        });

        help.addClickListener(e -> {
            helpDialog.open();
        });
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
        planner = new Button("Planner");
        summary = new Button("Summary");
        help = new Button("Help");

        // Top menu bar containing the navigation buttons
        menu = new HorizontalLayout(planner, summary, help);

        // Page title and current section indicator
        currentSection = new NativeLabel("Current section: Planner");
        title = new H1("Planner");

        //main content container that will hold the active screen (Planner, Summary, or help)
        content = new HorizontalLayout();
        content.setWidthFull();

        //add base UI elements to the page
        add(menu, currentSection, title, content);

        //build each UI page
        initPlanner();
        initSummary();
        initHelp();

        content.add(plannerLayout);
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

        //buttons to make the form usable
        Button save = new Button("Save"),
                clear = new Button("Clear"),
                delete = new Button("Delete");

        //place the buttons side by side
        HorizontalLayout buttons = new HorizontalLayout(save, clear, delete);

        //arrange form fields vertically
        VerticalLayout form = new VerticalLayout(name, type, weight, marked, mark, buttons);
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
        plannerLayout = new HorizontalLayout(form, display);
        plannerLayout.setWidthFull();
    }

    /**
     * initSummary: Creates the Summary screen UI, this is where grade totals and required averages are shown
     */
    private void initSummary(){
        NativeLabel total = new NativeLabel("Total marked weight: 0.0%"),
                weightedGrade = new NativeLabel("Weighted grade so far (marked only): 0.0%"),
                remainingWeight = new NativeLabel("Remaining weight to reach 100%%: 0.0%"),
                requiredAverageLabel = new NativeLabel("Required average on remaining: 0.0%");

        NumberField target = new NumberField("Target overall (%)");

        //arranges summary elements vertically
        summaryLayout = new VerticalLayout(total, weightedGrade, remainingWeight, target, requiredAverageLabel);
        summaryLayout.setWidthFull();
    }

    /**
     * initHelp: Creates the Help screen UI, explains how to use the grade calculator
     */
    private void initHelp() {
        helpDialog = new Dialog();
        // Adding a simple help dialog that has a label and a close button
        NativeLabel helpLabel = new NativeLabel("Help"),
                dialogPlanner = new NativeLabel("Planner: add assessments with weights and marks."),
                dialogSummary = new NativeLabel("Summary: see totals and required average for a target grade."),
                dialogRules = new NativeLabel("Rules: total planned weight cannot exceed 100%; names must be unique");

        helpLabel.getStyle().set("font-weight", "bold");

        Button dialogClose = new Button("Close", e -> {
            // close the dialog
            helpDialog.close();
        });
        HorizontalLayout buttonBar = new HorizontalLayout(JustifyContentMode.END, dialogClose);
        buttonBar.setWidthFull();

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(helpLabel, dialogPlanner, dialogSummary, dialogRules, buttonBar);

        // Prevent users from closing the dialog by clicking outside or pressing Escape
        helpDialog.setCloseOnEsc(false);
        helpDialog.setCloseOnOutsideClick(false);
        // Block all background interaction
        helpDialog.setModality(ModalityMode.STRICT);
        helpDialog.add(dialogLayout);
    }
}
